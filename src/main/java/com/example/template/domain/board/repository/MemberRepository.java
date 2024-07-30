package com.example.template.domain.board.repository;

import com.example.template.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// TODO : 테스트 용. 삭제 예정
@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
}