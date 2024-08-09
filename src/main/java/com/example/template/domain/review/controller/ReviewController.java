package com.example.template.domain.review.controller;

import com.example.template.domain.member.entity.Member;
import com.example.template.domain.review.dto.request.ReviewRequestDTO;
import com.example.template.domain.review.dto.response.ReviewResponseDTO;
import com.example.template.domain.review.entity.Keyword;
import com.example.template.domain.review.entity.Review;
import com.example.template.domain.review.service.KeywordQueryService;
import com.example.template.domain.review.service.ReviewCommandService;
import com.example.template.domain.review.service.ReviewQueryService;
import com.example.template.global.annotation.AuthenticatedMember;
import com.example.template.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reviews")
public class ReviewController {

    private final ReviewQueryService reviewQueryService;
    private final ReviewCommandService reviewCommandService;
    private final KeywordQueryService keywordQueryService;

    @PostMapping
    @Operation(summary = "평가 생성 API", description = "Request Body를 이용하여 새로운 평가를 생성합니다. keywords를 제외하고는 모두 필요합니다.")
    public ApiResponse<ReviewResponseDTO.CreateReviewResponseDTO> createReview(@AuthenticatedMember Member member,
                                                                               @Valid @RequestBody ReviewRequestDTO.CreateReviewRequestDTO request) {
        Review review = reviewCommandService.createReview(member, request);
        return ApiResponse.onSuccess(HttpStatus.CREATED, ReviewResponseDTO.CreateReviewResponseDTO.from(review));
    }

    @GetMapping("/stations/{stationId}")
    @Operation(summary = "충전소 평가 가져오는 API", description = "특정 충전소의 모든 평가를 정렬하고 무한 스크롤 방식으로 페이지네이션하여 반환")
    @Parameters({
            @Parameter(name = "stationId", description = "평가를 가져올 충전소의 ID"),
            @Parameter(name = "query", description = "SCORE: 별점순, RECENT: 최신순"),
            @Parameter(name = "lastId", description = "마지막으로 받은 평가의 id, 처음 가져올 때 -> 0")
    })
    public ApiResponse<List<ReviewResponseDTO.ReviewPreviewDTO>> getReviewsOfStations(@AuthenticatedMember Member member,
                                                                                      @PathVariable Long stationId,
                                                                                      @RequestParam(defaultValue = "0") Long lastId,
                                                                                      @RequestParam(defaultValue = "SCORE") String query,
                                                                                      @RequestParam(defaultValue = "10") int offset) {
        List<Review> reviewList = reviewQueryService.getReviewsOfStations(stationId, lastId, query, offset);
        return ApiResponse.onSuccess(reviewList
                .stream()
                .map(review -> ReviewResponseDTO.ReviewPreviewDTO.of(review, keywordQueryService.getKeywordsOfReview(review.getId()), member, reviewCommandService.isRecommended(review.getId(), member)))
                .toList()
        );
    }

    @GetMapping("/users")
    @Operation(summary = "본인 평가 목록 가져오는 API", description = "로그인된 유저가 작성한 평가 목록 전체 조회")
    public ApiResponse<List<ReviewResponseDTO.ReviewPreviewDTO>> getReviewsOfUsers(@AuthenticatedMember Member member) {
        List<Review> reviewList = reviewQueryService.getReviewsOfUsers(member);
        return ApiResponse.onSuccess(reviewList
                .stream()
                .map(review -> ReviewResponseDTO.ReviewPreviewDTO.of(review, keywordQueryService.getKeywordsOfReview(review.getId()), member, reviewCommandService.isRecommended(review.getId(), member)))
                .toList()
        );
    }

    @GetMapping("/{reviewId}")
    @Operation(summary = "평가 하나의 정보 가져오는 API", description = "특정 평가 하나 조회")
    public ApiResponse<ReviewResponseDTO.ReviewPreviewDTO> getReview(@AuthenticatedMember Member member,
                                                                     @PathVariable Long reviewId) {
        Review review = reviewQueryService.getReview(reviewId);
        return ApiResponse.onSuccess(ReviewResponseDTO.ReviewPreviewDTO.of(review, keywordQueryService.getKeywordsOfReview(reviewId), member, reviewCommandService.isRecommended(review.getId(), member)));
    }

    @GetMapping("/keywords")
    @Operation(summary = "평가의 키워드 가져오는 API", description = "평가 키워드 전부 가져오기")
    public ApiResponse<List<ReviewResponseDTO.KeywordDTO>> getKeywords() {
        List<Keyword> keywords = keywordQueryService.getKeywords();
        return ApiResponse.onSuccess(keywords.stream().map(ReviewResponseDTO.KeywordDTO::from).toList());
    }

    @PatchMapping("/{reviewId}")
    @Operation(summary = "평가 수정 API", description = "평가 하나 수정하기")
    public ApiResponse<ReviewResponseDTO.ReviewPreviewDTO> updateReview(@AuthenticatedMember Member member,
                                                                        @PathVariable Long reviewId,
                                                                        @RequestBody ReviewRequestDTO.UpdateReviewRequestDTO request) {
        Review review = reviewCommandService.updateReview(reviewId, request);
        return ApiResponse.onSuccess(
                ReviewResponseDTO.ReviewPreviewDTO.of(review, keywordQueryService.getKeywordsOfReview(reviewId), member, reviewCommandService.isRecommended(review.getId(), member))
        );
    }

    @DeleteMapping("/{reviewId}")
    @Operation(summary = "평가 삭제 API", description = "평가 하나 삭제하기")
    public ApiResponse<Long> deleteReview(@PathVariable Long reviewId) {
        Long id = reviewCommandService.deleteReview(reviewId);
        return ApiResponse.onSuccess(id);
    }

    @PostMapping("/{reviewId}")
    @Operation(summary = "평가 추천 API", description = "평가 추천 및 추천 취소 API")
    public ApiResponse<Boolean> recommendReview(@AuthenticatedMember Member member, @PathVariable Long reviewId) {
        return ApiResponse.onSuccess(reviewCommandService.recommendReview(member, reviewId));
    }
}
