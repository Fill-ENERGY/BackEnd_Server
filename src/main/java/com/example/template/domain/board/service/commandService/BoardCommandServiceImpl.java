package com.example.template.domain.board.service.commandService;

import com.example.template.domain.board.dto.request.BoardRequestDTO;
import com.example.template.domain.board.dto.response.BoardResponseDTO;
import com.example.template.domain.board.entity.Board;
import com.example.template.domain.board.entity.BoardImg;
import com.example.template.domain.board.entity.BoardLike;
import com.example.template.domain.board.entity.enums.Category;
import com.example.template.domain.board.exception.BoardErrorCode;
import com.example.template.domain.board.exception.BoardException;
import com.example.template.domain.board.repository.BoardImgRepository;
import com.example.template.domain.board.repository.BoardLikeRepository;
import com.example.template.domain.board.repository.BoardRepository;
import com.example.template.domain.member.entity.Member;
import com.example.template.global.config.aws.S3Manager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BoardCommandServiceImpl implements BoardCommandService {

    private final BoardRepository boardRepository;
    private final BoardLikeRepository boardLikeRepository;
    private final BoardImgRepository boardImgRepository;
    private final S3Manager s3Manager;

    @Override
    public BoardResponseDTO.BoardImgDTO uploadBoardImages(List<MultipartFile> images) {
        List<String> keyNames = new ArrayList<>();

        // 키 이름 생성
        for (MultipartFile image : images) {
            if (image != null && !image.isEmpty()) {
                UUID uuid = UUID.randomUUID();
                keyNames.add(s3Manager.generateBoardKeyName(uuid));
            }
        }

        // S3에 파일 일괄 업로드
        List<String> imageUrls = s3Manager.uploadFiles(keyNames, images);

        // BoardImg 엔티티 생성 및 저장 (Board와 연결하지 않음)
        List<BoardImg> boardImgs = imageUrls.stream()
                .map(url -> BoardImg.builder().boardImgUrl(url).build())
                .toList();
        boardImgRepository.saveAll(boardImgs);

        // BoardImgDTO 생성 및 반환
        return BoardResponseDTO.BoardImgDTO.builder()
                .images(imageUrls)
                .build();
    }

    @Override
    public BoardResponseDTO.BoardDTO createBoard(BoardRequestDTO.CreateBoardDTO createBoardDTO, Member member) {
        Board board = createBoardDTO.toEntity(member);

        if (createBoardDTO.getImages() != null && !createBoardDTO.getImages().isEmpty()) {
            List<BoardImg> boardImgs = boardImgRepository.findAllByBoardImgUrlIn(createBoardDTO.getImages());

            // S3에 등록되지 않은 이미지를 가지고 접근
            if (boardImgs.size() != createBoardDTO.getImages().size()) {
                throw new BoardException(BoardErrorCode.INVALID_IMAGE_URLS);
            }

            boardImgs.forEach(img -> img.setBoard(board));
            boardImgRepository.saveAll(boardImgs);
        }

        Board savedBoard = boardRepository.save(board);
        return BoardResponseDTO.BoardDTO.from(savedBoard, member.getId());
    }

    @Override
    public BoardResponseDTO.BoardDTO updateBoard(Long boardId,
                                                 BoardRequestDTO.UpdateBoardDTO updateBoardDTO,
                                                 Member member) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardException(BoardErrorCode.BOARD_NOT_FOUND));
        validateBoardOwnership(board, member);

        // 게시글 기본 정보 업데이트
        board.update(updateBoardDTO.getTitle(), updateBoardDTO.getContent(), updateBoardDTO.getCategory());

        // 이미지 처리
        if (updateBoardDTO.getImages() != null) {
            // 현재 게시글의 이미지 URL 목록
            List<String> currentImageUrls = board.getImages().stream()
                    .map(BoardImg::getBoardImgUrl)
                    .toList();

            // 새로 제공된 이미지 URL 목록
            List<String> newImageUrls = updateBoardDTO.getImages();

            // 제거할 이미지 찾기
            List<BoardImg> imagesToRemove = board.getImages().stream()
                    .filter(img -> !newImageUrls.contains(img.getBoardImgUrl()))
                    .toList();

            // 추가할 이미지 URL 찾기
            List<String> imagesToAdd = newImageUrls.stream()
                    .filter(url -> !currentImageUrls.contains(url))
                    .toList();

            // 이미지 제거
            imagesToRemove.forEach(img -> {
                board.getImages().remove(img);
                boardImgRepository.delete(img); // DB에서 삭제
                s3Manager.deleteFile(img.getBoardImgUrl()); // S3에서도 삭제
            });

            // 새 이미지 추가
            if (!imagesToAdd.isEmpty()) {
                List<BoardImg> newBoardImgs = boardImgRepository.findAllByBoardImgUrlIn(imagesToAdd);

                // S3에 등록되지 않은 이미지를 가지고 접근
                if (newBoardImgs.size() != imagesToAdd.size()) {
                    throw new BoardException(BoardErrorCode.INVALID_IMAGE_URLS);
                }

                newBoardImgs.forEach(img -> img.setBoard(board));
                boardImgRepository.saveAll(newBoardImgs);
            }
        }

        Board updatedBoard = boardRepository.save(board);
        return BoardResponseDTO.BoardDTO.from(updatedBoard, member.getId());
    }

    @Override
    public Long deleteBoard(Long boardId, Member member) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardException(BoardErrorCode.BOARD_NOT_FOUND));
        validateBoardOwnership(board, member);

        // 연관된 이미지 처리
        List<BoardImg> images = board.getImages();
        if (!images.isEmpty()) {
            // S3에서 이미지 파일 일괄 삭제
            List<String> imageUrls = images.stream()
                    .map(BoardImg::getBoardImgUrl)
                    .toList();
            s3Manager.deleteFiles(imageUrls);

            // 데이터베이스에서 BoardImg 엔티티 삭제
            boardImgRepository.deleteAll(images);
        }

        // 게시글 삭제
        boardRepository.delete(board);
        return boardId;
    }

    @Override
    public BoardResponseDTO.BoardStatusDTO updateBoardStatus(Long boardId,
                                                             BoardRequestDTO.UpdateBoardStatusDTO updateBoardStatusDTO,
                                                             Member member) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardException(BoardErrorCode.BOARD_NOT_FOUND));
        if (board.getCategory() != Category.HELP) {
            throw new BoardException(BoardErrorCode.HELP_STATUS_UPDATE_NOT_ALLOWED);
        }
        validateBoardOwnership(board, member);
        board.updateHelpStatus(updateBoardStatusDTO.getHelpStatus());
        return BoardResponseDTO.BoardStatusDTO.from(board);
    }

    // TODO : 트리거 적용 시 하지 않은 Ver
    @Override
    public BoardResponseDTO.BoardLikeDTO addLike(Long boardId, Member member) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardException(BoardErrorCode.BOARD_NOT_FOUND));

        if (boardLikeRepository.existsByMemberAndBoard(member, board)) {
            throw new BoardException(BoardErrorCode.ALREADY_LIKED);
        }

        BoardLike boardLike = BoardLike.builder()
                .board(board)
                .member(member)
                .build();
        boardLikeRepository.save(boardLike);

        board.incrementLikeCount();
        boardRepository.save(board);

        return BoardResponseDTO.BoardLikeDTO.from(board, member.getId());
    }

    @Override
    public BoardResponseDTO.BoardLikeDTO removeLike(Long boardId, Member member) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardException(BoardErrorCode.BOARD_NOT_FOUND));

        BoardLike boardLike = boardLikeRepository.findByMemberAndBoard(member, board)
                .orElseThrow(() -> new BoardException(BoardErrorCode.LIKE_NOT_FOUND));

        boardLikeRepository.delete(boardLike);

        board.decrementLikeCount();
        boardRepository.save(board);

        return BoardResponseDTO.BoardLikeDTO.from(board, member.getId());
    }

    /*
    is_author을 Boolean 값으로 넘겨주어 프론트엔드에서 UI 레벨의 제어를 하지만
    수정, 삭제 등의 민감한 정보를 보호하기 위해 백엔드에서 추가적인 권한 검증을 수행
     */
    private void validateBoardOwnership(Board board, Member currentMember) {
        if (!board.getMember().getId().equals(currentMember.getId())) {
            throw new BoardException(BoardErrorCode.UNAUTHORIZED_BOARD_ACCESS);
        }
    }
}