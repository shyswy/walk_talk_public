package com.example.clubsite.repository.memberchatroom;

import com.example.clubsite.entity.ChatRoom;
import com.example.clubsite.entity.ClubMember;
import com.example.clubsite.entity.MemberChatRoom;

import java.util.List;
import java.util.Optional;

public interface MemberChatRoomRepositoryCustom {

    List<MemberChatRoom> findByClubMemberWithChatRoomAndClubMember(ClubMember clubMember);

    List<MemberChatRoom> findByChatRoomIdWithChatRoomAndClubMember(Long chatRoomId);

    Optional<ChatRoom> findByClubMembers(List<Long> memberIds, long clubMemberCount);

    List<MemberChatRoom> findAllByChatRoomIdWithStep(Long chatRoomId);

    MemberChatRoom findByIdWithChatRoomAndClubMember(Long id);

    MemberChatRoom findByIdWithChatRoom(Long id);

    MemberChatRoom findByIdWithClubMember(Long id);
}
