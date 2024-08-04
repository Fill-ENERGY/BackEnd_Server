package com.example.template.global.util.s3.exxception;

import com.example.template.global.apiPayload.ApiResponse;
import com.example.template.global.apiPayload.code.status.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum S3ErrorCode implements BaseErrorCode {

    // S3 ERROR 응답
    UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "S3500_1", "S3 파일 업로드에 실패했습니다."),
    DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "S3500_2", "S3 파일 삭제에 실패했습니다."),
    SIZE_MISMATCH(HttpStatus.INTERNAL_SERVER_ERROR, "S3500_3", "파일 수와 키 이름 수가 일치하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ApiResponse<Void> getErrorResponse() {
        return ApiResponse.onFailure(code, message);
    }
}