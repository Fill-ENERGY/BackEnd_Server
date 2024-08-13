package com.example.template.domain.complaint.exception;

import com.example.template.global.apiPayload.ApiResponse;
import com.example.template.global.apiPayload.code.status.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ComplaintErrorCode implements BaseErrorCode {

    // BOARD 에러
    COMPLAINT_NOT_FOUND(HttpStatus.NOT_FOUND, "COMPLAINT404", "민원글을 찾을 수 없습니다."),

    //IMAGE 에러
    INVALID_IMAGE_URLS(HttpStatus.BAD_REQUEST, "COMPLAINT400", "일부 이미지 URL이 유효하지 않거나 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ApiResponse<Void> getErrorResponse() {
        return ApiResponse.onFailure(code, message);
    }
}
