package com.example.template.domain.member.service;

import com.example.template.domain.member.dto.MemberRequestDTO;
import com.example.template.domain.member.dto.MemberResponseDTO;
import com.example.template.domain.member.entity.Member;
import jakarta.servlet.http.HttpServletRequest;

public interface MemberService {
    Member signup(MemberRequestDTO.SignupDTO signupDTO);
    MemberResponseDTO.LoginResultDTO login(MemberRequestDTO.LoginDTO loginDTO);
    void logout(HttpServletRequest request);
}
