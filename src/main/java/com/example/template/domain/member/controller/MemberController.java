package com.example.template.domain.member.controller;

import com.example.template.domain.member.dto.MemberRequestDTO;
import com.example.template.domain.member.dto.MemberResponseDTO;
import com.example.template.domain.member.dto.SocialRequestDTO;
import com.example.template.domain.member.entity.Member;
import com.example.template.domain.member.exception.MemberErrorCode;
import com.example.template.domain.member.exception.MemberException;
import com.example.template.domain.member.jwt.dto.JwtDTO;
import com.example.template.domain.member.jwt.exception.SecurityCustomException;
import com.example.template.domain.member.service.KakaoService;
import com.example.template.domain.member.service.MemberService;
import com.example.template.global.annotation.AuthenticatedMember;
import com.example.template.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/members")
@Tag(name = "멤버 API")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final KakaoService kakaoService;

    @Operation(summary = "일반 회원가입", description = "이름, 이메일, 비밀번호를 입력받아 회원가입을 진행합니다. 이메일은 중복 불가, 비밀먼호는 인코딩 되어 저장됨. 참고)")
    @PostMapping("/signup")
    public ApiResponse<MemberResponseDTO.SignupResultDTO> signup(@RequestBody MemberRequestDTO.SignupDTO requestDTO) {
        return ApiResponse.onSuccess(memberService.signup(requestDTO));
    }

    @Operation(summary = "일반 로그인", description = "이메일, 비밀번호를 입력받아 로그인을 진행합니다." +
            "반환 값으로 JWT accessToken과 refreshToken이 발급됨. accessToken 값을 Authorize에 인증")
    @PostMapping("/login")
    public ApiResponse<MemberResponseDTO.LoginResultDTO> login(@Valid @RequestBody MemberRequestDTO.CustomLoginDTO requestDTO) {
        return ApiResponse.onSuccess(memberService.login(requestDTO));
    }

    @Operation(summary = "카카오 로그인 및 회원가입", description = "카카오 accessToken을 입력받아 로그인 또는 회원가입을 처리합니다. " +
            "반환 값으로 JWT accessToken과 refreshToken이 발급되며, accessToken 값을 Authorize에 인증")
    @PostMapping("/social/loginorsignup/kakao")
    public ApiResponse<MemberResponseDTO.LoginResultDTO> loginOrSignupByKakao(@Valid @RequestBody SocialRequestDTO.LoginDTO requestDTO) {
        return ApiResponse.onSuccess(kakaoService.loginOrSignupByKakao(requestDTO));
    }

    @Operation(summary = "카카오 로그인 및 회원가입_SDK", description = "카카오 accessToken을 헤더로 받아 로그인 또는 회원가입을 처리합니다. " +
            "반환 값으로 JWT accessToken과 refreshToken이 발급되며, accessToken 값을 Authorize에 인증")
    @PostMapping("/social/oauth/kakao")
    public ApiResponse<MemberResponseDTO.LoginResultDTO> androidKakao(
            @RequestHeader("Authorization") String authorizationHeader) {

        // Authorization 헤더에서 Bearer 토큰 추출
        if (authorizationHeader != null) {
            String accessToken = authorizationHeader;
            // 카카오 서비스 호출하여 로그인 또는 회원가입 처리
            return ApiResponse.onSuccess(kakaoService.androidKakao(accessToken));
        } else {
            throw new MemberException(MemberErrorCode._INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "로그아웃")
    @PostMapping("/logout")
    public ApiResponse<String> logout(HttpServletRequest request) {
        memberService.logout(request);
        return ApiResponse.onSuccess("로그아웃 성공");
    }

    @Operation(summary = "토큰 재발급", description = "JWT accessToken이 만료됐을 시 refreshToken을 통해 accessToken 재발급.")
    @GetMapping("/reissue")
    public ApiResponse<JwtDTO> reissueToken(@RequestHeader("RefreshToken") String refreshToken) {
        return ApiResponse.onSuccess(memberService.reissueToken(refreshToken));
    }
    @Operation(summary = "인증 테스트 용", description = "어노테이션 작동 테스트용 API 입니다.")
    @GetMapping("/me")
    public ApiResponse<MemberResponseDTO.MemberTestDTO> getCurrentMember(@AuthenticatedMember Member member) {
        MemberResponseDTO.MemberTestDTO memberDTO = new MemberResponseDTO.MemberTestDTO(
                member.getId(),
                member.getEmail(),
                member.getName()
        );
        return ApiResponse.onSuccess(memberDTO);
    }

}
