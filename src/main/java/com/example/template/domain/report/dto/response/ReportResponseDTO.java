package com.example.template.domain.report.dto.response;

import com.example.template.domain.member.entity.Member;
import com.example.template.domain.report.dto.request.ReportRequestDTO;
import com.example.template.domain.report.entity.Report;
import com.example.template.domain.report.entity.ReportType;
import lombok.Builder;
import lombok.Getter;

public class ReportResponseDTO {
    @Builder
    @Getter
    public static class ReportDTO{
        private Long reportId;
        private String content;
        private ReportType reportType;
        private Long targetId;

        public static ReportDTO from(Report savedReport) {
            return ReportDTO.builder()
                    .reportId(savedReport.getId())
                    .reportType(savedReport.getReportType())
                    .content(savedReport.getContent())
                    .reportId(savedReport.getTargetId()).build();
        }
    }
}
