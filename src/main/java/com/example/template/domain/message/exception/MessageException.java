package com.example.template.domain.message.exception;

import com.example.template.global.apiPayload.code.status.BaseErrorCode;
import com.example.template.global.apiPayload.exception.GeneralException;
import lombok.Getter;

@Getter
public class MessageException extends GeneralException {
    public MessageException(MessageErrorCode errorCode) {
        super(errorCode);
    }
}
