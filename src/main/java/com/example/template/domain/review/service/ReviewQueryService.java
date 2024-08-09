package com.example.template.domain.review.service;

import com.example.template.domain.member.entity.Member;
import com.example.template.domain.review.entity.Review;

import java.util.List;

public interface ReviewQueryService {
    List<Review> getReviewsOfStations(Long stationId, Long lastId, String query, int offset);
    List<Review> getReviewsOfUsers(Member member);
    Review getReview(Long reviewId);
    boolean isExist(Long reviewId);
}
