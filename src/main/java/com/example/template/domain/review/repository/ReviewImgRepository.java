package com.example.template.domain.review.repository;

import com.example.template.domain.review.entity.Review;
import com.example.template.domain.review.entity.ReviewImg;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewImgRepository extends JpaRepository<ReviewImg, Long> {
    List<ReviewImg> findAllByReviewIs(Review review);
    List<ReviewImg> findAllByImgUrlIn(List<String> imgUrls);
    void deleteAllByImgUrlIn(List<String> imgUrls);
}
