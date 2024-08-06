package com.example.template.domain.report.repository;

import com.example.template.domain.report.entity.ReportContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportContentRepository extends JpaRepository<ReportContent, Long> {
    void deleteByReportId(Long reportId);
}
