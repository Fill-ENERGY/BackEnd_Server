package com.example.template.domain.review.repository;

import com.example.template.domain.member.entity.Member;
import com.example.template.domain.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findAllByMemberIsOrderByCreatedAtDesc(Member member);
    Page<Review> findAllByCreatedAtLessThanOrderByCreatedAtDesc(LocalDateTime createdAt, Pageable pageable);

    @Query(value = "SELECT r1.* FROM review r1 JOIN (SELECT r2.review_id, CONCAT(LPAD(r2.score, 10, '0'), LPAD(r2.review_id, 10, '0')) as cursorValue FROM review r2) as cursorTable ON cursorTable.review_id = r1.review_id WHERE cursorValue < (SELECT CONCAT(LPAD(r2.score, 10, '0'), LPAD(r2.review_id, 10, '0')) FROM review r2 WHERE r2.review_id = :reviewId) ORDER BY score DESC, review_id DESC  LIMIT :offset", nativeQuery = true)
    List<Review> findAllByOrderByScore(@Param("reviewId") Long reviewId, @Param("offset") int offset);

    Page<Review> findAllByOrderByScoreDescIdDesc(Pageable pageable);

    @Query(value = "SELECT r1.* FROM review r1 JOIN (SELECT r2.review_id, CONCAT(LPAD(r2.recommendation_num, 10, '0'), LPAD(r2.created_at, 19, '0')) AS cursorValue FROM review r2) AS cursorTable ON cursorTable.review_id = r1.review_id WHERE cursorValue < (SELECT CONCAT(LPAD(r2.recommendation_num, 10, '0'), LPAD(r2.created_at, 19, '0')) FROM review r2 WHERE r2.review_id = :lastId) ORDER BY recommendation_num DESC, created_at DESC LIMIT :offset", nativeQuery = true)
    List<Review> findAllByOrderByRecommendationNumDescCreatedAtDescFromId(@Param("lastId") Long lastId, @Param("offset") int offset);

    Page<Review> findAllByOrderByRecommendationNumDescCreatedAtDesc(Pageable pageable);

    // 사진 있는 평가만 가져오기

    @Query(value = "SELECT r1.* FROM review r1 WHERE EXISTS(SELECT ri.* FROM review_img ri WHERE ri.review_id = r1.review_id) AND r1.created_at < :createdAt ORDER BY created_at DESC LIMIT :offset", nativeQuery = true)
    List<Review> findAllByCreatedAtLessThanOrderByCreatedAtDescContainsPhoto(@Param("createdAt") LocalDateTime createdAt, @Param("offset") int offset);

    @Query(value = "SELECT r1.* FROM review r1 JOIN (SELECT r2.review_id, CONCAT(LPAD(r2.recommendation_num, 10, '0'), LPAD(r2.created_at, 19, '0')) AS cursorValue FROM review r2) AS cursorTable ON cursorTable.review_id = r1.review_id WHERE cursorValue < (SELECT CONCAT(LPAD(r2.recommendation_num, 10, '0'), LPAD(r2.created_at, 19, '0')) FROM review r2 WHERE r2.review_id = :lastId) AND EXISTS(SELECT ri.* FROM review_img ri WHERE ri.review_id = cursorTable.review_id) ORDER BY recommendation_num DESC, created_at DESC LIMIT :offset", nativeQuery = true)
    List<Review> findAllByOrderByRecommendationNumDescCreatedAtDescFromIdContainPhoto(@Param("lastId") Long lastId, @Param("offset") int offset);

    @Query(value = "SELECT r1.* FROM review r1 WHERE EXISTS(SELECT ri.* FROM review_img ri WHERE ri.review_id = r1.review_id) ORDER BY recommendation_num DESC, created_at DESC LIMIT :offset", nativeQuery = true)
    List<Review> findAllByOrderByRecommendationNumDescCreatedAtDescContainPhoto(@Param("offset") int offset);

}
