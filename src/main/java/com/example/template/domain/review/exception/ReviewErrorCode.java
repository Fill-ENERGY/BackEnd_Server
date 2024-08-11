package com.example.template.domain.review.exception;

import com.example.template.global.apiPayload.ApiResponse;
import com.example.template.global.apiPayload.code.status.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ReviewErrorCode implements BaseErrorCode {

    NOT_FOUND(HttpStatus.NOT_FOUND, "REVIEW404", "평가를 찾지 못했습니다."),
    QUERY_BAD_REQUEST(HttpStatus.BAD_REQUEST, "REVIEW400", "평가의 쿼리가 잘못되었습니다."),
    SCORE_RANGE_ERROR(HttpStatus.BAD_REQUEST, "REVIEW404", "평가 점수가 범위 밖입니다."),
    INVALID_IMG_URL(HttpStatus.BAD_REQUEST, "REVIEW404", "잘못된 이미지 경로입니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ApiResponse<Void> getErrorResponse() {
        return ApiResponse.onFailure(code, message);
    }
}
