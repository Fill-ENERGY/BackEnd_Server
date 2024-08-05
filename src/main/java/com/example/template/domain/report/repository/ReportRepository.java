package com.example.template.domain.report.repository;

import com.example.template.domain.report.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    @Query("SELECT r FROM Report r WHERE r.member.id = :memberId AND r.targetId = :targetId AND r.reportType = :reportType")
    Report findByMemberAndTargetIdAndReportType(
            @Param("member") Long memberId,
            @Param("targetId") Long targetId,
            @Param("reportType") String reportType
    );
}
