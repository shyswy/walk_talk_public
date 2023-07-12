package com.example.clubsite.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RankNumChatRoomIdRequest {
    private int rankNum;
    private Long chatRoomId;
}
