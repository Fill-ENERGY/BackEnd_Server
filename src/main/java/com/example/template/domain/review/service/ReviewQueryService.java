package com.example.template.domain.review.service;

import com.example.template.domain.member.entity.Member;
import com.example.template.domain.review.dto.response.ReviewResponseDTO;
import com.example.template.domain.review.entity.Review;

import java.util.List;

public interface ReviewQueryService {
    ReviewResponseDTO.ReviewPreviewListDTO getReviewsOfStations(Member member, Long stationId, Long lastId, String query, int offset);
    ReviewResponseDTO.ReviewPreviewListDTO getReviewsOfUsers(Member member, String query, Long lastId, int offset, String only);
    Review getReview(Long reviewId);
    boolean isExist(Long reviewId);
    boolean isRecommended(Long reviewId, Member member);
}
