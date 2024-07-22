package com.example.template.domain.message.entity;

import com.example.template.domain.member.entity.Member;
import com.example.template.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
public class Message extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id", nullable = false)
    private Long id;

    private String content; // 내용

    @Column(name = "img_url")
    private String imgUrl;    // 사진 경로

    @Enumerated(EnumType.STRING)
    @Column(name = "read_status", nullable = false)
    private ReadStatus readStatus;  // 읽음 상태

    @Column(name = "deleted_by_sen")
    private boolean deletedBySen;    // 보낸 사람 삭제 여부

    @Column(name = "deleted_by_rec")
    private boolean deletedByRec;  // 받은 사람 삭제 여부

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private Member sender;  // 보낸 사람

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private Member receiver;    // 받는 사람

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_thread_id")
    private MessageThread messageThread;
}
