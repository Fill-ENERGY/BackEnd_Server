package com.example.template.domain.member.controller;

import com.example.template.domain.member.converter.MemberConverter;
import com.example.template.domain.member.dto.MemberRequestDTO;
import com.example.template.domain.member.dto.MemberResponseDTO;
import com.example.template.domain.member.entity.Member;
import com.example.template.domain.member.jwt.dto.JwtDTO;
import com.example.template.domain.member.jwt.exception.SecurityCustomException;
import com.example.template.domain.member.jwt.exception.TokenErrorCode;
import com.example.template.domain.member.jwt.util.JwtProvider;
import com.example.template.domain.member.service.MemberService;
import com.example.template.global.apiPayload.ApiResponse;
import io.jsonwebtoken.ExpiredJwtException;
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
    private final JwtProvider jwtProvider;
    final private MemberCommandService memberCommandService;
    final private MemberQueryService memberQueryService;

    @Operation(summary = "일반 회원가입", description = "이름, 이메일, 비밀번호를 입력받아 회원가입을 진행합니다. 이메일은 중복 불가, 비밀먼호는 인코딩 되어 저장됨. 참고)")
    @PostMapping("/signup")
    public ApiResponse<MemberResponseDTO.SignupResultDTO> signup(@RequestBody MemberRequestDTO.SignupDTO requestDTO) {
        Member member = memberService.signup(requestDTO);
        return ApiResponse.onSuccess(MemberConverter.toSignupResultDTO(member));
    }

    @Operation(summary = "일반 로그인", description = "이메일, 비밀번호를 입력받아 로그인을 진행합니다." +
            "반환 값으로 JWT accessToken과 refreshToken이 발급됨. accessToken 값을 Authorize에 인증")
    @PostMapping("/login")
    public ApiResponse<MemberResponseDTO.LoginResultDTO> login(@Valid @RequestBody MemberRequestDTO.LoginDTO requestDTO) {
        return ApiResponse.onSuccess(memberService.login(requestDTO));
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
        try {
            jwtProvider.validateRefreshToken(refreshToken);
            return ApiResponse.onSuccess(
                    jwtProvider.reissueToken(refreshToken)
            );
        } catch (ExpiredJwtException eje) {
            throw new SecurityCustomException(TokenErrorCode.TOKEN_EXPIRED, eje);
        } catch (IllegalArgumentException iae) {
            throw new SecurityCustomException(TokenErrorCode.INVALID_TOKEN, iae);
        }
    }
  
    @Operation(summary = "프로필 수정 API", description = "프로필 수정 API입니다.")
    @PatchMapping("/profiles")
    public ApiResponse<ProfileResponseDTO.ProfileDTO> updateProfile(@RequestBody ProfileRequestDTO.UpdateProfileDTO updateProfileDTO){
        //임의로 넣음 -> 추후 @Authenti~~ 사용예정
        Long memberId = 1L;
        return ApiResponse.onSuccess(memberCommandService.updateProfile(memberId, updateProfileDTO));
    }

    @Operation(summary = "프로필 조회 API", description = "프로필 조회 API입니다.")
    @GetMapping("/profiles")
    public ApiResponse<ProfileResponseDTO.ProfileDTO> getProfile(){
        //임의로 넣음 -> 추후 @Authenti~~ 사용예정
        Long memberId = 1L;
        return ApiResponse.onSuccess(memberQueryService.getProfile(memberId));
    }

    @Operation(summary = "프로필 삭제 API", description = "프로필 삭제 API입니다.")
    @DeleteMapping("/members/profile")
    public ApiResponse<Long> deleteProfile(){
        //임의로 넣음 -> 추후 @Authenti~~ 사용예정
        Long memberId =1L;
        return ApiResponse.onSuccess(memberCommandService.deleteProfile(memberId));

}
