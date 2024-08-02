package com.example.template.domain.block.repository;

import com.example.template.domain.block.entity.Block;
import com.example.template.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BlockRepository extends JpaRepository<Block, Long> {
    List<Block> findByMember(Member member);

    boolean existsByMemberAndTargetMember(Member member, Member targetMember);
}
