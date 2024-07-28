package com.example.template.domain.message.repository;

import com.example.template.domain.member.entity.Member;
import com.example.template.domain.message.entity.MessageParticipant;
import com.example.template.domain.message.entity.MessageThread;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MessageParticipantRepository extends JpaRepository<MessageParticipant, Long> {
    Optional<MessageParticipant> findByMemberAndMessageThread(Member member, MessageThread messageThread);
}
