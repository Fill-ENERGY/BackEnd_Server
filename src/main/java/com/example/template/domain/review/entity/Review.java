package com.example.template.domain.review.entity;

import com.example.template.domain.member.entity.Member;
import com.example.template.domain.station.entity.Station;
import com.example.template.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "review")
@Entity
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id", nullable = false)
    private Long id;

    @Lob
    @Column(nullable = false)
    private String content; // 내용

    @Column(name = "recommendation_num")
    private Integer recommendationNum;  // 추천수

    @Column(nullable = false)
    private double score;   // 평점

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id")
    private Station station;

    public void update(String content, double score) {
        this.content = content;
        this.score = score;
    }
}
