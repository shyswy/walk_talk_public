package com.example.clubsite.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class MemberIdsChatRoomRequest {
    private Long chatRoomId;
    private List<Long> memberIds;
}
