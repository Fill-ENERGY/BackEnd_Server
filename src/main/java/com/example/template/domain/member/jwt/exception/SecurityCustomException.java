package com.example.template.domain.member.jwt.exception;

import com.example.template.global.apiPayload.code.status.BaseErrorCode;
import com.example.template.global.apiPayload.exception.GeneralException;
import lombok.Getter;

@Getter
public class SecurityCustomException extends GeneralException {

    private final Throwable cause;

    public SecurityCustomException(BaseErrorCode errorCode) {
        super(errorCode);
        this.cause = null;
    }

    public SecurityCustomException(BaseErrorCode errorCode, Throwable cause) {
        super(errorCode);
        this.cause = cause;
    }
}