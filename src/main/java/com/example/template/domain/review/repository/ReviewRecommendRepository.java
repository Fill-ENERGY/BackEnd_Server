package com.example.template.domain.review.repository;

import com.example.template.domain.member.entity.Member;
import com.example.template.domain.review.entity.Review;
import com.example.template.domain.review.entity.ReviewRecommend;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRecommendRepository extends JpaRepository<ReviewRecommend, Long> {
    Optional<ReviewRecommend> findByMemberIsAndReviewIs(Member member, Review review);
    boolean existsByReviewIsAndMemberIs(Review review, Member member);
    void deleteAllByMemberIs(Member member);
    void deleteAllByReviewIs(Review review);
}
