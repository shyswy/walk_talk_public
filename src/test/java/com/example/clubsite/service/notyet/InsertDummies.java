package com.example.clubsite.service.notyet;

import com.example.clubsite.dto.clubmember.ClubMemberDTO;
import com.example.clubsite.dto.clubmember.Token;
import com.example.clubsite.entity.ClubMember;
import com.example.clubsite.exhandler.errorcode.CustomErrorResponseCode;
import com.example.clubsite.exhandler.exception.CustomException;
import com.example.clubsite.repository.ChatRoomRepository;
import com.example.clubsite.repository.clubmember.ClubMemberRepository;
import com.example.clubsite.repository.memberchatroom.MemberChatRoomRepository;
import com.example.clubsite.utility.JWTUtil;
import com.example.clubsite.service.chat.ChatService;
import com.example.clubsite.service.clubmember.ClubMemberService;
import com.example.clubsite.service.friend.FriendService;
import com.example.clubsite.service.memberchatroom.MemberChatRoomService;
import lombok.extern.log4j.Log4j2;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
@Log4j2
@ActiveProfiles(profiles = "dv")
class InsertDummies {

    @Autowired
    FriendService friendService;
    @Autowired
    ClubMemberService clubMemberService;
    @Autowired
    ClubMemberRepository clubMemberRepository;
    @Autowired
    MemberChatRoomRepository memberChatRoomRepository;
    @Autowired
    MemberChatRoomService memberChatRoomService;
    @Autowired
    ChatRoomRepository chatRoomRepository;
    @Autowired
    ChatService chatService;
    @Autowired
    JWTUtil jwtUtil;

    @Value("${default.image.name}")
    private String DEFAULT_IMAGE;
    
    @Test
    void InsertSingleChatRoomDummy() { //multiple chatMember Test done.
        List<Long> memberIds = List.of(1L, 154L, 333L, 33L);
        memberIds = memberIds.stream().distinct().collect(Collectors.toList()); // 중복 값 제거 ( 1,1 과 같은 -> 자기자신과의 채팅방)
        Long myMemberId = 2L;
        Long chatRoomId = chatService.registerChatRoomAndMemberChatRoom(memberIds, myMemberId, "titleSingle" + String.valueOf(myMemberId));
    }
        @Test
    void insertMemberDummies() {

        int count = 50;
        Faker faker = new Faker();

        for (int i = 0; i < count; i++) {
            try {
                ClubMemberDTO clubMemberDTO = ClubMemberDTO.builder()
                        .email(faker.internet().emailAddress())
                        .stepCount((long) i)
                        .name(faker.name().fullName())
                        .profileImageFileUrl(DEFAULT_IMAGE)
                        .build();
                clubMemberService.saveByDto(clubMemberDTO);

                ClubMember member = clubMemberService.getByEmail(clubMemberDTO.getEmail()).orElseThrow(() -> new CustomException(CustomErrorResponseCode.INVALID_USER_EMAIL));


                Token token = jwtUtil.generateToken(member.getId());
                log.info(clubMemberDTO.getName() + ", access token: " + token.getAccessToken());
                clubMemberService.updateRefreshToken(member, token.refreshToken);
                clubMemberService.updateStep(member.getId(), (long) i);
            } catch (Exception e) {
                log.error("An error occurred while registering chat room: " + e.getMessage());
            }
        }
    }
}
