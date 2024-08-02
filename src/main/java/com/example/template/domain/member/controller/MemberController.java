package com.example.template.domain.member.controller;

import com.example.template.domain.member.dto.ProfileRequestDTO;
import com.example.template.domain.member.dto.ProfileResponseDTO;
import com.example.template.domain.member.service.MemberCommandService;
import com.example.template.domain.member.service.MemberQueryService;
import com.example.template.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/")
@AllArgsConstructor
public class MemberController {

    final private MemberCommandService memberCommandService;
    final private MemberQueryService memberQueryService;

    @Operation(summary = "프로필 수정 API", description = "프로필 수정 API입니다.")
    @PatchMapping("/members/profiles")
    public ApiResponse<ProfileResponseDTO.ProfileDTO> updateProfile(@RequestBody ProfileRequestDTO.UpdateProfileDTO updateProfileDTO){
        //임의로 넣음 -> 추후 @Authenti~~ 사용예정
        Long memberId = 1L;
        return ApiResponse.onSuccess(memberCommandService.updateProfile(memberId, updateProfileDTO));
    }

    @Operation(summary = "프로필 조회 API", description = "프로필 조회 API입니다.")
    @GetMapping("/members/profiles")
    public ApiResponse<ProfileResponseDTO.ProfileDTO> getProfile(){
        //임의로 넣음 -> 추후 @Authenti~~ 사용예정
        Long memberId = 1L;
        return ApiResponse.onSuccess(memberQueryService.getProfile(memberId));
    }

    //이부분은 로그인 맡은 분이 수정 및 구현 예정
//    @Operation(summary = "프로필 생성 API", description = "프로필 생성 API입니다.")
//    @PostMapping("/members/profile")
//    public ApiResponse<Boolean> createProfile(@RequestBody ProfileRequestDTO.CreateProfileDTO createProfileDTO){
//        Long memberId = 1L;
//        return ApiResponse.onSuccess(memberService.createProfile(memberId, createProfileDTO));
//    }

    @Operation(summary = "프로필 삭제 API", description = "프로필 삭제 API입니다.")
    @DeleteMapping("/members/profile")
    public ApiResponse<Long> deleteProfile(){
        //임의로 넣음 -> 추후 @Authenti~~ 사용예정
        Long memberId =1L;
        return ApiResponse.onSuccess(memberCommandService.deleteProfile(memberId));
    }

}
