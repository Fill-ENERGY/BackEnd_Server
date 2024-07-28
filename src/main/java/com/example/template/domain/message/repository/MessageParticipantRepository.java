package com.example.template.domain.message.repository;

import com.example.template.domain.message.entity.MessageParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageParticipantRepository extends JpaRepository<MessageParticipant, Long> {
}
