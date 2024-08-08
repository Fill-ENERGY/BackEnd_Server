package com.example.template.domain.review.service;

import com.example.template.domain.review.entity.Review;

import java.util.List;

public interface ReviewQueryService {
    List<Review> getReviewsOfStations(Long stationId, Long lastId, String query, int offset);
    List<Review> getReviewsOfUsers();
    Review getReview(Long reviewId);
}
