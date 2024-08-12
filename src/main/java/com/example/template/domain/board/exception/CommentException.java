package com.example.template.domain.board.exception;

import com.example.template.global.apiPayload.exception.GeneralException;
import lombok.Getter;

@Getter
public class CommentException extends GeneralException {
    public CommentException(CommentErrorCode errorCode) {
        super(errorCode);
    }
}
