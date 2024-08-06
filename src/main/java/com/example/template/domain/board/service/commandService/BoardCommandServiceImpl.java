package com.example.template.domain.board.service.commandService;

import com.example.template.domain.board.dto.request.BoardRequestDTO;
import com.example.template.domain.board.dto.response.BoardResponseDTO;
import com.example.template.domain.board.entity.Board;
import com.example.template.domain.board.entity.BoardLike;
import com.example.template.domain.board.entity.enums.Category;
import com.example.template.domain.board.exception.BoardErrorCode;
import com.example.template.domain.board.exception.BoardException;
import com.example.template.domain.board.repository.BoardLikeRepository;
import com.example.template.domain.board.repository.BoardRepository;
import com.example.template.domain.member.entity.Member;
import com.example.template.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BoardCommandServiceImpl implements BoardCommandService {

    private final BoardRepository boardRepository;
    private final BoardLikeRepository boardLikeRepository;
    private final MemberRepository memberRepository;

    @Override
    public BoardResponseDTO.BoardDTO createBoard(BoardRequestDTO.CreateBoardDTO createBoardDTO) {
        Member member = getMockMember();
        Board board = createBoardDTO.toEntity(member);
        Board savedBoard = boardRepository.save(board);
        return BoardResponseDTO.BoardDTO.from(savedBoard, member.getId());
    }

    @Override
    public BoardResponseDTO.BoardDTO updateBoard(Long boardId, BoardRequestDTO.UpdateBoardDTO updateBoardDTO) {
        Member member = getMockMember();
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardException(BoardErrorCode.BOARD_NOT_FOUND));
        validateBoardOwnership(board, member);
        board.update(updateBoardDTO.getTitle(), updateBoardDTO.getContent(), updateBoardDTO.getCategory());
        return BoardResponseDTO.BoardDTO.from(board, member.getId());
    }

    @Override
    public Long deleteBoard(Long boardId) {
        Member member = getMockMember();
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardException(BoardErrorCode.BOARD_NOT_FOUND));
        validateBoardOwnership(board, member);
        boardRepository.delete(board);
        return boardId;
    }

    @Override
    public BoardResponseDTO.BoardStatusDTO updateBoardStatus(Long boardId, BoardRequestDTO.UpdateBoardStatusDTO updateBoardStatusDTO) {
        Member member = getMockMember();
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardException(BoardErrorCode.BOARD_NOT_FOUND));
        if (board.getCategory() != Category.HELP) {
            throw new BoardException(BoardErrorCode.HELP_STATUS_UPDATE_NOT_ALLOWED);
        }
        validateBoardOwnership(board, member);
        board.updateHelpStatus(updateBoardStatusDTO.getHelpStatus());
        return BoardResponseDTO.BoardStatusDTO.from(board);
    }

    // TODO : 나중에 트리거 적용 시 고민해야 될 로직
    // 트리거 + 영속성 컨텍스트 생각하면 뺄지 안뺄지 고민해봐야할 듯
    @Override
    public BoardResponseDTO.BoardLikeDTO addLike(Long boardId) {
        Member member = getMockMember();
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
    public BoardResponseDTO.BoardLikeDTO removeLike(Long boardId) {
        Member member = getMockMember();
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardException(BoardErrorCode.BOARD_NOT_FOUND));

        BoardLike boardLike = boardLikeRepository.findByMemberAndBoard(member, board)
                .orElseThrow(() -> new BoardException(BoardErrorCode.LIKE_NOT_FOUND));

        boardLikeRepository.delete(boardLike);

        board.decrementLikeCount();
        boardRepository.save(board);

        return BoardResponseDTO.BoardLikeDTO.from(board, member.getId());
    }

    // TODO : 멤버의 임시 목데이터
    private Member getMockMember() {
        return memberRepository.findById(1L)
                .orElseThrow(() -> new BoardException(BoardErrorCode.MEMBER_NOT_FOUND));
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