package com.example.template.domain.board.entity;

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
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id", nullable = false)
    private Long id;

    @Lob
    @Column(nullable = false)
    private String content; // 내용

    @Column(name = "is_secret", nullable = false)
    private boolean isSecret;  // 비밀 여부

    // is_author 필드 제거
    // TODO : "글쓴이"와 같은 유저인지는 동적인 정보이므로, 계산 로직으로 처리

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    // (대댓글 위해서) 자기 순환 참조
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @OneToMany(mappedBy = "parent")
    private List<Comment> children = new ArrayList<>();

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentImg> images = new ArrayList<>();

    // Setter 메서드들
    public void setContent(String content) {
        this.content = content;
    }

    public void setSecret(boolean secret) {
        isSecret = secret;
    }

    // 연관관계 편의 메서드들
    public void setMember(Member member) {
        this.member = member;
    }

    public void setBoard(Board board) {
        this.board = board;
        board.getComments().add(this);
    }

    public void setParent(Comment parent) {
        this.parent = parent;
    }

    public void addChild(Comment child) {
        this.children.add(child);
        child.setParent(this);
    }

    public void removeChild(Comment child) {
        this.children.remove(child);
        child.setParent(null);
    }

    public void addCommentImg(CommentImg commentImg) {
        this.images.add(commentImg);
        commentImg.setComment(this);
    }

    public void removeCommentImg(CommentImg commentImg) {
        this.images.remove(commentImg);
        commentImg.setComment(null);
    }
}
