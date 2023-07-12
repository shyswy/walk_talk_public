package com.example.clubsite.repository.memberchatroom;

import com.example.clubsite.entity.ChatRoom;
import com.example.clubsite.entity.ClubMember;
import com.example.clubsite.entity.MemberChatRoom;
import com.example.clubsite.entity.QStep;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.example.clubsite.entity.QChatRoom.chatRoom;
import static com.example.clubsite.entity.QClubMember.clubMember;
import static com.example.clubsite.entity.QMemberChatRoom.memberChatRoom;


@Repository
@RequiredArgsConstructor
@Log4j2
public class MemberChatRoomRepositoryImpl implements MemberChatRoomRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public List<MemberChatRoom> findByClubMemberWithChatRoomAndClubMember(ClubMember keyClubMember) {
        return queryFactory.select(memberChatRoom)
                .from(memberChatRoom)
                .leftJoin(memberChatRoom.chatRoom, chatRoom).fetchJoin()
                .leftJoin(memberChatRoom.clubMember, clubMember).fetchJoin()
                .where(memberChatRoom.clubMember.eq(keyClubMember))
                .fetch();
    }

    public List<MemberChatRoom> findByChatRoomIdWithChatRoomAndClubMember(Long chatRoomId) {
        return queryFactory.select(memberChatRoom)
                .from(memberChatRoom)
                .leftJoin(memberChatRoom.chatRoom, chatRoom).fetchJoin()
                .leftJoin(memberChatRoom.clubMember, clubMember).fetchJoin()
                .where(memberChatRoom.chatRoom.id.eq(chatRoomId))
                .fetch();
    }

    public List<MemberChatRoom> findAllByChatRoomIdWithStep(Long chatRoomId) {
        QStep step = QStep.step;
        return queryFactory.select(memberChatRoom)
                .from(memberChatRoom)
                .leftJoin(memberChatRoom.clubMember, clubMember).fetchJoin()
                .leftJoin(clubMember.step, step).fetchJoin()
                .where(memberChatRoom.chatRoom.id.eq(chatRoomId))
                .fetch();
    }

    public MemberChatRoom findByIdWithChatRoomAndClubMember(Long id) {
        return queryFactory.select(memberChatRoom)
                .from(memberChatRoom)
                .leftJoin(memberChatRoom.chatRoom, chatRoom).fetchJoin()
                .leftJoin(memberChatRoom.clubMember, clubMember).fetchJoin()
                .fetchOne();
    }

    public MemberChatRoom findByIdWithChatRoom(Long id) {
        return queryFactory.select(memberChatRoom)
                .from(memberChatRoom)
                .leftJoin(memberChatRoom.chatRoom, chatRoom).fetchJoin()
                .fetchOne();
    }

    public MemberChatRoom findByIdWithClubMember(Long id) {
        return queryFactory.select(memberChatRoom)
                .from(memberChatRoom)
                .leftJoin(memberChatRoom.clubMember, clubMember).fetchJoin()
                .fetchOne();
    }

    public Optional<ChatRoom> findByClubMembers(List<Long> memberIds, long clubMemberCount) {
        ChatRoom chatRoom = queryFactory
                .select(memberChatRoom.chatRoom)
                .from(memberChatRoom)
                .groupBy(memberChatRoom.chatRoom)
                .having(
                        memberChatRoom.clubMember.id.count().eq(clubMemberCount)
                                .and(new CaseBuilder()
                                        .when(memberChatRoom.clubMember.id.notIn(memberIds)).then(memberChatRoom.clubMember.id)
                                        .otherwise((Long) null)
                                        .count().eq(0L)
                                )
                )
                .fetchOne();
        return Optional.ofNullable(chatRoom);
    }
}




