package com.example.template.domain.review.repository;

import com.example.template.domain.review.entity.Review;
import com.example.template.domain.review.entity.ReviewImg;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewImgRepository extends JpaRepository<ReviewImg, Long> {
    void deleteAllByReviewIs(Review review);
}
