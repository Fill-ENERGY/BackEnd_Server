package com.example.template.domain.member.jwt.handler;

import com.example.template.domain.member.jwt.exception.TokenErrorCode;
import com.example.template.domain.member.jwt.util.HttpResponseUtil;
import com.example.template.global.apiPayload.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException {
        HttpStatus httpStatus;
        ApiResponse<String> errorResponse;

        log.error(">>>>>> AuthenticationException: ", authException);
        httpStatus = HttpStatus.UNAUTHORIZED;
        errorResponse = ApiResponse.onFailure(
                TokenErrorCode.UNAUTHORIZED.getCode(),
                TokenErrorCode.UNAUTHORIZED.getMessage(),
                authException.getMessage());

        HttpResponseUtil.setErrorResponse(response, httpStatus, errorResponse);
    }
}