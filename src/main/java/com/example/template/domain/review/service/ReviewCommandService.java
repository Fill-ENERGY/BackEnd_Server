package com.example.template.domain.review.service;

import com.example.template.domain.member.entity.Member;
import com.example.template.domain.review.dto.request.ReviewRequestDTO;
import com.example.template.domain.review.entity.Review;

public interface ReviewCommandService {
    Review createReview(ReviewRequestDTO.CreateReviewRequestDTO request);
    Review updateReview(Long reviewId, ReviewRequestDTO.UpdateReviewRequestDTO request);
    Long deleteReview(Long reviewId);
    boolean recommendReview(Long reviewId);
    boolean isRecommended(Long reviewId, Member member);
}
