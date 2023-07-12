package com.example.clubsite.dto.friend;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class FriendDTO {
    private Long friendId;
    private Long toMemberId;
    private Long fromMemberId;
}
