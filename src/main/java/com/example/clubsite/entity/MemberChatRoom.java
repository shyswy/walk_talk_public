package com.example.clubsite.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "member_chat_uk",
                        columnNames = {"club_member_id", "chat_room_id"}  //각채팅방에는 1명의 유저만 존재한다.
                )
        }
)
@NamedEntityGraph(
        name = "withChatRoomAndClubMember",
        attributeNodes = {
                @NamedAttributeNode(value = "chatRoom"),
                @NamedAttributeNode(value = "clubMember", subgraph = "userToken")
        },
        subgraphs = {
                @NamedSubgraph(
                        name = "userToken",
                        attributeNodes = @NamedAttributeNode("userToken")
                )
        }
)
@NamedEntityGraph(
        name = "withStep",
        attributeNodes = {
                @NamedAttributeNode(value = "chatRoom"),
                @NamedAttributeNode(value = "clubMember", subgraph = "step")
        },
        subgraphs = {
                @NamedSubgraph(
                        name = "step",
                        attributeNodes = @NamedAttributeNode("step")
                )
        }
)
@Log4j2
public class MemberChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_chat_room_id")
    Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_member_id")
    private ClubMember clubMember;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    @Transactional//ok
    @PostPersist
    public void increaseChatRoomMemberCount() {
        this.getChatRoom().increaseMemberCount();
    }

    @Transactional//ok
    @PreRemove
    public void decreaseChatRoomMemberCount() {
        this.getChatRoom().decreaseMemberCount();
    }
}

