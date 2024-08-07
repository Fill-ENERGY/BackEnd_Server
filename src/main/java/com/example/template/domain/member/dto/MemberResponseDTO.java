package com.example.template.domain.member.dto;

import com.example.template.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class MemberResponseDTO {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class LoginResultDTO {
        private Long userId;
        private LocalDateTime createdAt;
        private String accessToken;
        private String refreshToken;

        public static LoginResultDTO from(Member member, String accessToken, String refreshToken){
            return LoginResultDTO.builder()
                    .userId(member.getId())
                    .createdAt(LocalDateTime.now())
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class SignupResultDTO {
        private Long id;
        private LocalDateTime createdAt;

        public static SignupResultDTO from(Member member){
            return SignupResultDTO.builder()
                    .id(member.getId())
                    .createdAt(member.getCreatedAt())
                    .build();
        }
    }
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class MemberTestDTO {
        private Long id;
        private String email;
        private String name;

    }
}