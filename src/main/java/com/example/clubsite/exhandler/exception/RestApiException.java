package com.example.clubsite.exhandler.exception;

import com.example.clubsite.exhandler.errorcode.ErrorResponseCode;
import lombok.Getter;

@Getter
public class RestApiException extends RuntimeException {
    private final ErrorResponseCode errorCode;
    private final String message;


    public RestApiException(ErrorResponseCode errorCode) {
        this(errorCode, errorCode.getMessage());
    }

    public RestApiException(ErrorResponseCode errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }
}
