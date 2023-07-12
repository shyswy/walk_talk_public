package com.example.clubsite.dto.chatroom;

import com.example.clubsite.entity.MemberChatRoom;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class ChatRoomDTO {
    private Long chatRoomId;
    private String title;
    private int memberCount;
    private List<String> imageUrls;

    public static ChatRoomDTO of(MemberChatRoom memberChatRoom, List<String> imageUrlsExceptMe) {
        return ChatRoomDTO.builder()
                .memberCount(memberChatRoom.getChatRoom().getMemberCount())
                .title(memberChatRoom.getChatRoom().getTitle())
                .chatRoomId(memberChatRoom.getChatRoom().getId())
                .imageUrls(imageUrlsExceptMe)
                .build();
    }
}
