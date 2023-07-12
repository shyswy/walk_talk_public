package com.example.clubsite.service.memberchatroom;

import com.example.clubsite.dto.chatroom.ChatRoomDTO;
import com.example.clubsite.dto.clubmember.RankListDTO;
import com.example.clubsite.dto.response.ChatRoomIdResponse;
import com.example.clubsite.dto.response.ChatRoomInviteResponse;
import com.example.clubsite.entity.ChatRoom;
import com.example.clubsite.entity.ClubMember;
import com.example.clubsite.entity.MemberChatRoom;
import com.example.clubsite.entity.UserToken;
import com.example.clubsite.redis.object.ChatRoomCacheDTO;

import java.util.ArrayList;
import java.util.List;

public interface MemberChatRoomService {
    List<ChatRoomCacheDTO> getMembersInChatRoomAsChatRoomCacheDTOs(Long chatRoomId);

    void deleteAllChatRoomCache(ClubMember clubMember);

    List<MemberChatRoom> getByClubMemberWithChatRoomAndClubMember(ClubMember findMember);

    ChatRoom getByClubMembers(List<Long> memberIds, long length);

    MemberChatRoom getByClubMemberWithChatRoom(ClubMember findMember, ChatRoom findChatRoom);

    void inviteMember(Long memberId, ChatRoom chatRoom);

    ChatRoomInviteResponse inviteMembers(List<Long> memberIds, Long chatRoomId);

    List<ChatRoomDTO> getChatRoomIdsByMemberId(Long memberId);

    RankListDTO getRankList(Long chatRoomId, Long myId, int rankNum);


    void save(MemberChatRoom memberChatRoom);

    void delete(MemberChatRoom memberChatRoom);

    ChatRoomIdResponse leaveChat(Long clubMemberId, Long chatRoomId);

    MemberChatRoom getByClubMemberIdAndChatRoomIdAndDecreaseChatRoomMemberCount(Long clubMemberId, Long chatRoomId);

    void addMemberToChatRoom(ClubMember findMember, ChatRoom findChatRoom);

    MemberChatRoom getByClubMemberIdAndChatRoomId(Long clubMemberId, Long chatRoomId);

    default List<ChatRoomCacheDTO> memberChatRoomToChatRoomFcmDTOList(List<MemberChatRoom> memberChatRooms) {
        List<ChatRoomCacheDTO> chatRoomCacheDTOList = new ArrayList<>();
        for (MemberChatRoom memberChatRoom : memberChatRooms) {
            UserToken userToken = memberChatRoom.getClubMember().getUserToken();
            if (userToken != null) {
                chatRoomCacheDTOList.add(
                        new ChatRoomCacheDTO(
                                memberChatRoom.getChatRoom().getId(),
                                memberChatRoom.getClubMember().getId()
                                //, userToken.getFcmToken()
                        )
                );
            }
        }
        return chatRoomCacheDTOList;
    }
}
