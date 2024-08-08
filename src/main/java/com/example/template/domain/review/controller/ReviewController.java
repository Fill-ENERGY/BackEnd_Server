package com.example.template.domain.review.controller;

import com.example.template.domain.member.entity.Member;
import com.example.template.domain.member.exception.MemberErrorCode;
import com.example.template.domain.member.exception.MemberException;
import com.example.template.domain.member.jwt.userdetails.PrincipalDetails;
import com.example.template.domain.member.repository.MemberRepository;
import com.example.template.domain.review.dto.request.ReviewRequestDTO;
import com.example.template.domain.review.dto.response.ReviewResponseDTO;
import com.example.template.domain.review.entity.Keyword;
import com.example.template.domain.review.entity.Review;
import com.example.template.domain.review.service.KeywordQueryService;
import com.example.template.domain.review.service.ReviewCommandService;
import com.example.template.domain.review.service.ReviewQueryService;
import com.example.template.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reviews")
public class ReviewController {

    private final ReviewQueryService reviewQueryService;
    private final ReviewCommandService reviewCommandService;
    private final KeywordQueryService keywordQueryService;
    // TODO: 수정 필요
    private final MemberRepository memberRepository;

    @PostMapping
    @Operation(summary = "평가 생성 API", description = "리뷰 생성하는 API")
    public ApiResponse<ReviewResponseDTO.CreateReviewResponseDTO> createReview(@Valid @RequestBody ReviewRequestDTO.CreateReviewRequestDTO request) {
        Review review = reviewCommandService.createReview(request);
        return ApiResponse.onSuccess(HttpStatus.CREATED, ReviewResponseDTO.CreateReviewResponseDTO.from(review));
    }

    @GetMapping("/stations/{stationId}")
    @Operation(summary = "충전소 평가 가져오는 API", description = "특정 충전소의 평가 전체 조회")
    @Parameters({
            @Parameter(name = "query", description = "SCORE: 별점순, RECENT: 최신순"),
            @Parameter(name = "lastId", description = "마지막으로 받은 평가의 id, 처음 가져올 때 -> 0")
    })
    public ApiResponse<List<ReviewResponseDTO.ReviewPreviewDTO>> getReviewsOfStations(@PathVariable Long stationId,
                                                                                      @RequestParam Long lastId,
                                                                                      @RequestParam String query,
                                                                                      @RequestParam(defaultValue = "10") int offset) {
        List<Review> reviewList = reviewQueryService.getReviewsOfStations(stationId, lastId, query, offset);
        Member member = getMember();
        return ApiResponse.onSuccess(reviewList
                .stream()
                .map(review -> ReviewResponseDTO.ReviewPreviewDTO.of(review, keywordQueryService.getKeywordsOfReview(review.getId()), member, reviewCommandService.isRecommended(review.getId(), member)))
                .toList()
        );
    }

    @GetMapping("/users")
    @Operation(summary = "본인 평가 목록 가져오는 API", description = "로그인된 유저의 평가목록 가져오기")
    public ApiResponse<List<ReviewResponseDTO.ReviewPreviewDTO>> getReviewsOfUsers() {
        List<Review> reviewList = reviewQueryService.getReviewsOfUsers();
        Member member = getMember();
        return ApiResponse.onSuccess(reviewList
                .stream()
                .map(review -> ReviewResponseDTO.ReviewPreviewDTO.of(review, keywordQueryService.getKeywordsOfReview(review.getId()), member, reviewCommandService.isRecommended(review.getId(), member)))
                .toList()
        );
    }

    @GetMapping("/{reviewId}")
    @Operation(summary = "평가 하나의 정보 가져오는 API", description = "특정 평가 하나 조회")
    public ApiResponse<ReviewResponseDTO.ReviewPreviewDTO> getReview(@PathVariable Long reviewId) {
        Review review = reviewQueryService.getReview(reviewId);
        Member member = getMember();
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
    public ApiResponse<ReviewResponseDTO.ReviewPreviewDTO> updateReview(@PathVariable Long reviewId,
                                                                        @RequestBody ReviewRequestDTO.UpdateReviewRequestDTO request) {
        Review review = reviewCommandService.updateReview(reviewId, request);
        Member member = getMember();
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
    public ApiResponse<Boolean> recommendReview(@PathVariable Long reviewId) {
        return ApiResponse.onSuccess(reviewCommandService.recommendReview(reviewId));
    }

    // TODO: 수정 필요
    private Member getMember() {
        PrincipalDetails details = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return memberRepository.findByEmail(details.getUsername()).orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
    }
}
