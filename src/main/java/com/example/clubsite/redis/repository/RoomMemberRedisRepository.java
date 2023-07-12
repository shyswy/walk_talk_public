package com.example.clubsite.redis.repository;

import com.example.clubsite.redis.object.ChatRoomCacheDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@RequiredArgsConstructor
@Repository
public class RoomMemberRedisRepository {   //extends CrudRepository<Member, String>
    private final RedisTemplate<String, List<ChatRoomCacheDTO>> redisTemplate;
    private final StringRedisTemplate commonRedisTemplate;


    public void save(String key, List<ChatRoomCacheDTO> clubMembers) {
        redisTemplate.opsForValue().set(key, clubMembers);
    }

    public List<ChatRoomCacheDTO> findAll(String key) {
        List<ChatRoomCacheDTO> ChatRoomCacheDtoS = redisTemplate.opsForValue().get(key);
        if (ChatRoomCacheDtoS == null) return new ArrayList<>();
        return ChatRoomCacheDtoS;
    }

    public List<String> getListOps(String key) {
        Long len = commonRedisTemplate.opsForList().size(key);
        return len == 0 ? new ArrayList<>() : commonRedisTemplate.opsForList().range(key, 0, len - 1);
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    } //deleteAll 제대로 수행 x 아닌가..? 1개만 수행?
}
