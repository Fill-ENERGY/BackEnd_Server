package com.example.template.domain.message.entity;

import com.example.template.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
public class MessageImg extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_img_id", nullable = false)
    private Long id;

    @Column(name = "img_url", nullable = false)
    private String imgUrl; // 사진 경로

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id")
    private Message message;

    public void setMessage(Message message) {
        if(this.message != null)
            message.getImages().remove(this);
        this.message = message;
        message.getImages().add(this);
    }
}
