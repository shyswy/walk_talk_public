package com.example.clubsite.redis.object;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ChatRoomCacheDTO {
    private Long chatRoomId;
    private Long clubMemberId;
}

