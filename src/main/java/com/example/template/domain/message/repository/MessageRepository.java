package com.example.template.domain.message.repository;

import com.example.template.domain.member.entity.Member;
import com.example.template.domain.message.entity.Message;
import com.example.template.domain.message.entity.MessageThread;
import com.example.template.domain.message.entity.ReadStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long> {
    Optional<Message> findTopByReceiverAndReadStatusAndDeletedByRecFalseOrderByCreatedAtDesc(Member member, ReadStatus readStatus);

    long countByMessageThreadAndReceiverAndReadStatusAndDeletedByRecFalse(MessageThread thread, Member member, ReadStatus readStatus);

    @Query("SELECT m FROM Message m WHERE m.messageThread = :thread AND " +
            "((m.sender = :member AND m.deletedBySen = false) OR " +
            "(m.receiver = :member AND m.deletedByRec = false)) " +
            "ORDER BY m.createdAt DESC")
    List<Message> findMessagesByThreadAndMember(@Param("thread") MessageThread thread,
                                                @Param("member") Member member, Pageable pageable);
}