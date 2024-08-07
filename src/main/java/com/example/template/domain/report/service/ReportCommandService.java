package com.example.template.domain.report.service;

import com.example.template.domain.report.dto.request.ReportRequestDTO;
import com.example.template.domain.report.dto.response.ReportResponseDTO;

public interface ReportCommandService {

    ReportResponseDTO.ReportDTO createReport(Long id, ReportRequestDTO.CreateReportDTO requestDTO);
    Long deleteReport(Long memberId, ReportRequestDTO.DeleteReportDTO reportDTO);
}
