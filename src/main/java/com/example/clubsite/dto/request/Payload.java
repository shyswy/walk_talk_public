package com.example.clubsite.dto.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payload {
    private String notiType;
    private Long chatId;
    private String nickName;
    private String imagePath;
    private String message;
    private String linkUrl;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payload payload = (Payload) o;
        return Objects.equals(getNotiType(), payload.getNotiType()) && Objects.equals(getChatId(), payload.getChatId()) && Objects.equals(getNickName(), payload.getNickName()) && Objects.equals(getImagePath(), payload.getImagePath()) && Objects.equals(getMessage(), payload.getMessage()) && Objects.equals(getLinkUrl(), payload.getLinkUrl());
    }
    @Override
    public int hashCode() {
        return Objects.hash(getNotiType(), getChatId(), getNickName(), getImagePath(), getMessage(), getLinkUrl());
    }
}
