package com.example.template.domain.board.exception;

import com.example.template.global.apiPayload.code.status.BaseErrorCode;
import com.example.template.global.apiPayload.exception.GeneralException;
import lombok.Getter;

@Getter
public class BoardException extends GeneralException {
    public BoardException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
