package com.example.template.domain.complaint.controller;

import com.example.template.domain.complaint.dto.request.ComplaintRequestDTO;
import com.example.template.domain.complaint.dto.response.ComplaintResponseDTO;
import com.example.template.domain.complaint.service.ComplaintCommandService;
import com.example.template.domain.complaint.service.ComplaintQueryService;
import com.example.template.domain.member.entity.Member;
import com.example.template.global.annotation.AuthenticatedMember;
import com.example.template.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/complaints")
public class ComplaintController {

    private final ComplaintQueryService complaintQueryService;
    private final ComplaintCommandService complaintCommandService;

    @Operation(summary = "민원글 쓰기 전 충전소 이름 얻어오기",description = "민원글 쓰기 누를 때 호출")
    @GetMapping("/station/{stationId}")
    ApiResponse<ComplaintResponseDTO.getStationDTO> getStationName(@PathVariable("stationId") Long stationId){
        return ApiResponse.onSuccess(complaintQueryService.getStationName(stationId));
    }

    @Operation(summary = "이미지 업로드", description = "민원글에 첨부할 이미지를 업로드.")
    @PostMapping(value = "/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ComplaintResponseDTO.ComplaintImgDTO> uploadImages(
            @RequestPart("images") List<MultipartFile> images) {
        return ApiResponse.onSuccess(complaintCommandService.uploadImages(images));
    }

    @Operation(summary = "새 민원글 작성")
    @PostMapping()
    public ApiResponse<ComplaintResponseDTO.ComplaintDTO> createComplaint(@AuthenticatedMember Member member,
            @Valid @RequestBody ComplaintRequestDTO.CreateComplaintDTO createComplaintDTO) {
        return ApiResponse.onSuccess(complaintCommandService.createComplaint(member, createComplaintDTO));
    }


    @Operation(summary = "내가 쓴 민원글 상세 조회")
    @GetMapping("/{complaintId}")
    ApiResponse<ComplaintResponseDTO.ComplaintDTO>getComplaintDetail(@AuthenticatedMember Member member, @PathVariable("complaintId") Long complaintId){
        return ApiResponse.onSuccess(complaintQueryService.getComplaintDetail(member, complaintId));
    }

    @Operation(summary = "내가 쓴 민원글 목록 조회")
    @GetMapping()
    ApiResponse<List<ComplaintResponseDTO.ComplaintDTO>> getComplaintList(@AuthenticatedMember Member member){
        return ApiResponse.onSuccess(complaintQueryService.getComplaintList(member));
    }
}
