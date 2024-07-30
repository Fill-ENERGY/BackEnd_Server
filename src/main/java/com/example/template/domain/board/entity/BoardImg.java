package com.example.template.domain.board.entity;

import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
public class BoardImg {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_img_id", nullable = false)
    private Long id;

    @Column(name = "board_img_url", nullable = false)
    private String boardImgUrl;  // 사진 경로

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    // Setter 메서드
    public void setBoardImgUrl(String boardImgUrl) {
        this.boardImgUrl = boardImgUrl;
    }

    // 연관관계 편의 메서드
    public void setBoard(Board board){
        if(this.board != null)
            board.getImages().remove(this);
        this.board = board;
        board.getImages().add(this);
    }
}