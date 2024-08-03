package com.example.template.domain.report.controller;

import com.example.template.domain.report.dto.request.ReportRequestDTO;
import com.example.template.domain.report.dto.response.ReportResponseDTO;
import com.example.template.domain.report.service.ReportCommandService;
import com.example.template.global.apiPayload.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class ReportController {

    private final ReportCommandService reportCommandService;

    public ApiResponse<ReportResponseDTO.ReportDTO> createReport(@RequestBody ReportRequestDTO.CreateReportDTO createReportDTO){
        //추후에 @Authenticaton~~ 추가 예정
        Long memberId = 1L;
        return ApiResponse.onSuccess(reportCommandService.createReport(memberId, createReportDTO));
    }

    public ApiResponse<Long> deleteReport(@RequestBody ReportRequestDTO.UpdateReportDTO updateReportDTO){
        //추후에 @Authenticaton~~ 추가 예정
        Long memberId = 1L;
        return ApiResponse.onSuccess(reportCommandService.deleteReport(memberId, updateReportDTO));
    }
}
