package com.example.clubsite.exhandler.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommonErrorResponseCode implements ErrorResponseCode {
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "Invalid parameter included"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED"),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "Resource not exists"),
    DATABASE_INTEGRITY(HttpStatus.INTERNAL_SERVER_ERROR, "database integrity violation error"),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "Bad Request"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error"),
    IO_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "IO Exception occur"),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
