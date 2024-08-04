package com.example.template.domain.member.exception;

import com.example.template.global.apiPayload.code.status.BaseErrorCode;
import com.example.template.global.apiPayload.exception.GeneralException;

public class MemberException extends GeneralException {

    public MemberException(MemberErrorCode errorCode){
        super(errorCode);
    }
}
