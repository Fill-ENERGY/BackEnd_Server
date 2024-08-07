package com.example.template.domain.message.entity;

import com.example.template.domain.member.entity.Member;
import com.example.template.domain.message.entity.enums.ParticipationStatus;
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

    // TODO 마지막으로 본 쪽지 id 필드 삭제 - leftAt으로 나간 시간 이후 받은 쪽지만 필터링 가능

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_thread_id")
    private MessageThread messageThread;

    public void leaveThread() {
        this.participationStatus = ParticipationStatus.LEFT;
        this.leftAt = LocalDateTime.now();
    }

    public void updateParticipationStatus(ParticipationStatus participationStatus) {
        this.participationStatus = participationStatus;
    }
}
