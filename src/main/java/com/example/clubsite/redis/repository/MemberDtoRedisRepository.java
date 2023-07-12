package com.example.clubsite.redis.repository;

import com.example.clubsite.dto.clubmember.ClubMemberDTO;
import com.example.clubsite.redis.object.RoomMemberDTO;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface MemberDtoRedisRepository extends CrudRepository<ClubMemberDTO, Long> {
    List<ClubMemberDTO> findMemberCachesByFcmToken(String fcmToken);
}
