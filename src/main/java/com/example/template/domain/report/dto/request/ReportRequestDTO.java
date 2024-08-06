package com.example.template.domain.report.dto.request;

import com.example.template.domain.member.entity.Member;
import com.example.template.domain.report.entity.Report;
import com.example.template.domain.report.entity.ReportType;
import lombok.Builder;
import lombok.Getter;

public class ReportRequestDTO {
    @Builder
    @Getter
    public static class CreateReportDTO{
        private String content;
        private ReportType reportType;
        private Long targetId;

    }

    public static Report toEntity(Member member, ReportRequestDTO.CreateReportDTO createReportDTO) {
        return Report.builder()
                .member(member)
                .content(createReportDTO.getContent())
                .targetId(createReportDTO.getTargetId())
                .reportType(createReportDTO.getReportType()).build();

    }
    @Builder
    @Getter
    public class DeleteReportDTO {
        private ReportType reportType;
        private Long targetId;
    }
}
