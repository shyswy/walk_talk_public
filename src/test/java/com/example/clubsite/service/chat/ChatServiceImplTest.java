package com.example.clubsite.service.chat;


import com.example.clubsite.dto.request.Payload;
import com.example.clubsite.entity.*;
import com.example.clubsite.enumType.NotiType;
import com.example.clubsite.exhandler.exception.CheckedException;
import com.example.clubsite.redis.object.ChatRoomCacheDTO;
import com.example.clubsite.redis.service.MemberTokenRedisService;
import com.example.clubsite.redis.service.RoomMemberRedisService;
import com.example.clubsite.repository.ChatRepository;
import com.example.clubsite.service.fcm.FCMService;
import com.example.clubsite.service.chatroom.ChatRoomService;
import com.example.clubsite.service.clubmember.ClubMemberService;
import com.example.clubsite.service.memberchatroom.MemberChatRoomService;
import com.example.clubsite.utility.UrlUtil;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.mockito.Mockito.*;


@Slf4j
class ChatServiceImplTest {
    @Mock
    private ChatRoomService chatRoomService;

    @Mock
    private ClubMemberService clubMemberService;
    @Mock
    private ChatRepository chatRepository;

    @Mock
    private FCMService fcmService;

    @Mock
    private RoomMemberRedisService roomMemberRedisService;
    @Mock
    private MemberChatRoomService memberChatRoomService;

    @Mock
    private UrlUtil urlUtil;

    @Mock
    MemberTokenRedisService memberTokenRedisService;


    //@Spy//스터빙을 안 한 테스트는 기존 객체의 로직을, 스터빙을 한 테스트는 스터빙 값을 리턴
    @InjectMocks //mock을 주입받아 실제 객체 생성
    @Spy
    private ChatServiceImpl chatService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void addChatTest() {
        // given
        Long chatRoomId = 1L;
        String message = "hello";

        List<Long> receiverIds = new ArrayList<>(Arrays.asList(2L, 3L));//자신 외의 채팅방 내 멤버들 설정
        List<ClubMember> receivers = buildMembersWithUserTokenByIds(receiverIds); //멤버와 그 연관된 객체를 임의로 생성.
        ChatRoom chatRoom = ChatRoom.builder().chats(Collections.emptyList()).build();
        ClubMember sender = makeClubMember(1L, "sender@gmail.com", "senderTestName", "senderTestImage"); //메세지 전송하는 사람
        Chat chat = Chat.builder().chatRoom(chatRoom).clubMember(sender).message(message).build();//sender가 전송한 hello 메세지 객체 생성.

        // when
        when(chatRoomService.getById(chatRoomId)).thenReturn(chatRoom);
        when(clubMemberService.getById(sender.getId())).thenReturn(sender);
        when(roomMemberRedisService.findChatRoomMembersCacheByChatRoomId(chatRoomId)).thenReturn(new ArrayList<>());//채팅방 멤버 캐시 비어있을 때 가정.
        when(chatRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0)); //save 성공 가정 -> 바로 엔티티 반환


        List<ChatRoomCacheDTO> allMemberInChatRoom = getChatRoomCacheDTOs(chatRoomId,sender.getId(), receiverIds);//전송자, 그외 멤버들 로 채팅 방 멤버 객체 생성
        when(memberChatRoomService.getMembersInChatRoomAsChatRoomCacheDTOs(chatRoomId)).thenReturn(allMemberInChatRoom); //바로 객체 가져오기


        when(urlUtil.getProfileUrlWithUrl(anyString())).thenAnswer(invocation -> invocation.getArgument(0)); //이미지 파일 그냥 그대로( 앞에 url x)

        when(memberTokenRedisService.getMemberTokenCacheByClubMemberId(any())).thenReturn(Optional.empty());// 캐시 존재 x 가정
        getAllClubmemberWithUserTokenByIdMock(receivers);
        try {
            doNothing().when(fcmService).sendMessageTo(any(), anyString(), anyString(), anyString());// void 메소드가 아무것도 수행하지 않게 하기
        } catch (CheckedException e) {
            throw new RuntimeException(e);
        }
        //then
        chatService.addChat(sender.getId(), chatRoomId, message);

        for (ClubMember receiver : receivers) {
            try {
                verify(fcmService, times(1)).sendMessageTo( //자신을 제외한 유저 2명에게, 메세지 전송 메서드가 각각 해서 2번 전송?
                        eq(payloadBuilder(chatRoomId, sender, chat)),
                        eq(receiver.getUserToken().getFcmToken()),
                        eq("walkTalk: " + sender.getName()),
                        eq(chat.getMessage())
                );
            } catch (CheckedException e) {
                throw new RuntimeException(e);
            }
        }
    }


//    @Test
//    void addChatTest() throws CheckedException {//BuisnessException 없애고 에러 테스트 수행하자! ( 몇명 성공, 몇명 실패 케이스 )
//        // given
//        Long chatRoomId = 1L;
//        String message = "hello";
//
//        List<Long> receiverIds = new ArrayList<>(Arrays.asList(2L, 3L));//자신 외의 채팅방 내 멤버들 설정
//        List<ClubMember> receivers = buildMembersWithUserTokenByIds(receiverIds); //멤버와 그 연관된 객체를 임의로 생성.
//        ChatRoom chatRoom = ChatRoom.builder().chats(Collections.emptyList()).build();
//        ClubMember sender = makeClubMember(1L, "sender@gmail.com", "senderTestName", "senderTestImage"); //메세지 전송하는 사람
//        Chat chat = Chat.builder().chatRoom(chatRoom).clubMember(sender).message(message).build();//sender가 전송한 hello 메세지 객체 생성.
//
//        // when
//        when(chatRoomService.findEntityById(chatRoomId)).thenReturn(chatRoom);
//        when(clubMemberService.findEntityById(sender.getId())).thenReturn(sender);
//        when(roomMemberRedisService.findChatRoomMembersCacheByChatRoomId(chatRoomId)).thenReturn(new ArrayList<>());//채팅방 멤버 캐시 비어있을 때 가정.
//        when(chatRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0)); //save 성공 가정 -> 바로 엔티티 반환
//
//
//        List<ChatRoomCacheDTO> allMemberInChatRoom = getChatRoomCacheDTOs(chatRoomId,sender.getId(), receiverIds);//전송자, 그외 멤버들 로 채팅 방 멤버 객체 생성
//        when(memberChatRoomService.getAllMembersInChatRoomAsChatRoomCacheDTOs(chatRoomId)).thenReturn(allMemberInChatRoom); //바로 객체 가져오기
//
//
//        when(urlUtil.getProfileUrlWithUrl(anyString())).thenAnswer(invocation -> invocation.getArgument(0)); //이미지 파일 그냥 그대로( 앞에 url x)
//
//        when(memberTokenRedisService.getMemberTokenCacheByClubMemberId(any())).thenReturn(Optional.empty());// 캐시 존재 x 가정
//
//
//        getAllClubmemberWithUserTokenByIdMock(receivers);
//
//        doNothing().when(fcmService).sendMessageTo(any(), anyString(), anyString(), anyString());// void 메소드가 아무것도 수행하지 않게 하기
//
//
//        //then
//        chatService.addChat(sender.getId(), chatRoomId, message);
//
//        for (ClubMember receiver : receivers) {
//            verify(fcmService, times(1)).sendMessageTo( //자신을 제외한 유저 2명에게, 메세지 전송 메서드가 각각 해서 2번 전송?
//                    eq(payloadBuilder(chatRoomId, sender, chat)),
//                    eq(receiver.getUserToken().getFcmToken()),
//                    eq("walkTalk: " + sender.getName()),
//                    eq(chat.getMessage())
//            );
//        }
//    }

    @NotNull
    private static List<ClubMember> buildMembersWithUserTokenByIds(List<Long> receiverIds) {
        List<ClubMember> receivers=new ArrayList<>();
        for (Long receiverId : receiverIds) {
            receivers.add(buildClubMemberWithUserToken(receiverId));
        }
        return receivers;
    }


    private List<ChatRoomCacheDTO> getChatRoomCacheDTOs(Long chatRoomId,Long sender1, List<Long> receivers) {
        List<ChatRoomCacheDTO> allMemberInChatRoom = new ArrayList<>();
        allMemberInChatRoom.add(new ChatRoomCacheDTO(chatRoomId, sender1));
        for (Long receiver : receivers) {
            allMemberInChatRoom.add(new ChatRoomCacheDTO(chatRoomId, receiver));
        }
        return allMemberInChatRoom;
    }

    private static ClubMember makeClubMember(Long id, String email, String name, String imageName) {
        ClubMember registeredClubMember = ClubMember.builder()
                .id(id)
                .email(email)
                .name(name)
                .profileFileName(imageName)
                .build();
        return registeredClubMember;
    }

    private static ClubMember buildClubMemberWithUserToken(Long id) {
        Faker faker = new Faker();
        ClubMember clubMember = ClubMember.builder()
                .id(id)
                .email(faker.internet().emailAddress())
                .name(faker.name().fullName())
                .profileFileName("NOIMAGE")
                .build();
        UserToken userToken = UserToken.builder().fcmToken("testFcmToken:"+clubMember.getName()).refreshToken("testRefreshToken:"+clubMember.getName()).build();
        clubMember.setUserToken(userToken);
        return clubMember;
    }

    private void getAllClubmemberWithUserTokenByIdMock(List<ClubMember> receivers) {
        for (ClubMember receiver : receivers) {
            when(clubMemberService.getByIdWithUserToken(receiver.getId())).thenAnswer(invocation -> { //주어진 id 에 맞는 moc 객체 가져오기
                Long memberId = invocation.getArgument(0);
                return receiver;
            });
        }
    }

    private Payload payloadBuilder(Long chatRoomId, ClubMember clubMember, Chat chat) {
        return Payload.builder()
                .chatId(chatRoomId)
                .nickName(clubMember.getName())
                .message(chat.getMessage())
                .imagePath(clubMember.getProfileFileName())
                .notiType(NotiType.CHAT.name())
                .linkUrl("not yet")
                .build();
    }
}
