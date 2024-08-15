package com.example.template.domain.board.dto.response;

import com.example.template.domain.board.entity.Comment;
import com.example.template.domain.board.entity.CommentImg;
import com.example.template.domain.member.entity.Member;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class CommentResponseDTO {

    private static final String DELETED_COMMENT_MESSAGE = "댓글이 삭제되었습니다.";
    private static final String SECRET_COMMENT_MESSAGE = "비밀 댓글입니다.";
    private static final String DELETED_MEMBER_NAME = "(삭제)";

    @Getter
    @Builder
    public static class CommentImgDTO {
        private List<String> images;
    }

    @Getter
    @Builder
    public static class CommentDTO {
        private Long id;
        private String content;
        private boolean secret;
        private Long memberId;
        private String memberName;
        private LocalDateTime createdAt;
        private List<String> images;
        private boolean deleted;
        private Long parentId;
        // 현재 사용자가 댓글 작성자인지 여부
        private boolean author;           // true인 경우: 댓글 수정/삭제 옵션을 표시
        // 현재 사용자가 비밀 댓글을 볼 수 있는지 여부 (게시글 작성자 or 댓글본인)
        private boolean canViewSecret;    // true인 경우: 비밀 댓글의 실제 내용을 표시
        private List<CommentDTO> replies;

        public static CommentDTO from(Comment comment, Member currentMember) {
            boolean isCommentAuthor = comment.getMember().getId().equals(currentMember.getId());
            boolean isBoardAuthor = comment.getBoard().getMember().getId().equals(currentMember.getId());
            boolean canViewSecret = isCommentAuthor || isBoardAuthor;

            return CommentDTO.builder()
                    .id(comment.getId())
                    // 부모 댓글이 있는 경우 부모 댓글의 ID를 설정
                    .parentId(comment.getParent() != null ? comment.getParent().getId() : null)
                    .content(getCommentContent(comment, canViewSecret)).secret(comment.isSecret())
                    // 삭제된 댓글인 경우 작성자 ID를 null로 설정
                    .memberId(comment.isDeleted() ? null : comment.getMember().getId())
                    // 삭제된 댓글인 경우 작성자 이름을 "(삭제)"로 표시
                    .memberName(comment.isDeleted() ? DELETED_MEMBER_NAME : comment.getMember().getName())
                    .createdAt(comment.getCreatedAt())
                    // 삭제된 댓글인 경우 이미지 목록을 비움, 그렇지 않으면 이미지 URL 목록 생성
                    .images(comment.isDeleted() ? Collections.emptyList() :
                            comment.getImages().stream().map(CommentImg::getCommentImgUrl).toList())
                    .deleted(comment.isDeleted())
                    // 현재 사용자가 댓글 작성자이고 댓글이 삭제되지 않았을 경우 true
                    .author(isCommentAuthor && !comment.isDeleted())
                    // 비밀 댓글을 볼 수 있는 권한이 있고 댓글이 삭제되지 않았을 경우 true
                    .canViewSecret(canViewSecret && !comment.isDeleted())
                    // comment.getChildren()이 null일 경우 빈 리스트를 반환하고,
                    // null이 아닐 경우 모든 자식 댓글을 CommentDTO로 변환
                    .replies(Optional.ofNullable(comment.getChildren())
                            .map(children -> children.stream()
                                    .map(childComment -> CommentDTO.from(childComment, currentMember))
                                    .toList())
                            .orElse(Collections.emptyList()))
                    .build();
        }

        /**
         * 댓글의 내용을 결정하는 메서드입니다.
         * 이 메서드는 댓글의 상태(삭제 여부, 비밀 여부)와 사용자의 권한에 따라 적절한 내용을 반환합니다.
         *
         * @param comment 내용을 결정할 댓글 객체
         * @param canViewSecret 현재 사용자가 비밀 댓글을 볼 수 있는 권한이 있는지 여부
         * @return 결정된 댓글 내용
         *
         * 동작 방식:
         * 1. 댓글이 삭제된 경우, 삭제되었다는 메시지를 반환합니다.
         * 2. 댓글이 비밀 댓글이고 사용자가 볼 수 있는 권한이 없는 경우, 비밀 댓글이라는 메시지를 반환합니다.
         * 3. 그 외의 경우, 원래의 댓글 내용을 반환합니다.
         */
        private static String getCommentContent(Comment comment, boolean canViewSecret) {
            if (comment.isDeleted()) {
                return DELETED_COMMENT_MESSAGE;
            }
            if (comment.isSecret() && !canViewSecret) {
                return SECRET_COMMENT_MESSAGE;
            }
            return comment.getContent();
        }
    }

    @Getter
    @Builder
    public static class CommentsListDTO {
        private List<CommentDTO> comments;

        public static CommentsListDTO of(List<Comment> comments, Member currentMember) {
            List<CommentDTO> commentDTOList = comments.stream()
                    .filter(comment -> comment.getParent() == null)  // 최상위 댓글만 선택
                    .map(comment -> CommentDTO.from(comment, currentMember))
                    .toList();

            return CommentsListDTO.builder()
                    .comments(commentDTOList)
                    .build();
        }
    }
}
