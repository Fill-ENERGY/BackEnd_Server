package com.example.template.domain.review.entity;

import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
public class ReviewImg {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_img_id", nullable = false)
    private Long id;

    @Column(name = "img_url", nullable = false)
    private String imgUrl; // 사진 경로

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

    public void setReview(Review review) {
        this.review = review;
    }
}
