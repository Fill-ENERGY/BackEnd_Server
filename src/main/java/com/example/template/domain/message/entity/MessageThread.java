package com.example.template.domain.message.entity;

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
public class MessageThread extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_thread_id", nullable = false)
    private Long id;

    @OneToMany(mappedBy = "messageThread", cascade = CascadeType.ALL)
    private List<MessageParticipant> participants = new ArrayList<>();
}
