package com.example.template.domain.board.dto.request;

import com.example.template.domain.board.entity.Board;
import com.example.template.domain.board.entity.BoardImg;
import com.example.template.domain.board.entity.enums.Category;
import com.example.template.domain.board.entity.enums.HelpStatus;
import com.example.template.domain.member.entity.Member;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BoardRequestDTO {
    @Getter
    public static class CreateBoardDTO {
        @NotNull(message = "제목은 필수입니다.")
        @Size(max = 30, message = "제목은 30자를 초과할 수 없습니다.")
        private String title;

        @NotNull(message = "내용은 필수입니다.")
        private String content;

        @NotNull(message = "카테고리는 필수입니다.")
        private Category category;

        private List<String> images; // 엔티티에 없는 필드, 별도 처리 필요

        public Board toEntity(Member member) {
            HelpStatus helpStatus = this.category == Category.HELP ? HelpStatus.REQUESTED : HelpStatus.NONE;

            return Board.builder()
                    .title(title)
                    .content(content)
                    .category(category)
                    .helpStatus(helpStatus)
                    .member(member)
                    .likeNum(0)
                    .commentCount(0)
                    .images(new ArrayList<>())
                    .build();
        }
    }

    @Getter
    public static class UpdateBoardDTO {
        @NotNull(message = "제목은 필수입니다.")
        @Size(max = 30, message = "제목은 30자를 초과할 수 없습니다.")
        private String title;

        @NotNull(message = "내용은 필수입니다.")
        private String content;

        @NotNull(message = "카테고리는 필수입니다.")
        private Category category;

        private List<String> images; // 엔티티에 없는 필드, 별도 처리 필요
    }

    @Getter
    public static class UpdateBoardStatusDTO {
        @NotNull(message = "상태는 필수 입력값입니다.")
        private HelpStatus helpStatus;
    }
}