package com.example.clubsite.dto.chat;

import com.example.clubsite.dto.chat.ChatDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@Builder
public class ChatListDTO {
    private Long chatId;
    private String title;
    private List<ChatDTO> chats;

    public ChatListDTO(Long chatId,String title, List<ChatDTO> chats) {
        this.chatId = chatId;
        this.title=title;
        this.chats = chats;
    }
}
