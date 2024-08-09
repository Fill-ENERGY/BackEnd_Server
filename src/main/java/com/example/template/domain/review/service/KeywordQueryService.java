package com.example.template.domain.review.service;

import com.example.template.domain.review.entity.Keyword;

import java.util.List;

public interface KeywordQueryService {
    List<Keyword> getKeywords();
    List<Keyword> getKeywordsOfReview(Long reviewId);
}
