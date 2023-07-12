package com.example.clubsite.repository.friend;

import com.example.clubsite.entity.ClubMember;
import com.example.clubsite.entity.Friend;
import com.example.clubsite.enumType.FriendStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend, Long>, FriendRepositoryCustom {
    Optional<Friend> findByFromMemberAndToMember(ClubMember fromMember, ClubMember toMember);

    Optional<Friend> findByFromMemberIdAndToMemberId(Long fromMemberId, Long toMemberId);

    List<Friend> findByToMemberAndStatus(ClubMember toMember, FriendStatus status);
}


