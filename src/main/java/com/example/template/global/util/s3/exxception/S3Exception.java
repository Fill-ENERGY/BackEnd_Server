package com.example.template.global.util.s3.exxception;

import com.example.template.global.apiPayload.code.status.BaseErrorCode;
import com.example.template.global.apiPayload.exception.GeneralException;
import lombok.Getter;

@Getter
public class S3Exception extends GeneralException {
    public S3Exception(BaseErrorCode errorCode) {
        super(errorCode);
    }
}