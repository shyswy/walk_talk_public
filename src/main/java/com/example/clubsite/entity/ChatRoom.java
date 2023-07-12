package com.example.clubsite.entity;

import com.example.clubsite.exhandler.errorcode.CommonErrorResponseCode;
import com.example.clubsite.exhandler.exception.RestApiException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter

@Log4j2
public class ChatRoom extends CreateDateEntity {
    @Id
    @Column(name = "chat_room_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "title")
    String title;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "chatRoom", cascade = CascadeType.REMOVE)
    @Column(name = "member_chat_rooms")
    private List<MemberChatRoom> memberChatRooms = new ArrayList<>();
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "chatRoom", cascade = CascadeType.REMOVE)
    @Column(name = "chats")
    private List<Chat> chats = new ArrayList<>();
    @Builder.Default
    private int memberCount = 0;

    public void increaseMemberCount() {
        this.memberCount++;
    }

    public void decreaseMemberCount() {
        if (this.memberCount >= 1)
            this.memberCount--;
        else throw new RestApiException(CommonErrorResponseCode.INTERNAL_SERVER_ERROR);
    }
}

