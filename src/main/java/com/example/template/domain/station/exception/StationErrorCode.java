package com.example.template.domain.station.exception;

import com.example.template.global.apiPayload.ApiResponse;
import com.example.template.global.apiPayload.code.status.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum StationErrorCode implements BaseErrorCode {

    NOT_FOUND(HttpStatus.NOT_FOUND, "STATION404", "충전소를 찾지 못했습니다."),
    QUERY_BAD_REQUEST(HttpStatus.BAD_REQUEST, "STATION400", "충전소 쿼리가 잘못되었습니다.")
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ApiResponse<Void> getErrorResponse() {
        return ApiResponse.onFailure(code, message);
    }
}
