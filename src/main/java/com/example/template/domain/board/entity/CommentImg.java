package com.example.template.domain.board.entity;

import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
public class CommentImg {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_img_id", nullable = false)
    private Long id;

    @Column(name = "comment_img_url", nullable = false)
    private String commentImgUrl;  // 사진 경로

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    // Setter 메서드
    public void setCommentImgUrl(String commentImgUrl) {
        this.commentImgUrl = commentImgUrl;
    }

    // 연관관계 편의 메서드
    public void setComment(Comment comment) {
        this.comment = comment;
    }
}