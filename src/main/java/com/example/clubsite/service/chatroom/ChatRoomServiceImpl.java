package com.example.clubsite.service.chatroom;

import com.example.clubsite.entity.ChatRoom;
import com.example.clubsite.exhandler.errorcode.CommonErrorResponseCode;
import com.example.clubsite.exhandler.exception.RestApiException;
import com.example.clubsite.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Log4j2
public class ChatRoomServiceImpl implements ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;

    @Override
    public void save(ChatRoom chatRoom) {
        chatRoomRepository.save(chatRoom);
    }

    @Override
    @Transactional(readOnly = true)//ok
    public ChatRoom getById(Long chatRoomId) {
        return chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new RestApiException(CommonErrorResponseCode.RESOURCE_NOT_FOUND));
    }

    @Override
    public void delete(ChatRoom chatRoom) {
        chatRoomRepository.delete(chatRoom);
    }
}
