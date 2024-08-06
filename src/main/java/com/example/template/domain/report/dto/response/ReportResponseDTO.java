package com.example.template.domain.report.dto.response;

import com.example.template.domain.report.entity.Report;
import com.example.template.domain.report.entity.ReportCategory;
import com.example.template.domain.report.entity.ReportContent;
import com.example.template.domain.report.entity.ReportType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

public class ReportResponseDTO {
    @Builder
    @Getter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ReportDTO{
        private Long reportId;
        private String content;
        private ReportType reportType;
        private Long targetId;
        private ReportCategory reportCategory;

        public static ReportDTO from(Report savedReport) {
            return ReportDTO.builder()
                    .reportId(savedReport.getId())
                    .reportType(savedReport.getReportType())
                    .reportCategory(savedReport.getReportCategory())
                    .targetId(savedReport.getTargetId())
                    .reportId(savedReport.getTargetId()).build();
        }

        public static ReportDTO from(Report savedReport, ReportContent reportContent) {
            return ReportDTO.builder()
                    .reportId(savedReport.getId())
                    .reportType(savedReport.getReportType())
                    .content(reportContent.getContent())
                    .reportCategory(savedReport.getReportCategory())
                    .targetId(savedReport.getTargetId())
                    .reportId(savedReport.getTargetId()).build();
        }
    }
}
