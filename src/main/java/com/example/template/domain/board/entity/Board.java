package com.example.template.domain.board.entity;

import com.example.template.domain.board.entity.enums.Category;
import com.example.template.domain.board.entity.enums.HelpStatus;
import com.example.template.domain.member.entity.Member;
import com.example.template.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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
    private String title;     // 제목

    @Lob
    @Column(nullable = false)
    private String content;   // 내용

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private Category category;     // 카테고리

    @Enumerated(EnumType.STRING)
    @Column(name = "help_status", nullable = false)
    private HelpStatus helpStatus; // 도움 상태

    @Column(name = "like_num", nullable = false)
    private Integer likeNum = 0;       // 좋아요 수

    @Column(name = "comment_count", nullable = false)
    private Integer commentCount = 0;  // 댓글 수

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL)
    private List<BoardImg> images = new ArrayList<>();

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    // Setter 메서드
    public void update(String title, String content, Category category) {
        this.title = title;
        this.content = content;
        this.category = category;
    }

    public void incrementLikeCount() {
        this.likeNum++;
    }

    public void decrementLikeCount() {
        if (this.likeNum > 0) {
            this.likeNum--;
        }
    }

    public void incrementCommentCount() {
        this.commentCount++;
    }

    public void decrementCommentCount() {
        if (this.commentCount > 0) {
            this.commentCount--;
        }
    }

    public void setCommentCount(int count) {
        this.commentCount = Math.max(0, count);
    }

    public void updateHelpStatus(HelpStatus helpStatus) {
        this.helpStatus = helpStatus;
    }
}
