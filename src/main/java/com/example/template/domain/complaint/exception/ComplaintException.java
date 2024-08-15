package com.example.template.domain.complaint.exception;

import com.example.template.global.apiPayload.code.status.BaseErrorCode;
import com.example.template.global.apiPayload.exception.GeneralException;
import lombok.Getter;

@Getter
public class ComplaintException extends GeneralException {
    public ComplaintException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
