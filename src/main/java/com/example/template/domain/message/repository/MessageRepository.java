package com.example.template.domain.message.repository;

import com.example.template.domain.member.entity.Member;
import com.example.template.domain.message.entity.Message;
import com.example.template.domain.message.entity.MessageThread;
import com.example.template.domain.message.entity.enums.ReadStatus;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    long countByMessageThreadAndReceiverAndReadStatusAndDeletedByRecFalse(MessageThread messageThread, Member member, ReadStatus readStatus);

    @Query("SELECT m FROM Message m WHERE m.messageThread = :messageThread AND " +
            "((m.sender = :member AND m.deletedBySen = false) OR " +
            "(m.receiver = :member AND m.deletedByRec = false)) " +
            "ORDER BY m.createdAt DESC")
    List<Message> findMessagesByMessageThreadAndMemberOrderByCreatedAtDesc(@Param("messageThread") MessageThread messageThread,
                                                                           @Param("member") Member member, Pageable pageable);

    @Query("SELECT m FROM Message m WHERE m.messageThread = :messageThread " +
            "AND m.id < :cursor " +
            "AND ((m.sender = :member AND m.deletedBySen = false) OR " +
            "(m.receiver = :member AND m.deletedByRec = false)) " +
            "AND (:leftAt IS NULL OR m.createdAt > :leftAt) " +
            "ORDER BY m.createdAt DESC")
    List<Message> findMessagesByMessageThreadAndMemberAndLeftAtAfterWithCursor(
            @Param("cursor") Long cursor,
            @Param("messageThread") MessageThread messageThread,
            @Param("member") Member member,
            @Param("leftAt") LocalDateTime leftAt,
            Pageable pageable);

    default List<Message> findMessagesByMessageThreadAndMemberAndLeftAtAfterWithCursor(
            Long cursor, Integer limit, MessageThread messageThread, Member member, LocalDateTime leftAt) {
        return findMessagesByMessageThreadAndMemberAndLeftAtAfterWithCursor(cursor, messageThread, member, leftAt, PageRequest.of(0, limit));
    }

    List<Message> findMessagesByMessageThreadAndReceiverAndReadStatus(MessageThread messageThread, Member member, ReadStatus readStatus);

    List<Message> findByDeletedBySenTrueAndDeletedByRecTrue();

    List<Message> findByMessageThreadId(Long id);
}