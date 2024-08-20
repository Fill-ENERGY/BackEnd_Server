package com.example.template.domain.message.repository;

import com.example.template.domain.member.entity.Member;
import com.example.template.domain.message.entity.MessageParticipant;
import com.example.template.domain.message.entity.MessageThread;
import com.example.template.domain.message.entity.enums.ParticipationStatus;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MessageParticipantRepository extends JpaRepository<MessageParticipant, Long> {
    Optional<MessageParticipant> findByMemberAndMessageThread(Member member, MessageThread messageThread);

    void deleteByMessageThreadId(Long id);

    @Query("SELECT mp FROM MessageParticipant mp " +
            "WHERE mp.member = :member " +
            "AND mp.participationStatus = :status " +
            "AND (mp.messageThread.updatedAt < :cursor " +
            "OR (mp.messageThread.updatedAt = :cursor AND mp.messageThread.id < :lastId)) " +
            "AND mp.messageThread.id NOT IN (SELECT mt.id FROM MessageThread mt " +
            "JOIN mt.participants p WHERE p.member IN :blockedMembers) " +
            "ORDER BY mp.messageThread.updatedAt DESC, mp.messageThread.id DESC")
    List<MessageParticipant> findByMemberAndParticipationStatusWithCursor(
            @Param("member") Member member,
            @Param("status") ParticipationStatus status,
            @Param("blockedMembers") List<Member> blockedMembers,
            @Param("cursor") LocalDateTime cursor,
            @Param("lastId") Long lastId,
            Pageable pageable
    );


    default List<MessageParticipant> findByMemberAndParticipationStatusWithCursor(
            LocalDateTime cursor,
            Long lastId,
            List<Member> blockedMembers,
            Integer limit,
            Member member,
            ParticipationStatus status
    ) {
        return findByMemberAndParticipationStatusWithCursor(member, status, blockedMembers, cursor, lastId, PageRequest.of(0, limit));
    }

}