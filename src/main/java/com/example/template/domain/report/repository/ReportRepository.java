package com.example.template.domain.report.repository;

import com.example.template.domain.report.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    Report findByMemberAndTargetIdAndReportType(@Param(""));
}
