package com.example.template.domain.message.exception;

import com.example.template.global.apiPayload.ApiResponse;
import com.example.template.global.apiPayload.code.status.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MessageErrorCode implements BaseErrorCode {

    // MessageThread ERROR 응답
    THREAD_NOT_FOUND(HttpStatus.NOT_FOUND,
            "THREAD401", "채팅방이 없습니다."),

    // Message ERROR 응답
    MESSAGE_NOT_FOUND(HttpStatus.NOT_FOUND,
                      "MESSAGE401", "쪽지가 없습니다."),
    PERMISSION_DENIED(HttpStatus.FORBIDDEN,
            "MESSAGE402", "보낸 사람 또는 받는 사람이 아닙니다. 권한이 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ApiResponse<Void> getErrorResponse() {
        return ApiResponse.onFailure(code, message);
    }
}
