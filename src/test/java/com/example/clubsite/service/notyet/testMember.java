package com.example.clubsite.service.notyet;

import com.example.clubsite.dto.clubmember.Token;
import com.example.clubsite.entity.ChatRoom;
import com.example.clubsite.entity.ClubMember;
import com.example.clubsite.enumType.ClubMemberRole;
import com.example.clubsite.redis.service.MemberTokenRedisService;
import com.example.clubsite.repository.memberchatroom.MemberChatRoomRepository;
import com.example.clubsite.service.clubmember.ClubMemberService;
import com.example.clubsite.utility.JWTUtil;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;

@SpringBootTest
@Log4j2
@ActiveProfiles(profiles = "dv")
class testMember {


    @Autowired
    ClubMemberService clubMemberService;


    @Autowired
    MemberChatRoomRepository memberChatRoomRepository;

    @Autowired
    MemberTokenRedisService memberTokenRedisService;


    @Autowired
    JWTUtil jwtUtil;

    @Test
    void findMemberCacheTest() {
        clubMemberService.getById(162L);
    }

    @Test
    void checkDuplicateKeyError() { //중복된 id 가 save 시 업데이트 된다.
        ClubMember clubMember = ClubMember.builder()
                .id(1L)
                .email("duplicateKey5@gmail.com")
                .name("duplicateKeyName5")
                .profileFileName(null)//clubMemberDTO.getProfileImageFileUrl()
                .build();
        clubMember.addMemberRole(ClubMemberRole.USER);// 회원가입한 멤버는 기본 유저로 역할 설정

        Token token = jwtUtil.generateToken(clubMember.getId());
        log.info(clubMember.getName() + ", access token: " + token.getAccessToken());
        clubMemberService.updateRefreshToken(clubMember, token.refreshToken);
        // clubMemberService.createStepIfNotExist(clubMember);
        clubMemberService.save(clubMember);
    }

    @Test
    void membersQueryTest() {
        ChatRoom findchatRoom = memberChatRoomRepository.findByClubMembers(Arrays.asList(1L, 2L), 2L).get();
        log.info("id: " + findchatRoom.getId());
    }
}






