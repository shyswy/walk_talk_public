package com.example.clubsite.redis.clubmemberdto;

import com.example.clubsite.dto.clubmember.ClubMemberDTO;
import com.example.clubsite.entity.ClubMember;
import com.example.clubsite.redis.repository.MemberDtoRedisRepository;
import com.example.clubsite.utility.UrlUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberDtoRedisService {
    private final MemberDtoRedisRepository memberDtoRedisRepository;

    private final UrlUtil urlUtil;

    public static final String DEVELOP = "st";
    @Value("${spring.profile}")
    private String activeProfile;

    public void save(ClubMember clubMember) {
        try {
            memberDtoRedisRepository.save(ClubMemberDTO.of(clubMember, urlUtil));
        } catch (Exception e) {
            log.error("save cache error!");
        }
    }

    public void update(ClubMember clubMember) {
        try {
            ClubMemberDTO clubMemberDTO = memberDtoRedisRepository.findById(clubMember.getId()).orElse(ClubMemberDTO.of(clubMember,urlUtil));
            memberDtoRedisRepository.save(clubMemberDTO);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("error in update cache!");
        }
    }

    public void updateFcmToken(ClubMember clubMember) {
        try {
            ClubMemberDTO clubMemberDTO = memberDtoRedisRepository.findById(clubMember.getId()).orElse(ClubMemberDTO.of(clubMember,urlUtil));
            String newFcmToken = clubMember.getUserToken().getFcmToken();
            if (!newFcmToken.equals(clubMemberDTO.getFcmToken()))
                clubMemberDTO.changeFcmToken(newFcmToken);
            memberDtoRedisRepository.save(clubMemberDTO);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("error in update cache!");
        }
    }

    public void updateRefreshToken(ClubMember clubMember) {
        try {
            String newRefreshToken = clubMember.getUserToken().getRefreshToken();
           ClubMemberDTO memberDTO = memberDtoRedisRepository.findById(clubMember.getId()).orElse(ClubMemberDTO.of(clubMember, urlUtil));
            if (!newRefreshToken.equals(memberDTO.getRefreshToken()))
                memberDTO.changeRefreshToken(newRefreshToken);
            memberDtoRedisRepository.save(memberDTO);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("error in update cache!");
        }
    }




    public Optional<ClubMemberDTO> findById(Long clubMemberId) {
        try {
            return memberDtoRedisRepository.findById(clubMemberId);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public void deleteExceptMe(String fcmToken, Long userTokenId) {
        try {
            List<ClubMemberDTO> findList = memberDtoRedisRepository.findMemberCachesByFcmToken(fcmToken);
            for (ClubMemberDTO clubMemberDTO : findList) {
                if (clubMemberDTO.getTokenId().equals(userTokenId)) continue;
                memberDtoRedisRepository.delete(clubMemberDTO);
            }
        } catch (Exception e) {
            log.error("error in delete cache!");
        }
    }

    public void delete(ClubMember clubMember){
        memberDtoRedisRepository.delete(ClubMemberDTO.of(clubMember,urlUtil));
    }

//    private static MemberTokenCacheDTO memberTokenCacheDtoBuild(UserToken userToken, Long memberId) {
//        return MemberTokenCacheDTO.builder()
//                .id(memberId)
//                .tokenId(userToken.getId())
//                .fcmToken(userToken.getFcmToken())
//                .refreshToken(userToken.getRefreshToken())
//                .build();
//    }
}
