package com.example.template.domain.report.dto.request;

import com.example.template.domain.member.entity.Member;
import com.example.template.domain.report.entity.Report;
import lombok.Builder;
import lombok.Getter;

public class ReportRequestDTO {
    @Builder
    @Getter
    public static class CreateReportDTO{
        private String content;
        private String reportType;
        private Long targetId;

        public Report toEntity(Member member) {
            return Report.builder()
                    .member(member)
                    .content(this.getContent())
                    .targetId(this.getTargetId())
                    .reportType(this.getReportType()).build();

        }
    }

    @Builder
    @Getter
    public class UpdateReportDTO {
        private String reportType;
        private Long targetId;
    }
}
