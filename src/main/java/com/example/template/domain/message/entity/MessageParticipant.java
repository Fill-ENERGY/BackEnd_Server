package com.example.template.domain.message.entity;

import com.example.template.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
public class MessageParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_participant_id", nullable = false)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "participation_status", nullable = false)
    private ParticipationStatus participationStatus;  // 참여 상태

    @Column(name = "left_at")
    private LocalDateTime leftAt;   // 나간 시간

    @Column(name = "last_viewed_message")
    private Long lastViewedMessage; // 마지막으로 본 메시지 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_thread_id")
    private MessageThread messageThread;

    public void leaveThread(Long lastViewedMessage) {
        this.participationStatus = ParticipationStatus.LEFT;
        this.leftAt = LocalDateTime.now();
        this.lastViewedMessage = lastViewedMessage;
    }
}
