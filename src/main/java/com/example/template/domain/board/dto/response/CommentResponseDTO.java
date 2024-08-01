package com.example.template.domain.board.dto.response;

import com.example.template.domain.board.entity.Comment;
import com.example.template.domain.board.entity.CommentImg;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

public class CommentResponseDTO {

    @Getter
    @Builder
    public static class CommentDTO {
        private Long id;
        private String content;
        private boolean isSecret;
        private Long memberId;
        private String memberName;
        private LocalDateTime createdAt;
        private List<String> images;

        public static CommentDTO from(Comment comment) {
            return CommentDTO.builder()
                    .id(comment.getId())
                    .content(comment.isSecret() ? "비밀 댓글입니다." : comment.getContent())
                    .isSecret(comment.isSecret())
                    .memberId(comment.getMember().getId())
                    .memberName(comment.getMember().getName())
                    .createdAt(comment.getCreatedAt())
                    .images(comment.getImages().stream().map(CommentImg::getCommentImgUrl).toList())
                    .build();
        }
    }
}
