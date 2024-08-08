package com.example.template.domain.review.dto.request;

import com.example.template.domain.member.entity.Member;
import com.example.template.domain.review.entity.Keyword;
import com.example.template.domain.review.entity.Review;
import com.example.template.domain.station.entity.Station;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.List;

public class ReviewRequestDTO {

    @Getter
    public static class CreateReviewRequestDTO {
        @NotBlank(message = "내용이 비어있습니다.")
        @Schema(name = "content", description = "내용", example = "충전소 이용이 편리해요")
        private String content;
        @NotNull(message = "점수가 비어있습니다.")
        @Schema(name = "score", description = "별점", example = "4.5")
        private double score;
        private List<Keyword> keywords;
        @Schema(name = "stationId", description = "리뷰를 달 충전소의 id", example = "1")
        private Long stationId;
        // TODO: 사진 추가

        public Review toReview(Member member, Station station) {
            return Review.builder()
                    .content(this.content)
                    .score(this.score)
                    .recommendationNum(0)
                    .member(member)
                    .station(station)
                    .build();
        }
    }

    @Getter
    public static class UpdateReviewRequestDTO {
        @NotBlank(message = "내용이 비어있습니다.")
        private String content;
        @NotNull(message = "점수가 비어있습니다.")
        private double score;
        private List<Keyword> keywords;
        // TODO: 사진 추가
    }
}
