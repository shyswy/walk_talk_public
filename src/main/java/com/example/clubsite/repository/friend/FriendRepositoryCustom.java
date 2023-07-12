package com.example.clubsite.repository.friend;

import com.example.clubsite.entity.ClubMember;
import com.example.clubsite.entity.Friend;
import com.example.clubsite.enumType.FriendStatus;

import java.util.List;
import java.util.Optional;

public interface FriendRepositoryCustom {

    Optional<Friend> findTwoWayByFromMemberAndToMember(ClubMember fromMember, ClubMember toMember);

    List<Friend> findByFromMemberIdOrToMemberIdAndStatus(Long member_id, FriendStatus status);

    List<Friend> findByMemberId(Long member_id);
}
