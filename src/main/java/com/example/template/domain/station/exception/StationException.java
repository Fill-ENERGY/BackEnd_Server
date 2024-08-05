package com.example.template.domain.station.exception;

import com.example.template.global.apiPayload.code.status.BaseErrorCode;
import com.example.template.global.apiPayload.exception.GeneralException;

public class StationException extends GeneralException {
    public StationException(BaseErrorCode code) {
        super(code);
    }
}
