package com.example.template.domain.message.entity;

import com.example.template.domain.member.entity.Member;
import com.example.template.domain.message.entity.enums.ReadStatus;
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
public class Message extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id", nullable = false)
    private Long id;

    @Lob
    private String content; // 내용

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

    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL)
    private List<MessageImg> images = new ArrayList<>();

    public void updateDeletedBySender(boolean deleted) {
        this.deletedBySen = deleted;
    }

    public void updateDeletedByReceiver(boolean deleted) {
        this.deletedByRec = deleted;
    }

    public void updateReadStatus(ReadStatus readStatus) {
        this.readStatus = readStatus;
    }
}
