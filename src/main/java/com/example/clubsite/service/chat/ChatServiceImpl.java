package com.example.clubsite.service.chat;

import com.example.clubsite.dto.chat.ChatDTO;
import com.example.clubsite.dto.chat.ChatListDTO;
import com.example.clubsite.dto.request.MemberIdsRequest;
import com.example.clubsite.dto.request.Payload;
import com.example.clubsite.dto.response.ChatRoomIdResponse;
import com.example.clubsite.entity.Chat;
import com.example.clubsite.entity.ChatRoom;
import com.example.clubsite.entity.ClubMember;
import com.example.clubsite.entity.MemberChatRoom;
import com.example.clubsite.enumType.NotiType;
import com.example.clubsite.exhandler.errorcode.CommonErrorResponseCode;
import com.example.clubsite.exhandler.errorcode.CustomErrorResponseCode;
import com.example.clubsite.exhandler.exception.CheckedException;
import com.example.clubsite.exhandler.exception.CustomException;
import com.example.clubsite.exhandler.exception.RestApiException;
import com.example.clubsite.redis.object.ChatRoomCacheDTO;
import com.example.clubsite.redis.object.RoomMemberDTO;
import com.example.clubsite.redis.service.MemberTokenRedisService;
import com.example.clubsite.redis.service.RoomMemberRedisService;
import com.example.clubsite.repository.ChatRepository;
import com.example.clubsite.service.fcm.FCMService;
import com.example.clubsite.service.chatroom.ChatRoomService;
import com.example.clubsite.service.clubmember.ClubMemberService;
import com.example.clubsite.service.memberchatroom.MemberChatRoomService;
import com.example.clubsite.utility.UrlUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class ChatServiceImpl implements ChatService {
    private final FCMService fcmService;
    private final ClubMemberService clubMemberService;
    private final ChatRoomService chatRoomService;
    private final MemberChatRoomService memberChatRoomService;
    private final ChatRepository chatRepository;
    private final UrlUtil urlUtil;
    private final RoomMemberRedisService roomMemberRedisService;

    private final MemberTokenRedisService memberTokenRedisService;

    @Value("${default.image.name}")
    private String DEFAULT_IMAGE;

    @Override
    @NotNull
    @Transactional//ok
    public ChatListDTO enterChatRoom(Long chatRoomId, Long myId) {
        ChatRoom findChatRoom = chatRoomService.getById(chatRoomId);
        List<ChatRoomCacheDTO> allMemberInChatRoom = roomMemberRedisService.findChatRoomMembersCacheByChatRoomId(chatRoomId);
        checkAndSaveCache(chatRoomId, myId, allMemberInChatRoom);
        List<ChatDTO> chats = this.chatsToChatDTOs(this.getByChatRoomIdWithClubMember(chatRoomId), myId);
        return new ChatListDTO(chatRoomId, findChatRoom.getTitle(), chats);
    }

    @Override
    @NotNull
    @Transactional//ok
    public ChatRoomIdResponse makeChatRoom(Long myMemberId, MemberIdsRequest request) {
        List<Long> memberIds = getDistinctMemberList(request);
        return new ChatRoomIdResponse(registerChatRoomAndMemberChatRoom(memberIds, myMemberId, request.getTitle()));
    }


    @Override
    public Long registerChatRoomAndMemberChatRoom(List<Long> memberIds, Long myMemberId, String title) {
        if (!memberIds.contains(myMemberId)) {
            memberIds.add(myMemberId);
        }
        List<ClubMember> clubMembers = clubMemberService.getMembersEntitiesByIds(memberIds);
        //checkDuplicatedChatRoom(memberIds); 중복 채팅방 체크 수행 메소드 제외, 요구에따라 변경가능.
        ChatRoom chatRoom = buildChatRoomAndSave(title);
        buildAndSaveMemberChatRoom(clubMembers, chatRoom);
        return chatRoom.getId();
    }

    public List<Chat> getByChatRoomIdWithClubMember(Long chatRoomId) {
        return chatRepository.findWithClubMemberByChatRoomId(chatRoomId);
    }

    @Override
    @Transactional//ok
    public void addChat(Long clubMemberId, Long chatRoomId, String message) {//fcm async
        ChatRoom chatRoom = chatRoomService.getById(chatRoomId);//1
        ClubMember clubMember = clubMemberService.getById(clubMemberId);//2
        Chat chat = createChat(message, chatRoom, clubMember);
        save(chat); //채팅 저장.

        List<ChatRoomCacheDTO> allMemberInChatRoom = roomMemberRedisService.findChatRoomMembersCacheByChatRoomId(chatRoomId);
        allMemberInChatRoom = saveNewCacheAndReturnValue(chatRoomId, allMemberInChatRoom);
        if (allMemberInChatRoom.size() != 1)
            sendMessageToMemberExceptMe(chatRoomId, clubMember, chat, allMemberInChatRoom);
    }

    public void sendMessageToMemberExceptMe(Long chatRoomId, ClubMember clubMember, Chat chat, List<ChatRoomCacheDTO> allMemberInChatRoom) {
        for (ChatRoomCacheDTO chatRoomMemberExceptMe : allMemberInChatRoom) {//id, fcmToken ->FcmDTO
            Long findMemberId = chatRoomMemberExceptMe.getClubMemberId();
            if (clubMember.getId().equals(findMemberId)) continue;
            String findFcmToken = null;
            findFcmToken = getOrSaveCache(findMemberId);
            if (findFcmToken == null) continue;
            try {
                fcmService.sendMessageTo(//fcmService -> 가독성.  -> body에 전부담기..?
                        payloadBuilder(chatRoomId, clubMember, chat),
                        findFcmToken,
                        "walkTalk: " + clubMember.getName(),
                        chat.getMessage());
            } catch (CheckedException e) {
                writeErrorLog(clubMember.getId(), chatRoomMemberExceptMe, findFcmToken, e);
            }
        }
    }

    public String getOrSaveCache(Long findMemberId) {
        Optional<RoomMemberDTO> memberCache = memberTokenRedisService.getMemberTokenCacheByClubMemberId(findMemberId);
        if (memberCache.isEmpty() || memberCache.get().getFcmToken() == null) {
            return clubMemberService.getByIdWithUserToken(findMemberId).getUserToken().getFcmToken();
        } else {
            return memberCache.get().getFcmToken();
        }
    }

    public List<ChatRoomCacheDTO> saveNewCacheAndReturnValue(Long chatRoomId, List<ChatRoomCacheDTO> allMemberInChatRoom) {
        if (allMemberInChatRoom.isEmpty()) {
            allMemberInChatRoom = memberChatRoomService.getMembersInChatRoomAsChatRoomCacheDTOs(chatRoomId);
            roomMemberRedisService.saveChatRoomMembersCache(chatRoomId, allMemberInChatRoom);
        }
        return allMemberInChatRoom;
    }

    public static Chat createChat(String message, ChatRoom chatRoom, ClubMember clubMember) {
        return Chat.builder()
                .message(message)
                .chatRoom(chatRoom)
                .clubMember(clubMember)
                .build();
    }

    @Override
    public void save(Chat chat) {
        chatRepository.save(chat);
    }

    @Override
    public List<ChatDTO> chatsToChatDTOs(List<Chat> result, Long myId) {
        return result.stream()
                .map(objects -> ChatDTO.of(objects, myId, urlUtil))
                .collect(Collectors.toList());
    }

    private ChatRoom buildChatRoomAndSave(String title) {
        ChatRoom chatRoom = ChatRoom.builder()
                .memberCount(0)
                .title(title)
                .build();
        chatRoomService.save(chatRoom);
        return chatRoom;
    }

    private void buildAndSaveMemberChatRoom(List<ClubMember> clubMembers, ChatRoom chatRoom) {
        for (ClubMember clubMember : clubMembers) {
            MemberChatRoom memberChatRoom = MemberChatRoom.builder()
                    .clubMember(clubMember)
                    .chatRoom(chatRoom)
                    .build();
            memberChatRoomService.save(memberChatRoom);
        }
    }

    private void checkAndSaveCache(Long chatRoomId, Long myId, List<ChatRoomCacheDTO> allMemberInChatRoom) {
        allMemberInChatRoom = saveNewCacheAndReturnValue(chatRoomId, allMemberInChatRoom);
        checkMemberIsinChatRoom(myId, allMemberInChatRoom);
    }

    private static void writeErrorLog(Long clubMemberId, ChatRoomCacheDTO chatRoomMemberExceptMe, String fcmToken, CheckedException e) {
        log.error("{} to {} message error", clubMemberId, chatRoomMemberExceptMe.getClubMemberId());
        log.error("failed user fcm Token = {}", fcmToken);
        log.error("error message={}", "message", e);
    }

    private Payload payloadBuilder(Long chatRoomId, ClubMember clubMember, Chat chat) {
        return Payload.builder()
                .chatId(chatRoomId)
                .nickName(clubMember.getName())
                .message(chat.getMessage())
                .imagePath(urlUtil.getProfileUrlWithUrl(clubMember.getProfileFileName() != null ? clubMember.getProfileFileName() : DEFAULT_IMAGE
                ))
                .notiType(NotiType.CHAT.name())
                .linkUrl("not yet")
                .build();
    }

    @NotNull
    private static List<Long> getDistinctMemberList(MemberIdsRequest request) {
        return request.getMemberIds().stream().distinct().collect(Collectors.toList());
    }

    private static void checkMemberIsinChatRoom(Long myId, List<ChatRoomCacheDTO> allMemberInChatRoom) {
        if (allMemberInChatRoom.stream().noneMatch(dto -> dto.getClubMemberId().equals(myId)))
            throw new RestApiException(CommonErrorResponseCode.UNAUTHORIZED);
    }

    private void checkDuplicatedChatRoom(List<Long> memberIds) throws CustomException {//중복 채팅방 체크 요구에 따라 추가 가능
        ChatRoom findChatroom = findChatRoom(memberIds);
        if (findChatroom != null)
            throw new CustomException(CustomErrorResponseCode.ALREADY_EXIST_CHATROOM, "already exist chatRoom"); //찾은 채팅방 리턴 이미 존재하는 채팅방 커스텀에러로
    }

    private ChatRoom findChatRoom(List<Long> memberIds) {
        ChatRoom findChatRoom = memberChatRoomService.getByClubMembers(memberIds, memberIds.size());
        if (findChatRoom == null) return null;
        return findChatRoom;
    }
}
