package com.example.template.domain.member.service;

import com.example.template.domain.member.dto.MemberRequestDTO;
import com.example.template.domain.member.dto.MemberResponseDTO;
import com.example.template.domain.member.entity.Member;
import com.example.template.domain.member.jwt.dto.JwtDTO;
import jakarta.servlet.http.HttpServletRequest;

public interface MemberService {
    MemberResponseDTO.SignupResultDTO signup(MemberRequestDTO.SignupDTO signupDTO);
    MemberResponseDTO.LoginResultDTO login(MemberRequestDTO.CustomLoginDTO loginDTO);
    MemberResponseDTO.SignupResultDTO socialSignup(MemberRequestDTO.SignupDTO signupDTO);
    void logout(HttpServletRequest request);
    JwtDTO reissueToken(String refreshToken);
}
