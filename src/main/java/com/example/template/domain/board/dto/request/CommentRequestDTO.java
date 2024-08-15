package com.example.template.domain.board.dto.request;

import com.example.template.domain.board.entity.Board;
import com.example.template.domain.board.entity.Comment;
import com.example.template.domain.member.entity.Member;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class CommentRequestDTO {
    @Getter
    public static class CreateDTO {
        @NotNull(message = "내용은 필수입니다.")
        private String content;
        @NotNull
        private boolean secret;
        private Long parentCommentId;
        private List<String> images;


        public Comment toEntity(Member member, Board board) {
            return Comment.builder()
                    .content(content)
                    .secret(secret)
                    .member(member)
                    .images(new ArrayList<>())
                    .board(board)
                    .build();
        }
    }

    @Getter
    public static class UpdateDTO {
        @NotNull(message = "내용은 필수입니다.")
        private String content;
        @NotNull
        private boolean isSecret;
        private List<String> images;
    }
}