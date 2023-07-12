package com.example.clubsite.exhandler.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CheckedErrorResponseCode {
    CHECKED_IO_ERROR(50000, "IO Exception occur but checked"),
    SEND_MESSAGE_FAIL(50001, "Business Exception: sendMessage Fail");
    private final Integer statusCode;
    private final String message;
}
