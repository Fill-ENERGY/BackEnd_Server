package com.example.template.domain.func1.exception;

import com.example.template.global.apiPayload.code.status.BaseErrorCode;
import com.example.template.global.apiPayload.exception.GeneralException;
import lombok.Getter;

@Getter
public class FuncException extends GeneralException {
    public FuncException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
