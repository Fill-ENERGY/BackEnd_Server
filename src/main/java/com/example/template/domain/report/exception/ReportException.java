package com.example.template.domain.report.exception;

import com.example.template.global.apiPayload.code.status.BaseErrorCode;
import com.example.template.global.apiPayload.exception.GeneralException;
import lombok.Getter;

@Getter
public class ReportException extends GeneralException {
    public ReportException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
