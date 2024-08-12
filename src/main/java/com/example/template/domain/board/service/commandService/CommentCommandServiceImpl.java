package com.example.template.domain.board.service.commandService;

import com.example.template.domain.board.dto.request.CommentRequestDTO;
import com.example.template.domain.board.dto.response.CommentResponseDTO;
import com.example.template.domain.board.entity.Board;
import com.example.template.domain.board.entity.Comment;
import com.example.template.domain.board.entity.CommentImg;
import com.example.template.domain.board.exception.BoardErrorCode;
import com.example.template.domain.board.exception.BoardException;
import com.example.template.domain.board.exception.CommentErrorCode;
import com.example.template.domain.board.exception.CommentException;
import com.example.template.domain.board.repository.BoardRepository;
import com.example.template.domain.board.repository.CommentImgRepository;
import com.example.template.domain.board.repository.CommentRepository;
import com.example.template.domain.member.entity.Member;
import com.example.template.global.config.aws.S3Manager;
import com.example.template.global.util.s3.exxception.S3ErrorCode;
import com.example.template.global.util.s3.exxception.S3Exception;
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
public class CommentCommandServiceImpl implements  CommentCommandService{

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private final CommentImgRepository commentImgRepository;
    private final S3Manager s3Manager;

    /**
     * 댓글 이미지를 업로드하는 메서드입니다.
     *
     * @param images 업로드할 이미지 파일 리스트
     * @return 업로드된 이미지의 URL 리스트를 포함한 DTO
     * @throws S3Exception 이미지 업로드에 실패한 경우
     */
    public CommentResponseDTO.CommentImgDTO uploadCommentImages(List<MultipartFile> images) {
        List<String> keyNames = new ArrayList<>();

        for (MultipartFile image : images) {
            if (image != null && !image.isEmpty()) {
                UUID uuid = UUID.randomUUID();
                keyNames.add(s3Manager.generateCommentKeyName(uuid));
            }
        }

        List<String> imageUrls = s3Manager.uploadFiles(keyNames, images);

        List<CommentImg> commentImgs = imageUrls.stream()
                .map(url -> CommentImg.builder().commentImgUrl(url).build())
                .toList();
        commentImgRepository.saveAll(commentImgs);

        return CommentResponseDTO.CommentImgDTO.builder()
                .images(imageUrls)
                .build();
    }

    /**
     * 새로운 댓글을 생성하는 메서드입니다.
     *
     * @param boardId 댓글이 속할 게시글의 ID
     * @param createDTO 생성할 댓글의 정보가 담긴 DTO
     * @param currentMember 댓글을 작성하는 회원
     * @return 생성된 댓글의 DTO
     * @throws BoardException 게시글을 찾을 수 없는 경우
     * @throws CommentException 부모 댓글을 찾을 수 없거나, 중첩 답글이 허용되지 않는 경우, 또는 이미지 URL이 유효하지 않은 경우
     */
    public CommentResponseDTO.CommentDTO createComment(Long boardId, CommentRequestDTO.CreateDTO createDTO, Member currentMember) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardException(BoardErrorCode.BOARD_NOT_FOUND));

        Comment comment = createDTO.toEntity(currentMember, board);

        // 부모 댓글이 있는 경우
        if (createDTO.getParentCommentId() != null) {
            // 부모 댓글이 다른 게시글에 속한 경우
            Comment parentComment = commentRepository.findByIdAndBoardId(createDTO.getParentCommentId(), boardId)
                    .orElseThrow(() -> new CommentException(CommentErrorCode.PARENT_COMMENT_NOT_FOUND));

            // 대댓글에 대한 답글 시도 시
            if (parentComment.getParent() != null) {
                throw new CommentException(CommentErrorCode.NESTED_REPLY_NOT_ALLOWED);
            }

            parentComment.addChild(comment);
        }

        // 이미지 처리
        if (createDTO.getImages() != null && !createDTO.getImages().isEmpty()) {
            List<CommentImg> commentImgs = commentImgRepository.findAllByCommentImgUrlIn(createDTO.getImages());

            if (commentImgs.size() != createDTO.getImages().size()) {
                throw new CommentException(CommentErrorCode.INVALID_IMAGE_URLS);
            }

            commentImgs.forEach(img -> img.setComment(comment));
            commentImgRepository.saveAll(commentImgs);
        }

        Comment savedComment = commentRepository.save(comment);

        // 게시글의 댓글 수 증가
        board.incrementCommentCount();
        boardRepository.save(board);

        return CommentResponseDTO.CommentDTO.from(savedComment, currentMember);
    }

    /**
     * 댓글을 수정하는 메서드입니다.
     *
     * @param boardId 댓글이 속한 게시글의 ID
     * @param commentId 수정할 댓글의 ID
     * @param updateDTO 수정할 내용이 담긴 DTO
     * @param member 수정을 요청한 회원
     * @return 수정된 댓글의 DTO
     * @throws CommentException 댓글을 찾을 수 없거나, 권한이 없는 경우, 댓글이 다른 게시글에 속한 경우, 또는 이미지 URL이 유효하지 않은 경우
     */
    public CommentResponseDTO.CommentDTO updateComment(Long boardId, Long commentId, CommentRequestDTO.UpdateDTO updateDTO, Member member) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException(CommentErrorCode.COMMENT_NOT_FOUND));
        validateCommentOwnership(comment, member);

        if (!comment.getBoard().getId().equals(boardId)) {
            throw new CommentException(CommentErrorCode.COMMENT_BOARD_MISMATCH);
        }

        comment.setContent(updateDTO.getContent());
        comment.setSecret(updateDTO.isSecret());

        if (updateDTO.getImages() != null) {
            List<String> currentImageUrls = comment.getImages().stream()
                    .map(CommentImg::getCommentImgUrl)
                    .toList();

            List<String> newImageUrls = updateDTO.getImages();

            List<CommentImg> imagesToRemove = comment.getImages().stream()
                    .filter(img -> !newImageUrls.contains(img.getCommentImgUrl()))
                    .toList();

            List<String> imagesToAdd = newImageUrls.stream()
                    .filter(url -> !currentImageUrls.contains(url))
                    .toList();

            imagesToRemove.forEach(img -> {
                comment.getImages().remove(img);
                commentImgRepository.delete(img);
                s3Manager.deleteFile(img.getCommentImgUrl());
            });

            if (!imagesToAdd.isEmpty()) {
                List<CommentImg> newCommentImgs = commentImgRepository.findAllByCommentImgUrlIn(imagesToAdd);

                if (newCommentImgs.size() != imagesToAdd.size()) {
                    throw new CommentException(CommentErrorCode.INVALID_IMAGE_URLS);
                }

                newCommentImgs.forEach(img -> img.setComment(comment));
                commentImgRepository.saveAll(newCommentImgs);
            }
        }

        Comment updatedComment = commentRepository.save(comment);
        return CommentResponseDTO.CommentDTO.from(updatedComment, member);
    }

    /**
     * 댓글을 삭제하는 메서드입니다.
     *
     * @param boardId 댓글이 속한 게시글의 ID
     * @param commentId 삭제할 댓글의 ID
     * @param member 삭제를 요청한 회원
     * @return 삭제된 댓글의 ID
     * @throws CommentException 댓글을 찾을 수 없거나, 권한이 없는 경우, 또는 댓글이 다른 게시글에 속한 경우
     */
    @Transactional
    public Long deleteComment(Long boardId, Long commentId, Member member) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException(CommentErrorCode.COMMENT_NOT_FOUND));
        validateCommentOwnership(comment, member);

        if (!comment.getBoard().getId().equals(boardId)) {
            throw new CommentException(CommentErrorCode.COMMENT_BOARD_MISMATCH);
        }

        Board board = comment.getBoard();
        if (comment.isTopLevelComment()) {
            handleTopLevelCommentDeletion(board, comment);
        } else {
            handleReplyDeletion(board, comment);
        }

        return commentId;
    }

    // 최상위 댓글 삭제
    private void handleTopLevelCommentDeletion(Board board, Comment comment) {
        if (comment.hasChildren()) {
            // 3. 대댓글이 있는 최상위 댓글의 경우
            softDeleteComment(comment);
            board.decrementCommentCount();
        } else {
            // 1. 대댓글이 없는 최상위 댓글의 경우
            hardDeleteComment(comment);
            board.decrementCommentCount();
        }
    }

    // 대댓글 삭제
    private void handleReplyDeletion(Board board, Comment comment) {
        // 2. 하위 댓글이 없는 대댓글의 경우
        hardDeleteComment(comment);
        board.decrementCommentCount();

        // 4. 최상위 댓글이 삭제되었고 이것이 마지막 대댓글인 경우
        Comment topLevelComment = comment.getParent();
        // 부모 댓글이 이미 삭제 상태이고, 다른 대댓글이 없는 경우 부모 댓글도 완전히 삭제
        if (topLevelComment != null && topLevelComment.isDeleted()) {
            long remainingReplies = commentRepository.countByParentIdAndIdNot(topLevelComment.getId(), comment.getId());
            if (remainingReplies == 0) {
                hardDeleteComment(topLevelComment);
                board.decrementCommentCount(); // 부모 댓글도 완전히 삭제되므로 추가로 1 감소
            }
        }
    }

    // Soft Delete
    private void softDeleteComment(Comment comment) {
        deleteCommentImages(comment);
        comment.markAsDeleted();
        commentRepository.save(comment);
    }

    // HardDelete
    private void hardDeleteComment(Comment comment) {
        deleteCommentImages(comment);
        commentRepository.delete(comment);
    }

    // 이미지 삭제
    private void deleteCommentImages(Comment comment) {
        for (CommentImg image : comment.getImages()) {
            s3Manager.deleteFile(image.getCommentImgUrl());
        }
        commentImgRepository.deleteAll(comment.getImages());
        comment.getImages().clear();
    }

    /*
    is_author을 Boolean 값으로 넘겨주어 프론트엔드에서 UI 레벨의 제어를 하지만
    수정, 삭제 등의 민감한 정보를 보호하기 위해 백엔드에서 추가적인 권한 검증을 수행
     */
    private void validateCommentOwnership(Comment comment, Member currentMember) {
        if (!comment.getMember().getId().equals(currentMember.getId())) {
            throw new CommentException(CommentErrorCode.NOT_COMMENT_OWNER);
        }
    }
}
