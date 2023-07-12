package com.example.clubsite.service.memberchatroom;

import com.example.clubsite.redis.object.ChatRoomCacheDTO;
import com.example.clubsite.dto.chatroom.ChatRoomDTO;
import com.example.clubsite.dto.clubmember.ClubMemberWithRankDTO;
import com.example.clubsite.dto.clubmember.RankListDTO;
import com.example.clubsite.dto.response.ChatRoomIdResponse;
import com.example.clubsite.dto.response.ChatRoomInviteResponse;
import com.example.clubsite.entity.ChatRoom;
import com.example.clubsite.entity.ClubMember;
import com.example.clubsite.entity.MemberChatRoom;
import com.example.clubsite.entity.Step;
import com.example.clubsite.exhandler.errorcode.CommonErrorResponseCode;
import com.example.clubsite.exhandler.exception.RestApiException;
import com.example.clubsite.redis.service.RoomMemberRedisService;
import com.example.clubsite.repository.memberchatroom.MemberChatRoomRepository;
import com.example.clubsite.service.chatroom.ChatRoomService;
import com.example.clubsite.service.clubmember.ClubMemberService;
import com.example.clubsite.utility.UrlUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class MemberChatRoomServiceImpl implements MemberChatRoomService {
    private static final int MAX_IMAGE_NUM = 3;
    private final MemberChatRoomRepository memberChatRoomRepository;
    private final ClubMemberService clubMemberService;
    private final ChatRoomService chatRoomService;
    private final RoomMemberRedisService roomMemberRedisService;
    private final UrlUtil urlUtil;

    @Override
    @Transactional(readOnly = true)
    public ChatRoom getByClubMembers(List<Long> memberIds, long length) {
        return memberChatRoomRepository.findByClubMembers(memberIds, memberIds.size()).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)//ok
    public List<ChatRoomDTO> getChatRoomIdsByMemberId(Long myMemberId) {
        ClubMember findMember = clubMemberService.getById(myMemberId);
        List<MemberChatRoom> chatRoomList = getByClubMemberWithChatRoomAndClubMember(findMember);
        return memberChatRoomListToChatDTOList(myMemberId, chatRoomList);
    }

    @Override
    public List<MemberChatRoom> getByClubMemberWithChatRoomAndClubMember(ClubMember findMember) {
        return memberChatRoomRepository.findByClubMemberWithChatRoomAndClubMember(findMember);
    }

    @Override
    @Transactional//ok
    public RankListDTO getRankList(Long chatRoomId, Long myId, int rankNum) {
        List<MemberChatRoom> memberChatRooms = findAllByChatRoomIdStep(chatRoomId);  //여기서 MemberChatRoom 말고, ClubMember 리스트를 한번에 가져오게!
        ClubMember clubMember = clubMemberService.getClubMemberWithStepById(myId);
        return makeRankListDTO(rankNum, memberChatRooms, clubMember);
    }

    public List<ChatRoomDTO> memberChatRoomListToChatDTOList(Long myMemberId, List<MemberChatRoom> result) {
        return result.stream()
                .map(objects -> this.entityToDTOWithImageThumbnail(myMemberId, objects))
                .collect(Collectors.toList());
    }

    public ChatRoomDTO entityToDTOWithImageThumbnail(Long myMemberId, MemberChatRoom memberChatRoom) {
        List<String> imageUrlsExceptMe = memberChatRoomRepository.findByChatRoomIdWithChatRoomAndClubMember(memberChatRoom.getChatRoom().getId())
                .stream()
                .filter(clubMember -> !clubMember.getId().equals(myMemberId))
                .map(objects -> urlUtil.getProfileUrlWithUrl(objects.getClubMember().getProfileFileName()))
                .limit(MAX_IMAGE_NUM)
                .collect(Collectors.toList());
        return ChatRoomDTO.of(memberChatRoom, imageUrlsExceptMe);
    }

    @Override
    @Transactional//ok
    public ChatRoomInviteResponse inviteMembers(List<Long> memberIds, Long chatRoomId) {//다수의 멤버를 채팅방에 추가.
        ChatRoom findChatRoom = chatRoomService.getById(chatRoomId);
        for (Long memberId : memberIds) {
            inviteMember(memberId, findChatRoom);
        }
        roomMemberRedisService.cacheDelete(chatRoomId);
        return new ChatRoomInviteResponse(findChatRoom.getMemberCount());
    }

    @Transactional//ok
    @Override
    public ChatRoomIdResponse leaveChat(Long clubMemberId, Long chatRoomId) {
        MemberChatRoom findMemberChatRoom = getByClubMemberIdAndChatRoomIdAndDecreaseChatRoomMemberCount(clubMemberId, chatRoomId);
        this.delete(findMemberChatRoom);
        roomMemberRedisService.cacheDelete(chatRoomId);
        return new ChatRoomIdResponse(chatRoomId);
    }

    @Override
    public void inviteMember(Long memberId, ChatRoom chatRoom) {
        ClubMember findMember = clubMemberService.getById(memberId);
        addMemberToChatRoom(findMember, chatRoom);
        roomMemberRedisService.cacheDelete(chatRoom.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatRoomCacheDTO> getMembersInChatRoomAsChatRoomCacheDTOs(Long chatRoomId) {
        List<MemberChatRoom> memberChatRooms = findAllByChatRoomIdWithChatRoomAndClubMember(chatRoomId);
        return memberChatRoomToChatRoomFcmDTOList(memberChatRooms);
    }

    @Override
    public void addMemberToChatRoom(ClubMember findMember, ChatRoom findChatRoom) {
        this.save(MemberChatRoom.builder()
                .clubMember(findMember)
                .chatRoom(findChatRoom)
                .build());
    }

    @Override
    public MemberChatRoom getByClubMemberIdAndChatRoomId(Long clubMemberId, Long chatRoomId) {
        ClubMember findMember = clubMemberService.getById(clubMemberId);
        ChatRoom findChatRoom = chatRoomService.getById(chatRoomId);
        return getByClubMemberWithChatRoom(findMember, findChatRoom);
    }

    @Override
    public void save(MemberChatRoom memberChatRoom) {
        if (!memberChatRoomRepository.existsByClubMemberAndChatRoom(memberChatRoom.getClubMember(), memberChatRoom.getChatRoom()))
            memberChatRoomRepository.save(memberChatRoom); //UK 제약조건 만족하는 값만 save
        else log.error("data integrity violation: duplicated data will be ignored.");
    }


    @Override
    public MemberChatRoom getByClubMemberIdAndChatRoomIdAndDecreaseChatRoomMemberCount(Long clubMemberId, Long chatRoomId) {
        ClubMember findMember = clubMemberService.getById(clubMemberId);
        ChatRoom findChatRoom = chatRoomService.getById(chatRoomId);
        if (findChatRoom.getMemberCount() <= 0) chatRoomService.delete(findChatRoom);
        return getByClubMemberWithChatRoom(findMember, findChatRoom);
    }

    @Override
    public MemberChatRoom getByClubMemberWithChatRoom(ClubMember findMember, ChatRoom findChatRoom) {
        return memberChatRoomRepository.findByClubMemberAndChatRoom(findMember, findChatRoom)
                .orElseThrow(() -> new RestApiException(CommonErrorResponseCode.RESOURCE_NOT_FOUND));
    }

    @Override
    public void delete(MemberChatRoom memberChatRoom) {
        memberChatRoomRepository.delete(memberChatRoom);
        deleteUselessChatRoom(memberChatRoom);
    }

    public void deleteUselessChatRoom(MemberChatRoom memberChatRoom) {
        ChatRoom chatRoom = memberChatRoom.getChatRoom();
        if (memberChatRoom.getChatRoom().getMemberCount() <= 0) {
            chatRoomService.delete(chatRoom);
        }
    }

    @NotNull
    private List<MemberChatRoom> findAllByChatRoomIdWithChatRoomAndClubMember(Long chatRoomId) {
        List<MemberChatRoom> allByChatRoomId = memberChatRoomRepository.findAllByChatRoomId(chatRoomId);
        if (allByChatRoomId.isEmpty()) throw new RestApiException(CommonErrorResponseCode.RESOURCE_NOT_FOUND);
        return allByChatRoomId;
    }

    private List<MemberChatRoom> findAllByChatRoomIdStep(Long chatRoomId) {
        List<MemberChatRoom> memberChatRooms = memberChatRoomRepository.findAllByChatRoomIdWithStep(chatRoomId);
        if (memberChatRooms.isEmpty()) throw new RestApiException(CommonErrorResponseCode.RESOURCE_NOT_FOUND);
        return memberChatRooms;
    }

    private RankListDTO makeRankListDTO(int rankNum, List<MemberChatRoom> memberChatRooms, ClubMember myClubMember) {
        ClubMemberWithRankDTO myClubMemberDTOWithRankDTO = ClubMemberWithRankDTO.of(myClubMember, urlUtil);
        List<ClubMemberWithRankDTO> rankList = clubMemberService.clubMembersToRankDtoS(
                extractClubMembersFromMemberChatRoomsOrderByStep(rankNum, memberChatRooms));
        return RankListDTO.of(myClubMemberDTOWithRankDTO, rankList);
    }

    private static List<ClubMember> extractClubMembersFromMemberChatRoomsOrderByStep(int rankNum, List<MemberChatRoom> memberChatRooms) {
        return memberChatRooms.stream()
                .map(MemberChatRoom::getClubMember)
//                .sorted(Comparator.comparing(member -> member.getStep().getStepCount(), Comparator.reverseOrder()))
                .sorted(Comparator.comparing(member -> {
                    Step step = member.getStep();
                    return (step != null) ? step.getStepCount() : 0; //nullPoint Exception 보완
                }, Comparator.reverseOrder()))
                .limit(rankNum)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAllChatRoomCache(ClubMember clubMember) {
        List<MemberChatRoom> memberChatRooms = getByClubMemberWithChatRoomAndClubMember(clubMember);
        for (MemberChatRoom MemberChatRoom : memberChatRooms) {
            roomMemberRedisService.cacheDelete(MemberChatRoom.getChatRoom().getId());
        }
    }
}



