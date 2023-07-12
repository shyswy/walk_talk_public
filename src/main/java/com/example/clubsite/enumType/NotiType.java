package com.example.clubsite.enumType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum NotiType {
    NORMAL("NORMAL"),
    CHAT("CHAT"),
    ;

    private String value;
}