package com.example.template.domain.review.service.impl;

import com.example.template.domain.member.entity.Member;
import com.example.template.domain.member.repository.MemberRepository;
import com.example.template.domain.review.dto.request.ReviewRequestDTO;
import com.example.template.domain.review.entity.Keyword;
import com.example.template.domain.review.entity.Review;
import com.example.template.domain.review.entity.ReviewKeyword;
import com.example.template.domain.review.entity.ReviewRecommend;
import com.example.template.domain.review.exception.ReviewErrorCode;
import com.example.template.domain.review.exception.ReviewException;
import com.example.template.domain.review.repository.ReviewImgRepository;
import com.example.template.domain.review.repository.ReviewKeywordRepository;
import com.example.template.domain.review.repository.ReviewRecommendRepository;
import com.example.template.domain.review.repository.ReviewRepository;
import com.example.template.domain.review.service.ReviewCommandService;
import com.example.template.domain.station.entity.Station;
import com.example.template.domain.station.exception.StationErrorCode;
import com.example.template.domain.station.exception.StationException;
import com.example.template.domain.station.repository.StationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewCommandServiceImpl implements ReviewCommandService {

    private final ReviewRepository reviewRepository;
    private final ReviewKeywordRepository reviewKeywordRepository;
    private final ReviewRecommendRepository reviewRecommendRepository;
    private final ReviewImgRepository reviewImgRepository;
    private final MemberRepository memberRepository;
    private final StationRepository stationRepository;

    @Override
    public Review createReview(Member member, ReviewRequestDTO.CreateReviewRequestDTO request) {

        Station station = stationRepository.findById(request.getStationId()).orElseThrow(() ->
                new StationException(StationErrorCode.NOT_FOUND));
        Review review = reviewRepository.save(request.toReview(member, station));
        // TODO: 사진 로직 필요
        request.getKeywords().forEach(keyword -> reviewKeywordRepository.save(
                ReviewKeyword.builder()
                        .keyword(keyword)
                        .review(review)
                        .build()
        ));

        return review;
    }

    @Override
    public Review updateReview(Long reviewId, ReviewRequestDTO.UpdateReviewRequestDTO request) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new ReviewException(ReviewErrorCode.NOT_FOUND));
        review.update(request.getContent(), request.getScore());

        List<ReviewKeyword> keywordList = new ArrayList<>(reviewKeywordRepository.findAllByReviewIs(review));
        List<Keyword> keywords = new ArrayList<>(keywordList.stream().map(ReviewKeyword::getKeyword).toList());
        // TODO: 사진 로직 필요
        if (request.getKeywords() != null) {
            request.getKeywords().forEach(keyword -> {
                if (keywords.contains(keyword)) {
                    keywords.remove(keyword);
                } else {
                    reviewKeywordRepository.save(ReviewKeyword.builder()
                            .keyword(keyword)
                            .review(review)
                            .build());
                }
            });
        }

        reviewKeywordRepository.deleteAll(keywordList.stream().filter(reviewKeyword -> keywords.contains(reviewKeyword.getKeyword())).toList());
        return review;
    }

    @Override
    public Long deleteReview(Long reviewId) {

        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new ReviewException(ReviewErrorCode.NOT_FOUND));

        // Hard delete로 구현
        // 리뷰 이미지 삭제
        // TODO: 사진 로직 필요
        reviewImgRepository.deleteAllByReviewIs(review);

        // 리뷰 추천 삭제
        reviewRecommendRepository.deleteAllByReviewIs(review);

        // 리뷰 키워드 삭제
        reviewKeywordRepository.deleteAllByReviewIs(review);

        // review 삭제
        reviewRepository.delete(review);
        return reviewId;
    }

    @Override
    public boolean recommendReview(Member member, Long reviewId) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new ReviewException(ReviewErrorCode.NOT_FOUND));
        reviewRecommendRepository.findByMemberIsAndReviewIs(member, review).ifPresentOrElse(
                reviewRecommendRepository::delete,
                () -> reviewRecommendRepository.save(
                            ReviewRecommend.builder()
                                    .review(review)
                                    .member(member)
                                    .build()
                    )
        );
        // 추천 수는 트리거로
        return true;
    }

    @Override
    public boolean isRecommended(Long reviewId, Member member) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new ReviewException(ReviewErrorCode.NOT_FOUND));
        return reviewRecommendRepository.existsByReviewIsAndMemberIs(review, member);
    }
}
