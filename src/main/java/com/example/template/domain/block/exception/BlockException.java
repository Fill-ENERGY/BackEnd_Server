package com.example.template.domain.block.exception;

import com.example.template.global.apiPayload.code.status.BaseErrorCode;
import com.example.template.global.apiPayload.exception.GeneralException;
import lombok.Getter;

@Getter
public class BlockException extends GeneralException {
    public BlockException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}