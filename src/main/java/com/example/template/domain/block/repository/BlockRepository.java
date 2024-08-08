package com.example.template.domain.block.repository;

import com.example.template.domain.block.entity.Block;
import com.example.template.domain.member.entity.Member;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BlockRepository extends JpaRepository<Block, Long> {
    boolean existsByMemberAndTargetMember(Member member, Member targetMember);

    @Query("SELECT b FROM Block b WHERE b.id < :cursor AND b.member = :member ORDER BY b.id DESC")
    List<Block> findByMemberWithCursor(@Param("cursor") Long cursor, @Param("member") Member member, Pageable pageable);

    default List<Block> findByMemberWithCursor(Long cursor, Integer limit, Member member) {
        return findByMemberWithCursor(cursor, member, PageRequest.of(0, limit));
    }
}
