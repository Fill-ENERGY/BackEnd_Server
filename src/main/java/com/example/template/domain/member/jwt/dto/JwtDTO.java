package com.example.template.domain.member.jwt.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JwtDTO {
    private String accessToken;
    private String refreshToken;
}
