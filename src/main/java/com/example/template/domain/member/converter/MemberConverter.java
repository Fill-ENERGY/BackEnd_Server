package com.example.template.domain.member.converter;

import com.example.template.domain.member.dto.MemberRequestDTO;
import com.example.template.domain.member.dto.MemberResponseDTO;
import com.example.template.domain.member.entity.Member;
import org.springframework.security.crypto.password.PasswordEncoder;

public class MemberConverter {
    public static Member toMember(MemberRequestDTO.SignupDTO signupDTO, PasswordEncoder passwordEncoder) {
        return Member.builder()
                .name(signupDTO.getName())
                .password(passwordEncoder.encode(signupDTO.getPassword()))
                .email(signupDTO.getEmail())
                .build();
    }
    public static MemberResponseDTO.SignupResultDTO toSignupResultDTO(Member member) {
        return MemberResponseDTO.SignupResultDTO.builder()
                .id(member.getId())
                .createdAt(member.getCreatedAt())
                .build();
    }
    public static MemberResponseDTO.LoginResultDTO toLoginResultDTO(Member member, String accessToken, String refreshToken) {
        return MemberResponseDTO.LoginResultDTO.builder()
                .userId(member.getId())
                .createdAt(member.getCreatedAt())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
