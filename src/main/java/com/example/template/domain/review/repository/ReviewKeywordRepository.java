package com.example.template.domain.review.repository;

import com.example.template.domain.review.entity.Review;
import com.example.template.domain.review.entity.ReviewKeyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewKeywordRepository extends JpaRepository<ReviewKeyword, Long> {
    List<ReviewKeyword> findAllByReviewIs(Review review);
    void deleteAllByReviewIs(Review review);
}
