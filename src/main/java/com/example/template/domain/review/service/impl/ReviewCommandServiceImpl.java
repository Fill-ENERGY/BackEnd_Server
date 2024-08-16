package com.example.template.domain.review.service.impl;

import com.example.template.domain.member.entity.Member;
import com.example.template.domain.review.dto.request.ReviewRequestDTO;
import com.example.template.domain.review.entity.*;
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
import com.example.template.global.config.aws.S3Manager;
import com.example.template.global.util.s3.entity.Uuid;
import com.example.template.global.util.s3.repository.UuidRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewCommandServiceImpl implements ReviewCommandService {

    private final ReviewRepository reviewRepository;
    private final ReviewKeywordRepository reviewKeywordRepository;
    private final ReviewRecommendRepository reviewRecommendRepository;
    private final ReviewImgRepository reviewImgRepository;
    private final StationRepository stationRepository;
    private final UuidRepository uuidRepository;
    private final S3Manager s3Manager;

    @Override
    public Review createReview(Member member, ReviewRequestDTO.CreateReviewRequestDTO request) {

        Station station = stationRepository.findById(request.getStationId()).orElseThrow(() ->
                new StationException(StationErrorCode.NOT_FOUND));
        Review review = reviewRepository.save(request.toReview(member, station));
        if (request.getImgUrls() != null && !request.getImgUrls().isEmpty()) {
            List<ReviewImg> reviewImgList = reviewImgRepository.findAllByImgUrlIn(request.getImgUrls());

            if (reviewImgList.size() != request.getImgUrls().size()) {
                throw new ReviewException(ReviewErrorCode.INVALID_IMG_URL);
            }
            reviewImgList.forEach(reviewImg -> reviewImg.setReview(review));

        }
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
        if (request.getKeywords() != null) {
            request.getKeywords().forEach(keyword -> {
                // 현재 저장된 키워드에 포함되는 경우 list에서 제거 (중복 제거)
                if (keywords.contains(keyword)) {
                    keywords.remove(keyword);
                }
                // 키워드가 저장되지 않은 경우 저장
                else {
                    reviewKeywordRepository.save(ReviewKeyword.builder()
                            .keyword(keyword)
                            .review(review)
                            .build());
                }
            });
        }
        // list에서 제거되지 않은 것들 모두 삭제
        reviewKeywordRepository.deleteAll(keywordList.stream().filter(reviewKeyword -> keywords.contains(reviewKeyword.getKeyword())).toList());

        List<ReviewImg> reviewImgList = new ArrayList<>(reviewImgRepository.findAllByReviewIs(review));
        List<String> removeImg = new ArrayList<>(reviewImgList.stream().map(ReviewImg::getImgUrl).toList());
        List<String> addImg = new ArrayList<>();
        if (request.getImgUrls() != null) {
            request.getImgUrls().forEach(url -> {
                // 현재 저장된 url에 포함되는 경우 제거 (중복 제거)
                if (removeImg.contains(url)) {
                    removeImg.remove(url);
                }
                // 새로 저장할 url 추가
                else {
                    addImg.add(url);
                }
            });
        }

        reviewImgList = reviewImgRepository.findAllByImgUrlIn(addImg);
        if (reviewImgList.size() != addImg.size()) {
            throw new ReviewException(ReviewErrorCode.INVALID_IMG_URL);
        }

        reviewImgList.forEach(reviewImg -> reviewImgRepository.save(ReviewImg.builder()
                            .review(review)
                            .imgUrl(reviewImg.getImgUrl())
                            .build()));

        // 해당하지 않은 url 전체 삭제
        s3Manager.deleteFiles(removeImg);
        reviewImgRepository.deleteAllByImgUrlIn(removeImg);

        return review;
    }

    @Override
    public Review deleteReview(Long reviewId) {

        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new ReviewException(ReviewErrorCode.NOT_FOUND));

        // Hard delete로 구현
        // 리뷰 이미지 삭제
        List<ReviewImg> images = reviewImgRepository.findAllByReviewIs(review);
        if (images != null) {
            s3Manager.deleteFiles(images.stream().map(ReviewImg::getImgUrl).toList());
            reviewImgRepository.deleteAll(images);
        }

        // 리뷰 추천 삭제
        reviewRecommendRepository.deleteAllByReviewIs(review);

        // 리뷰 키워드 삭제
        reviewKeywordRepository.deleteAllByReviewIs(review);

        // review 삭제
        reviewRepository.delete(review);
        return review;
    }

    @Override
    public Review recommendReview(Member member, Long reviewId) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new ReviewException(ReviewErrorCode.NOT_FOUND));
        reviewRecommendRepository.findByMemberIsAndReviewIs(member, review).ifPresentOrElse(
                reviewRecommend -> {
                    reviewRecommendRepository.delete(reviewRecommend);
                    review.decrementLikeCount();
                },
                () -> {
                    reviewRecommendRepository.save(
                            ReviewRecommend.builder()
                                    .review(review)
                                    .member(member)
                                    .build());
                    review.incrementLikeCount();
                }
        );
        // 추천 수는 트리거로
        return review;
    }

    @Override
    public boolean isRecommended(Long reviewId, Member member) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new ReviewException(ReviewErrorCode.NOT_FOUND));
        return reviewRecommendRepository.existsByReviewIsAndMemberIs(review, member);
    }

    @Override
    public List<ReviewImg> uploadImg(List<MultipartFile> images) {
        List<Uuid> uuids = new ArrayList<>();
        List<String> keyNames = new ArrayList<>();

        images.forEach(image -> {
            if (image != null && !image.isEmpty()) {
                String uuid = UUID.randomUUID().toString();
                Uuid savedUuid = Uuid.builder().uuid(uuid).build();
                uuids.add(savedUuid);
                keyNames.add(s3Manager.generateReviewKeyName(savedUuid));
            }
        });

        uuidRepository.saveAll(uuids);

        List<String> imageUrls = s3Manager.uploadFiles(keyNames, images);

        List<ReviewImg> reviewImg = imageUrls.stream().map(url -> ReviewImg.builder().imgUrl(url).build()).toList();
        return reviewImgRepository.saveAll(reviewImg);
    }
}
