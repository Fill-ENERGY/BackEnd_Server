package com.example.template.domain.report.dto.request;

import com.example.template.domain.member.entity.Member;
import com.example.template.domain.report.entity.Report;
import com.example.template.domain.report.entity.ReportCategory;
import com.example.template.domain.report.entity.ReportContent;
import com.example.template.domain.report.entity.ReportType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReportRequestDTO {
    @Builder
    @Getter
    public static class CreateReportDTO{
        private String content;
        private ReportType reportType;
        private ReportCategory reportCategory;
        private Long targetId;
    }

    public static Report toEntity(Member member, ReportRequestDTO.CreateReportDTO createReportDTO) {
        return Report.builder()
                .member(member)
                .reportCategory(createReportDTO.getReportCategory())
                .targetId(createReportDTO.getTargetId())
                .reportType(createReportDTO.getReportType()).build();

    }
    public static ReportContent toEntity(Report report, ReportRequestDTO.CreateReportDTO createReportDTO) {
        return ReportContent.builder()
                .report(report)
                .content(createReportDTO.getContent())
                .build();
    }
    @Builder
    @Getter
    public static class DeleteReportDTO {
        private ReportCategory reportCategory;
        private Long targetId;
    }
}
