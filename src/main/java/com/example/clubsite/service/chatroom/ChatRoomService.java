package com.example.clubsite.service.chatroom;

import com.example.clubsite.entity.ChatRoom;


public interface ChatRoomService {
    void save(ChatRoom chatRoom);
    ChatRoom getById(Long chatRoomId);
    void delete(ChatRoom chatRoom);
}
