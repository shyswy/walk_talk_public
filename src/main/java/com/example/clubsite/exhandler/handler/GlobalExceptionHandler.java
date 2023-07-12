package com.example.clubsite.exhandler.handler;


import com.example.clubsite.dto.response.RootResponse;
import com.example.clubsite.exhandler.errorcode.CommonErrorResponseCode;
import com.example.clubsite.exhandler.errorcode.ErrorResponseCode;
import com.example.clubsite.exhandler.errorcode.CustomErrorResponseCode;
import com.example.clubsite.exhandler.exception.CustomException;
import com.example.clubsite.exhandler.exception.RestApiException;
import com.example.clubsite.utility.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(RestApiException.class)
    public ResponseEntity<RootResponse> handleRestException(final RestApiException e) {
        return handleExceptionInternal(e.getErrorCode(), e.getMessage());
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<RootResponse> handleCUSTOMEDException(final CustomException e) {
        return handleExceptionInternal(e.getErrorCode(), e.getMessage());
    }

    @ExceptionHandler({EntityNotFoundException.class})
    public ResponseEntity<RootResponse> handleAllException(final EntityNotFoundException e) {
        final ErrorResponseCode errorCode = CommonErrorResponseCode.RESOURCE_NOT_FOUND;
        return handleExceptionInternal(errorCode, e.getMessage());
    }

    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<RootResponse> handleAllException(final IllegalArgumentException e) {
        log.error("IllegalArgumentException exception: message={}", "message", e);
        final ErrorResponseCode errorCode = CommonErrorResponseCode.INVALID_PARAMETER;
        return handleExceptionInternal(errorCode, e.getMessage());
    }

    @ExceptionHandler({DataIntegrityViolationException.class})
    public ResponseEntity<RootResponse> handleAllException(final DataIntegrityViolationException e) {
        log.error("DataIntegrityViolationException exception: message={}", "message", e);
        final ErrorResponseCode errorCode = CommonErrorResponseCode.DATABASE_INTEGRITY;
        return handleExceptionInternal(errorCode, e.getMessage());
    }

    @ExceptionHandler({IOException.class})
    public ResponseEntity<RootResponse> handleAllException(final IOException e) {
        log.error("IOException: message={}", "message", e);
        final ErrorResponseCode errorCode = CommonErrorResponseCode.IO_ERROR;
        return handleExceptionInternal(errorCode, e.getMessage());
    }

    @ExceptionHandler({Exception.class}) //그외의 에러들 ( unhandled )
    public ResponseEntity<RootResponse> handleAllException(final Exception e) {
        log.error("unknown exception: message={}", "message", e);
        final ErrorResponseCode errorCode = CommonErrorResponseCode.INTERNAL_SERVER_ERROR;
        return handleExceptionInternal(errorCode, e.getMessage());
    }

    private ResponseEntity<RootResponse> handleExceptionInternal(final CustomErrorResponseCode errorCode, String message) {
        return ResponseUtil.getInstance().getResponseEntity(errorCode.getStatusCode(), errorCode.getMessage(), null, null);
    }

    private ResponseEntity<RootResponse> handleExceptionInternal(final ErrorResponseCode errorCode, String message) {
        return ResponseUtil.getInstance().getResponseEntity(errorCode.getHttpStatus(), null, null);
    }
}



