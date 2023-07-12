package com.example.clubsite.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Chat extends CreateDateEntity {
    @Id
    @Column(name = "chat_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;
    private String message;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_member_id")
    private ClubMember clubMember;
}
