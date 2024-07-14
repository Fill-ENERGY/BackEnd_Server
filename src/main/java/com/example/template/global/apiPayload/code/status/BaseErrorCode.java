package com.example.template.global.apiPayload.code.status;

import com.example.template.global.apiPayload.ApiResponse;
import org.springframework.http.HttpStatus;

public interface BaseErrorCode {

    HttpStatus getHttpStatus();

    String getCode();

    String getMessage();

    ApiResponse<Void> getErrorResponse();
}
