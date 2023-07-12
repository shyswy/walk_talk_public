package com.example.clubsite.dto.chat;

import com.example.clubsite.entity.Chat;
import com.example.clubsite.entity.ClubMember;
import com.example.clubsite.exhandler.errorcode.CommonErrorResponseCode;
import com.example.clubsite.exhandler.exception.RestApiException;
import com.example.clubsite.utility.UrlUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@NoArgsConstructor
@AllArgsConstructor

@Builder
public class ChatDTO {
    private String nickName;
    private String imagePath;
    @JsonProperty("isMe")
    private boolean isMe; // @getter 사용시, isMe 이름으로 메소드 등록되서, me, isme 2개 필드 생긴다.. getter 수동으로 해서 해결!
    private String message;
    private LocalDateTime createdDate;

    public String getNickName() {
        return nickName;
    }

    public String getImagePath() {
        return imagePath;
    }


    public boolean isIsMe() { //isMe로 하면, me 필드도 생성된다!
        return isMe;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void changeImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public static ChatDTO of(Chat chat, Long myId, UrlUtil urlUtil) {
        ClubMember clubMember = chat.getClubMember();
        if (clubMember == null) throw new RestApiException(CommonErrorResponseCode.RESOURCE_NOT_FOUND);
        return ChatDTO.builder()   //각 유저마다 DTO 생성 -> list
                .nickName(clubMember.getName())
                .imagePath(urlUtil.getProfileUrl() + clubMember.getProfileFileName())
                .isMe(myId.equals(clubMember.getId()))
                .message(chat.getMessage())
                .createdDate(chat.getCreatedDate()) //chat방 생성 날짜 or 유저 생성 날짜
                .build();
    }


}


