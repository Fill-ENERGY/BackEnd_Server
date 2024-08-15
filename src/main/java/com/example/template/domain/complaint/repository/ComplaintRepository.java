package com.example.template.domain.complaint.repository;

import com.example.template.domain.complaint.entity.Complaint;
import com.example.template.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    @Query("SELECT c FROM Complaint c WHERE c.member.id = :memberId")
    List<Complaint> findAllByMemberId(@Param("memberId")Long memberId);

    Complaint findByIdAndMember(Long complaintId, Member member);
}
