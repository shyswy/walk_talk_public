package com.example.clubsite.repository.memberchatroom;

import com.example.clubsite.entity.ChatRoom;
import com.example.clubsite.entity.ClubMember;
import com.example.clubsite.entity.MemberChatRoom;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberChatRoomRepository extends JpaRepository<MemberChatRoom, Long>, MemberChatRoomRepositoryCustom {
    @EntityGraph(value = "withChatRoomAndClubMember", type = EntityGraph.EntityGraphType.LOAD)
    List<MemberChatRoom> findAllByChatRoomId(@Param("chatRoomId") Long chatRoomId);

    boolean existsByClubMemberAndChatRoom(ClubMember clubMember, ChatRoom chatRoom);

    Optional<MemberChatRoom> findByClubMemberAndChatRoom(ClubMember clubMember, ChatRoom chatRoom);
}
