package com.example.template.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

public class SocialRequestDTO {
    @Getter
    public static class LoginDTO{
        @NotBlank(message = "[ERROR] 토큰 입력은 필수 입니다.")
        String accessToken;
    }

    @Getter
    public static class SignupDTO{
        @NotBlank(message = "[ERROR] 토큰 입력은 필수 입니다.")
        String accessToken;
    }
}
