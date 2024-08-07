package com.example.template.domain.report.controller;

import com.example.template.domain.report.dto.request.ReportRequestDTO;
import com.example.template.domain.report.dto.response.ReportResponseDTO;
import com.example.template.domain.report.service.ReportCommandService;
import com.example.template.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/reports")
public class ReportController {

    private final ReportCommandService reportCommandService;

    @Operation(summary = "신고하기", description = "내용, 타입, targetId 입력 필요")
    @PostMapping("")
    public ApiResponse<ReportResponseDTO.ReportDTO> createReport(@RequestBody ReportRequestDTO.CreateReportDTO createReportDTO){
        //추후에 @Authenticaton~~ 추가 예정
        Long memberId = 1L;
        return ApiResponse.onSuccess(reportCommandService.createReport(memberId, createReportDTO));
    }

    @Operation(summary = "신고하기 취소", description = "타입, targetId 입력 필요")
    @DeleteMapping("")
    public ApiResponse<Long> deleteReport(@RequestBody ReportRequestDTO.DeleteReportDTO deleteReportDTO){
        //추후에 @Authenticaton~~ 추가 예정
        Long memberId = 1L;
        return ApiResponse.onSuccess(reportCommandService.deleteReport(memberId, deleteReportDTO));
    }
}
