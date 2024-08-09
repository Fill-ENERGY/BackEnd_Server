package com.example.template.domain.review.exception;

import com.example.template.global.apiPayload.exception.GeneralException;

public class ReviewException extends GeneralException {
    public ReviewException(ReviewErrorCode code) {
        super(code);
    }

}
