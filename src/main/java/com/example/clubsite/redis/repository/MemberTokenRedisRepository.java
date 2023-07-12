package com.example.clubsite.redis.repository;

import com.example.clubsite.redis.object.RoomMemberDTO;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MemberTokenRedisRepository extends CrudRepository<RoomMemberDTO, Long> {
    List<RoomMemberDTO> findMemberCachesByFcmToken(String fcmToken);
}
