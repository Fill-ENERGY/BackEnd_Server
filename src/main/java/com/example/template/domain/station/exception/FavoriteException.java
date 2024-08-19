package com.example.template.domain.station.exception;

import com.example.template.global.apiPayload.exception.GeneralException;

public class FavoriteException extends GeneralException {
    public FavoriteException(FavoriteErrorCode code) {
        super(code);
    }
}
