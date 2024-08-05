package com.example.template.domain.message.repository;

import com.example.template.domain.message.entity.MessageThread;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageThreadRepository extends JpaRepository<MessageThread, Long> {
}
