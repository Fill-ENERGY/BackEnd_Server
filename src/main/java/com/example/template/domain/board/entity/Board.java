package com.example.template.domain.board.entity;

import com.example.template.domain.member.entity.Member;
import com.example.template.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
public class Board extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id", nullable = false)
    private Long id;

    @Column(nullable = false, length = 30)
    private String title;   // 제목

    @Lob
    @Column(nullable = false)
    private String content; // 내용

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;    // 카테고리

    @Enumerated(EnumType.STRING)
    @Column(name = "help_status", nullable = false)
    private HelpStatus helpStatus;  // 도움 상태

    @Column(name = "like_num")
    private Integer likeNum;    // 좋아요 수

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
}
