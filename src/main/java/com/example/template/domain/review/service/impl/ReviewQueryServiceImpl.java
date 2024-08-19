package com.example.template.domain.review.service.impl;

import com.example.template.domain.member.entity.Member;
import com.example.template.domain.review.dto.response.ReviewResponseDTO;
import com.example.template.domain.review.entity.Review;
import com.example.template.domain.review.enums.ContainsQuery;
import com.example.template.domain.review.enums.SortType;
import com.example.template.domain.review.exception.ReviewErrorCode;
import com.example.template.domain.review.exception.ReviewException;
import com.example.template.domain.review.repository.ReviewRecommendRepository;
import com.example.template.domain.review.repository.ReviewRepository;
import com.example.template.domain.review.service.ReviewQueryService;
import com.example.template.domain.station.entity.Station;
import com.example.template.domain.station.exception.StationErrorCode;
import com.example.template.domain.station.exception.StationException;
import com.example.template.domain.station.repository.StationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewQueryServiceImpl implements ReviewQueryService {

    private final ReviewRepository reviewRepository;
    private final ReviewRecommendRepository reviewRecommendRepository;
    private final StationRepository stationRepository;

    @Override
    public ReviewResponseDTO.ReviewPreviewListDTO getReviewsOfStations(Member member, Long stationId, Long lastId, String query, int offset) {
        List<Review> result;
        Pageable pageable = PageRequest.of(0, offset + 1);
        Station station = stationRepository.findById(stationId).orElseThrow(() -> new StationException(StationErrorCode.NOT_FOUND));
        // 최신순
        if (query.equalsIgnoreCase(SortType.RECENT.toString())) {
            if (lastId.equals(0L)) {
                result = reviewRepository.findAllByStationIsAndCreatedAtLessThanOrderByCreatedAtDesc(station, LocalDateTime.now(), pageable).getContent();
            }
            else {
                Review review = reviewRepository.findById(lastId).orElseThrow(() -> new ReviewException(ReviewErrorCode.NOT_FOUND));
                result = reviewRepository.findAllByStationIsAndCreatedAtLessThanOrderByCreatedAtDesc(station, review.getCreatedAt(), pageable).getContent();
            }
        }
        // 별점순
        else if (query.equalsIgnoreCase(SortType.SCORE.toString())) {
            result = lastId.equals(0L) ? reviewRepository.findAllByStationIsOrderByScoreDescIdDesc(station, pageable).getContent()
                    : reviewRepository.findAllByOrderByScore(stationId, lastId, offset + 1);
        }
        else {
            throw new ReviewException(ReviewErrorCode.QUERY_BAD_REQUEST);
        }
        return createReviewPreviewListDTO(member, result, offset);
    }

    @Override
    public ReviewResponseDTO.ReviewPreviewListDTO getReviewsOfUsers(Member member, String query, Long lastId, int offset, String only) {
        List<Review> reviews;
        if (only.equalsIgnoreCase(ContainsQuery.PHOTO.toString())) {
            reviews = getReviewsOfUsersOnlyPhoto(member, query, lastId, offset + 1);
        }
        else if (only.equalsIgnoreCase(ContainsQuery.NONE.toString())){
            reviews = getAllReviewsOfUsers(member, query, lastId, offset + 1);
        }
        else {
            throw new ReviewException(ReviewErrorCode.QUERY_BAD_REQUEST);
        }
        return createReviewPreviewListDTO(member, reviews, offset);
    }

    @Override
    public Review getReview(Long reviewId) {
        return reviewRepository.findById(reviewId).orElseThrow(() -> new ReviewException(ReviewErrorCode.NOT_FOUND));
    }

    @Override
    public boolean isExist(Long reviewId) {
        return reviewRepository.existsById(reviewId);
    }

    @Override
    public boolean isRecommended(Long reviewId, Member member) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new ReviewException(ReviewErrorCode.NOT_FOUND));
        return reviewRecommendRepository.existsByReviewIsAndMemberIs(review, member);
    }

    private List<Review> getAllReviewsOfUsers(Member member, String query, Long lastId, int offset) {
        if (query.equalsIgnoreCase(SortType.RECENT.toString())) {
            if (lastId.equals(0L)) {
                return reviewRepository.findAllByMemberIsOrderByCreatedAtDesc(member);
            }
            else {
                Review review = reviewRepository.findById(lastId).orElseThrow(() -> new ReviewException(ReviewErrorCode.NOT_FOUND));
                Pageable pageable = PageRequest.of(0, offset);
                return reviewRepository.findAllByCreatedAtLessThanOrderByCreatedAtDesc(review.getCreatedAt(), pageable).getContent();
            }
        }
        else if (query.equalsIgnoreCase(SortType.RECOMMENDATION.toString())) {
            Pageable pageable = PageRequest.of(0, offset);
            return lastId.equals(0L) ? reviewRepository.findAllByOrderByRecommendationNumDescCreatedAtDesc(pageable).getContent()
                    : reviewRepository.findAllByOrderByRecommendationNumDescCreatedAtDescFromId(lastId, offset);
        }
        else {
            throw new ReviewException(ReviewErrorCode.QUERY_BAD_REQUEST);
        }
    }

    private List<Review> getReviewsOfUsersOnlyPhoto(Member member, String query, Long lastId, int offset) {
        if (query.equalsIgnoreCase(SortType.RECENT.toString())) {
            if (lastId.equals(0L)) {
                return reviewRepository.findAllByCreatedAtLessThanOrderByCreatedAtDescContainsPhoto(LocalDateTime.now(), offset);
            }
            else {
                Review review = reviewRepository.findById(lastId).orElseThrow(() -> new ReviewException(ReviewErrorCode.NOT_FOUND));
                return reviewRepository.findAllByCreatedAtLessThanOrderByCreatedAtDescContainsPhoto(review.getCreatedAt(), offset);
            }
        }
        else if (query.equalsIgnoreCase(SortType.RECOMMENDATION.toString())) {
            return lastId.equals(0L) ? reviewRepository.findAllByOrderByRecommendationNumDescCreatedAtDescContainPhoto(offset)
                    : reviewRepository.findAllByOrderByRecommendationNumDescCreatedAtDescFromIdContainPhoto(lastId, offset);
        }
        else {
            throw new ReviewException(ReviewErrorCode.QUERY_BAD_REQUEST);
        }
    }

    private ReviewResponseDTO.ReviewPreviewListDTO createReviewPreviewListDTO(Member member, List<Review> reviews, int offset) {
        boolean hasNext = reviews.size() > offset;
        Long lastId = null;
        if (hasNext) {
            reviews = reviews.subList(0, reviews.size() - 1);
            lastId = reviews.get(reviews.size() - 1).getId();
        }
        List<ReviewResponseDTO.ReviewPreviewDTO> list = reviews
                .stream()
                .map(review -> ReviewResponseDTO.ReviewPreviewDTO.of(review, isRecommended(review.getId(), member)))
                .toList();

        return ReviewResponseDTO.ReviewPreviewListDTO.of(list, hasNext, lastId);
    }
}
