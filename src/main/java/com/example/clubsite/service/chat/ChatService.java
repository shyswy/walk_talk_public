package com.example.clubsite.service.chat;

import com.example.clubsite.dto.request.MemberIdsRequest;
import com.example.clubsite.dto.response.ChatRoomIdResponse;
import com.example.clubsite.dto.chat.ChatDTO;
import com.example.clubsite.dto.chat.ChatListDTO;
import com.example.clubsite.entity.Chat;

import java.util.List;

public interface ChatService {
    ChatListDTO enterChatRoom(Long chatRoomId, Long myId);

    ChatRoomIdResponse makeChatRoom(Long myMemberId, MemberIdsRequest request);

    Long registerChatRoomAndMemberChatRoom(List<Long> memberIds, Long myMemberId, String title);

    List<ChatDTO> chatsToChatDTOs(List<Chat> result, Long myId);

    void addChat(Long clubMemberId, Long chatRoomId, String message);

    List<Chat> getByChatRoomIdWithClubMember(Long chatRoomId);

    void save(Chat chat);
}
