package com.example.template.domain.message.repository;

import com.example.template.domain.member.entity.Member;
import com.example.template.domain.message.entity.MessageThread;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MessageThreadRepository extends JpaRepository<MessageThread, Long> {
    @Query("SELECT t FROM MessageThread t " +
            "JOIN t.participants p1 " +
            "JOIN t.participants p2 " +
            "WHERE p1.member = :member1 AND p2.member = :member2")
    Optional<MessageThread> findByParticipantsMember(@Param("member1") Member member1, @Param("member2") Member member2);
}
