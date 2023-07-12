package com.example.clubsite.exhandler.errorcode;

import org.springframework.http.HttpStatus;

public interface ErrorResponseCode {
    String name();

    HttpStatus getHttpStatus();

    String getMessage();
}
