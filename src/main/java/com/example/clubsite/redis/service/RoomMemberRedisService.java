package com.example.clubsite.redis.service;

import com.example.clubsite.redis.object.ChatRoomCacheDTO;
import com.example.clubsite.redis.repository.RoomMemberRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomMemberRedisService {
    public static final String CHATROOMCACHEKEY = "chatroom:";
    private final RoomMemberRedisRepository roomMemberRedisRepository;

    public static final String DEVELOP = "st";
    @Value("${spring.profile}")
    private String activeProfile;

    public void saveChatRoomMembersCache(Long chatRoomId, List<ChatRoomCacheDTO> chatRoomCacheDTOS) {
        try{
            String cacheKey = CHATROOMCACHEKEY + chatRoomId.toString();
            roomMemberRedisRepository.save(cacheKey, chatRoomCacheDTOS);
        } catch (Exception e) {
            log.error("save cache error!");
        }
    }

    public List<ChatRoomCacheDTO> findChatRoomMembersCacheByChatRoomId(Long chatRoomId) {
        try {
            String cacheKey = CHATROOMCACHEKEY + chatRoomId.toString();
            return roomMemberRedisRepository.findAll(cacheKey);
        } catch (Exception e) {
            log.error("find cache error!");
            return new ArrayList<>();
        }
    }

    public void cacheDelete(Long chatRoomId) {
        try {
            String cacheKey = CHATROOMCACHEKEY + chatRoomId.toString();
            roomMemberRedisRepository.delete(cacheKey);
        } catch (Exception e) {
            log.error("delete cache error!");
        }
    }
}
