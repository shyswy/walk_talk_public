package com.example.clubsite.repository;

import com.example.clubsite.entity.Chat;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    @EntityGraph(attributePaths = "clubMember")
    List<Chat> findWithClubMemberByChatRoomId(Long chatRoomId);
}
