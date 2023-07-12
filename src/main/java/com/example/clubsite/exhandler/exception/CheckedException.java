package com.example.clubsite.exhandler.exception;

import com.example.clubsite.exhandler.errorcode.CheckedErrorResponseCode;

public class CheckedException extends Exception {
    private final CheckedErrorResponseCode errorCode;
    private final String message;

    public CheckedException(CheckedErrorResponseCode errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public CheckedException(CheckedErrorResponseCode errorCode) {
        this(errorCode, errorCode.getMessage());
    }
}
