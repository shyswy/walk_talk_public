package com.example.clubsite.repository.friend;

import com.example.clubsite.entity.ClubMember;
import com.example.clubsite.entity.Friend;
import com.example.clubsite.entity.QClubMember;
import com.example.clubsite.entity.QStep;
import com.example.clubsite.enumType.FriendStatus;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.example.clubsite.entity.QClubMember.clubMember;
import static com.example.clubsite.entity.QFriend.friend;
import static com.example.clubsite.entity.QStep.step;

@RequiredArgsConstructor
@Repository
public class FriendRepositoryImpl implements FriendRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public Optional<Friend> findTwoWayByFromMemberAndToMember(ClubMember fromMember, ClubMember toMember) {
        BooleanExpression forward = friend.fromMember.eq(fromMember).and(friend.toMember.eq(toMember));
        BooleanExpression reverse = friend.fromMember.eq(toMember).and(friend.toMember.eq(fromMember));
        return Optional.ofNullable(queryFactory.select(friend)
                .from(friend)
                .where(forward.or(reverse))
                .fetchFirst());
    }

    public List<Friend> findByFromMemberIdOrToMemberIdAndStatus(Long member_id, FriendStatus status) {
        BooleanExpression statusCondition = friend.status.eq(status);
        return queryFactory.select(friend)
                .from(friend)
                .leftJoin(friend.toMember, clubMember).fetchJoin()
                .leftJoin(friend.fromMember, clubMember).fetchJoin()
                .where(fromMemberOrToMemberEqualId(member_id).and(statusCondition))
                .fetch();
    }

    public List<Friend> findByMemberId(Long member_id) {
        QClubMember toMember = new QClubMember("toMember");
        QClubMember fromMember = new QClubMember("fromMember");
        QStep toMemberStep = new QStep("toMemberStep");
        QStep fromMemberStep = new QStep("fromMemberStep");
        return queryFactory.select(friend)
                .from(friend)
                .leftJoin(friend.toMember, toMember).fetchJoin()
                .leftJoin(friend.fromMember, fromMember).fetchJoin()
                .leftJoin(toMember.step, toMemberStep).fetchJoin()
                .leftJoin(fromMember.step, fromMemberStep).fetchJoin()
                .where(fromMemberOrToMemberEqualId(member_id))
                .fetch();
    }

    private BooleanExpression fromMemberOrToMemberEqualId(Long member_id) {
        BooleanExpression memberCondition = friend.fromMember.id.eq(member_id).or(friend.toMember.id.eq(member_id));
        return memberCondition;
    }
}
