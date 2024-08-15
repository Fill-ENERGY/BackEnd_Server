package com.example.template.domain.review.dto.response;

import com.example.template.domain.member.dto.ProfileResponseDTO;
import com.example.template.domain.review.entity.Keyword;
import com.example.template.domain.review.entity.Review;
import com.example.template.domain.review.entity.ReviewImg;
import com.example.template.domain.review.entity.ReviewKeyword;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

public class ReviewResponseDTO {

    @Getter
    @Builder
    public static class CreateReviewResponseDTO{
        private Long id;
        private LocalDateTime createdAt;

        public static CreateReviewResponseDTO from(Review review) {
            return CreateReviewResponseDTO.builder()
                    .id(review.getId())
                    .createdAt(review.getCreatedAt())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class ReviewPreviewDTO {
        private Long id;
        private String content;
        private Integer recommendationNum;
        private List<KeywordDTO> keywords;
        private List<String> images;
        private double score;
        // TODO: 공용 유저 응답으로 변경 예정
        private ProfileResponseDTO.ProfileDTO member;
        private boolean isRecommended;
        private String username;

        public static ReviewPreviewDTO of(Review review, boolean isRecommended) {
            return ReviewPreviewDTO.builder()
                    .id(review.getId())
                    .content(review.getContent())
                    .recommendationNum(review.getRecommendationNum())
                    .keywords(review.getKeywords().stream().map(ReviewKeyword::getKeyword).map(KeywordDTO::from).toList())
                    .images(review.getImgList().stream().map(ReviewImg::getImgUrl).toList())
                    .score(review.getScore())
                    .member(ProfileResponseDTO.ProfileDTO.from(review.getMember()))
                    .isRecommended(isRecommended)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class KeywordDTO {
        private String name;
        private String content;
        public static KeywordDTO from(Keyword keyword) {
            return KeywordDTO.builder()
                    .name(keyword.name())
                    .content(keyword.getDescription())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class ReviewImgDTO {
        private List<String> images;
    }

}
