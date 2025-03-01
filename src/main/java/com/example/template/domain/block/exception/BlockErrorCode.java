package com.example.template.domain.block.exception;

import com.example.template.global.apiPayload.ApiResponse;
import com.example.template.global.apiPayload.code.status.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum BlockErrorCode implements BaseErrorCode {

    // Block ERROR 응답
    BLOCK_NOT_FOUND(HttpStatus.NOT_FOUND,
            "BLOCK404", "차단 내역을 찾을 수 없습니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN,
            "BLOCK403_1", "권한이 없습니다."),
    SELF_BLOCK_NOT_ALLOWED(HttpStatus.FORBIDDEN,
            "BLOCK403_2", "자기 자신은 차단할 수 없습니다."),
    BLOCK_ALREADY_EXISTS(HttpStatus.BAD_REQUEST,
            "BLOCK400", "이미 차단된 사용자입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ApiResponse<Void> getErrorResponse() {
        return ApiResponse.onFailure(code, message);
    }
}

