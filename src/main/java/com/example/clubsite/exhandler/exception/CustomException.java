package com.example.clubsite.exhandler.exception;

import com.example.clubsite.exhandler.errorcode.CustomErrorResponseCode;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final CustomErrorResponseCode errorCode;
    private final String message;

    public CustomException(CustomErrorResponseCode errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public CustomException(CustomErrorResponseCode errorCode) {
        this(errorCode, errorCode.getMessage());
    }
}
