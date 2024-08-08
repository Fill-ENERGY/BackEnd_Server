package com.example.template.domain.review.service.impl;

import com.example.template.domain.board.exception.BoardErrorCode;
import com.example.template.domain.board.exception.BoardException;
import com.example.template.domain.member.entity.Member;
import com.example.template.domain.member.repository.MemberRepository;
import com.example.template.domain.review.entity.Review;
import com.example.template.domain.review.enums.SortType;
import com.example.template.domain.review.exception.ReviewErrorCode;
import com.example.template.domain.review.exception.ReviewException;
import com.example.template.domain.review.repository.ReviewRepository;
import com.example.template.domain.review.service.ReviewQueryService;
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
    private final StationRepository stationRepository;
    private final MemberRepository memberRepository;

    @Override
    public List<Review> getReviewsOfStations(Long stationId, Long lastId, String query, int offset) {
        List<Review> result;
        Pageable pageable = PageRequest.of(0, offset);
        // 최신순
        if (query.equalsIgnoreCase(SortType.RECENT.toString())) {
            if (lastId.equals(0L)) {
                result = reviewRepository.findAllByCreatedAtLessThanOrderByCreatedAtDesc(LocalDateTime.now(), pageable).getContent();
            }
            else {
                Review review = reviewRepository.findById(lastId).orElseThrow(() -> new ReviewException(ReviewErrorCode.NOT_FOUND));
                result = reviewRepository.findAllByCreatedAtLessThanOrderByCreatedAtDesc(review.getCreatedAt(), pageable).getContent();
            }
        }
        // 별점순
        else if (query.equalsIgnoreCase(SortType.SCORE.toString())) {
            result = lastId.equals(0L) ? reviewRepository.findAllByOrderByScoreDescIdDesc(pageable).getContent()
                    : reviewRepository.findAllByOrderByScore(lastId, offset);
        }
        else {
            throw new ReviewException(ReviewErrorCode.QUERY_BAD_REQUEST);
        }
        return result;
    }

    @Override
    public List<Review> getReviewsOfUsers() {
        return reviewRepository.findAllByMemberIsOrderByCreatedAtDesc(getMockMember());
    }

    @Override
    public Review getReview(Long reviewId) {
        return reviewRepository.findById(reviewId).orElseThrow(() -> new ReviewException(ReviewErrorCode.NOT_FOUND));
    }

    // TODO : 멤버의 임시 목데이터
    private Member getMockMember() {
        return memberRepository.findById(1L)
                .orElseThrow(() -> new BoardException(BoardErrorCode.MEMBER_NOT_FOUND));
    }
}
