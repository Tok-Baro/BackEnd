package com.api.tokbaro.global.exception;

import com.api.tokbaro.global.response.code.BaseResponseCode;

public class CustomException extends BaseException {

    public CustomException(BaseResponseCode errorCode) {
        super(errorCode);
    }
}