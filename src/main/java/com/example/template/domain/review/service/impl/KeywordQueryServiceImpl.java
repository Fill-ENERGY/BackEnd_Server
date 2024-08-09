package com.example.template.domain.review.service.impl;

import com.example.template.domain.review.entity.Keyword;
import com.example.template.domain.review.entity.Review;
import com.example.template.domain.review.entity.ReviewKeyword;
import com.example.template.domain.review.exception.ReviewErrorCode;
import com.example.template.domain.review.exception.ReviewException;
import com.example.template.domain.review.repository.ReviewKeywordRepository;
import com.example.template.domain.review.repository.ReviewRepository;
import com.example.template.domain.review.service.KeywordQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KeywordQueryServiceImpl implements KeywordQueryService {

    private final ReviewKeywordRepository reviewKeywordRepository;
    private final ReviewRepository reviewRepository;

    @Override
    public List<Keyword> getKeywords() {
        return Arrays.stream(Keyword.values()).toList();
    }

    @Override
    public List<Keyword> getKeywordsOfReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new ReviewException(ReviewErrorCode.NOT_FOUND));
        return reviewKeywordRepository.findAllByReviewIs(review).stream().map(ReviewKeyword::getKeyword).toList();
    }
}
