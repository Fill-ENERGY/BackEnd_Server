package com.example.template.domain.member.controller;

import com.example.template.domain.member.dto.ProfileRequestDTO;
import com.example.template.domain.member.dto.ProfileResponseDTO;
import com.example.template.domain.member.entity.Member;
import com.example.template.domain.member.service.MemberCommandService;
import com.example.template.domain.member.service.MemberQueryService;
import com.example.template.global.annotation.AuthenticatedMember;
import com.example.template.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/profiles")
@Tag(name = "프로필 API")
public class ProfileController {

    private final MemberQueryService memberQueryService;
    private final MemberCommandService memberCommandService;

    @Operation(summary = "내 프로필 조회")
    @GetMapping("")
    public ApiResponse<ProfileResponseDTO.ProfileDTO> getMyProfile(@AuthenticatedMember Member member){
        return ApiResponse.onSuccess(memberQueryService.getProfile(member.getId()));
    }

    @Operation(summary = "다른 사용자 프로필 조회", description = "채팅 또는 댓글 프로필 클릭 시 해당 사용자의 프로필 조회")
    @GetMapping("/{memberId}")
    public ApiResponse<ProfileResponseDTO.ProfileDTO> getProfile(@PathVariable("memberId") Long memberId){
        return ApiResponse.onSuccess(memberQueryService.getProfile(memberId));
    }

    @Operation(summary = "프로필 수정")
    @PatchMapping("")
    public ApiResponse<ProfileResponseDTO.ProfileDTO> updateProfile(@AuthenticatedMember Member member, @RequestPart(value = "file") MultipartFile file,
                                                                    @RequestPart ProfileRequestDTO.UpdateProfileDTO requestDto){
        return ApiResponse.onSuccess(memberCommandService.updateProfile(member, file, requestDto));
    }

}
