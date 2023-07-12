package com.example.clubsite.exhandler.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CustomErrorResponseCode {
    SEND_MESSAGE_FAIL(40010, "CUSTOMED sendMessage Fail"),
    INVALID_USER_EMAIL(40011, "CUSTOMED ERROR no user with such email"),
    ALREADY_EXIST_CHATROOM(40012, "CUSTOMED ERROR already exist chatRoom resource"),
    ;

    private final Integer statusCode;
    private final String message;
}


