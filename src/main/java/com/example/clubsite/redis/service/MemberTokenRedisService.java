package com.example.clubsite.redis.service;

import com.example.clubsite.entity.UserToken;
import com.example.clubsite.redis.object.RoomMemberDTO;
import com.example.clubsite.redis.repository.MemberTokenRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberTokenRedisService {
    private final MemberTokenRedisRepository memberTokenRedisRepository;

    public static final String DEVELOP = "st";
    @Value("${spring.profile}")
    private String activeProfile;

    public void setMemberTokenCacheByClubMember(UserToken userToken, Long memberId) {
        try {
            memberTokenRedisRepository.save(memberTokenCacheDtoBuild(userToken, memberId));
        } catch (Exception e) {
            log.error("save cache error!");
        }
    }

    public void updateFcmTokenCacheByClubMember(UserToken userToken, Long memberId) {
        try {
            RoomMemberDTO roomMemberDTO = memberTokenRedisRepository.findById(memberId).orElse(memberTokenCacheDtoBuild(userToken, memberId));
            if (userToken.getFcmToken().equals(roomMemberDTO.getFcmToken()))
                roomMemberDTO.changeFcmToken(userToken.getFcmToken());
            memberTokenRedisRepository.save(roomMemberDTO);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("error in update cache!");
        }
    }

    public void updateRefreshTokenCacheByClubMember(UserToken userToken, Long memberId) {
        try {
            RoomMemberDTO roomMemberDTO = memberTokenRedisRepository.findById(memberId).orElse(memberTokenCacheDtoBuild(userToken, memberId));
            if (!userToken.getRefreshToken().equals(roomMemberDTO.getRefreshToken()))
                roomMemberDTO.changeRefreshToken(userToken.getRefreshToken());
            memberTokenRedisRepository.save(roomMemberDTO);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("error in update cache!");
        }
    }


    public Optional<RoomMemberDTO> getMemberTokenCacheByClubMemberId(Long clubMemberId) {
        try {
            return memberTokenRedisRepository.findById(clubMemberId);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public void deleteMemberTokenCachesExceptMeByFcmToken(String fcmToken, Long userTokenId) {
        try {
            List<RoomMemberDTO> findList = memberTokenRedisRepository.findMemberCachesByFcmToken(fcmToken);
            for (RoomMemberDTO roomMemberDTO : findList) {
                if (roomMemberDTO.getTokenId().equals(userTokenId)) continue;
                memberTokenRedisRepository.delete(roomMemberDTO);
            }
        } catch (Exception e) {
            log.error("error in delete cache!");
        }
    }

    private static RoomMemberDTO memberTokenCacheDtoBuild(UserToken userToken, Long memberId) {
        return RoomMemberDTO.builder()
                .id(memberId)
                .tokenId(userToken.getId())
                .fcmToken(userToken.getFcmToken())
                .refreshToken(userToken.getRefreshToken())
                .build();
    }
}
