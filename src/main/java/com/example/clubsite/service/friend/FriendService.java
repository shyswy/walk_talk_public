package com.example.clubsite.service.friend;

import com.example.clubsite.dto.clubmember.ClubMemberDTO;
import com.example.clubsite.dto.friend.allFriendListDTO;
import com.example.clubsite.dto.response.FriendResponse;
import com.example.clubsite.entity.ClubMember;
import com.example.clubsite.entity.Friend;
import com.example.clubsite.enumType.FriendStatus;
import com.example.clubsite.exhandler.errorcode.CommonErrorResponseCode;
import com.example.clubsite.exhandler.errorcode.CustomErrorResponseCode;
import com.example.clubsite.exhandler.exception.CustomException;
import com.example.clubsite.exhandler.exception.RestApiException;
import com.example.clubsite.repository.friend.FriendRepository;
import com.example.clubsite.service.clubmember.ClubMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class FriendService {
    private final FriendRepository friendRepository;
    private final ClubMemberService clubMemberService;

    @Transactional//ok
    public FriendResponse requestFriend(Long fromMemberId, String toMemberEmail) {
        ClubMember fromMember = clubMemberService.getById(fromMemberId);
        ClubMember toMember = clubMemberService.getByEmail(toMemberEmail).orElseThrow(() -> new CustomException(CustomErrorResponseCode.INVALID_USER_EMAIL));
        if (fromMember.getId().equals(toMember.getId())) return null;
        Friend sameFriendRequest = friendRepository.findByFromMemberIdAndToMemberId(fromMember.getId(), toMember.getId()).orElse(null);
        if (sameFriendRequest != null)
            return new FriendResponse(sameFriendRequest.getId()); //같은 요청이 이미 있다면, 아무런 동작도 수행하지 않는다.
        Friend toUserFriendRequest = friendRepository.findByFromMemberIdAndToMemberId(toMember.getId(), fromMember.getId()).orElse(null);
        if (toUserFriendRequest != null) {
            toUserFriendRequest.changeStatus(FriendStatus.FRIEND);
            return new FriendResponse(toUserFriendRequest.getId());
        } else {
            Long friendId = this.save(Friend.builder()
                    .fromMember(fromMember)
                    .toMember(toMember)
                    .status(FriendStatus.REQUEST)
                    .build());
            return new FriendResponse(friendId);
        }
    }


    @Transactional//ok
    public Long acceptFriend(Long myId, Long requestMemberId) {
        ClubMember fromMember = clubMemberService.getById(requestMemberId);
        ClubMember toMember = clubMemberService.getById(myId);
        Friend friend = getOneWayFriendByMemberIds(fromMember, toMember);
        friend.changeStatus(FriendStatus.FRIEND);
        return friend.getId();
    }

    @Transactional//ok
    public void removeFriendShip(Long myId, Long memberId) {
        ClubMember fromMember = clubMemberService.getById(myId);
        ClubMember toMember = clubMemberService.getById(memberId);
        Friend findFriend = getTwoWayFriendByMemberIds(fromMember, toMember).orElseThrow(() -> new RestApiException(CommonErrorResponseCode.RESOURCE_NOT_FOUND));
        delete(findFriend);
    }

    public Long save(Friend friend) {
        Optional<Friend> findFriend = friendRepository.findByFromMemberIdAndToMemberId(friend.getFromMember().getId(), friend.getToMember().getId());
        if (findFriend.isEmpty()) {  //제약 조건에 위배 x 인 데이터만 저장.
            friendRepository.save(friend);
            return friend.getId();
        } else {
            log.error("data integrity violation: friendRepository Constraint is violated!");
            return findFriend.get().getId();
        }
    }

    public void delete(Friend friend) {
        friendRepository.delete(friend);
    }


    @Transactional(readOnly = true)//ok
    public List<ClubMemberDTO> getFriendList(Long memberId) {
        List<ClubMember> friends = new ArrayList<>();
        List<Friend> friendList = friendRepository.findByFromMemberIdOrToMemberIdAndStatus(memberId, FriendStatus.FRIEND);
        for (Friend friend : friendList) {
            if (friend.getFromMember().getId().equals(memberId)) friends.add(friend.getToMember());
            else if (friend.getToMember().getId().equals(memberId)) friends.add(friend.getFromMember());
            else {
                log.error("user is not include in query result, id={}", friend.getId());
            }
        }
        return clubMemberService.clubMembersToClubMemberDtoS(friends);
    }

    @Transactional(readOnly = true)//ok
    public List<ClubMemberDTO> getFriendRequestList(Long memberId) {
        ClubMember member = clubMemberService.getById(memberId);
        List<ClubMember> friendRequests = new ArrayList<>();
        friendRepository.findByToMemberAndStatus(member, FriendStatus.REQUEST)
                .stream()
                .map(Friend::getFromMember)
                .forEach(friendRequests::add);
        return clubMemberService.clubMembersToClubMemberDtoS(friendRequests);
    }

    @Transactional(readOnly = true)//ok
    public allFriendListDTO findAllRelationShipByMemberId(Long memberId) {
        List<Friend> allList = getAllList(memberId);
        List<ClubMember> friendList = new ArrayList<>();
        List<ClubMember> requestedList = new ArrayList<>();
        List<ClubMember> requestList = new ArrayList<>();
        for (Friend friend : allList) {
            if (friend.getStatus().equals(FriendStatus.FRIEND)) {
                if (friend.getFromMember().getId().equals(memberId)) friendList.add(friend.getToMember());
                else if (friend.getToMember().getId().equals(memberId)) friendList.add(friend.getFromMember());
                else log.error("error! user is not include in query result!");
            } else if (friend.getStatus().equals(FriendStatus.REQUEST) && friend.getToMember().getId().equals(memberId))
                requestedList.add(friend.getFromMember());
            else if (friend.getStatus().equals(FriendStatus.REQUEST) && friend.getFromMember().getId().equals(memberId))
                requestList.add(friend.getToMember());
            else log.error("error! user is not include in query result!");
        }
        List<ClubMemberDTO> friendMemberDtoList = clubMemberService.clubMembersToClubMemberDtoS(friendList);
        List<ClubMemberDTO> requestMemberDtoList = clubMemberService.clubMembersToClubMemberDtoS(requestList);
        List<ClubMemberDTO> requestedMemberDtoList = clubMemberService.clubMembersToClubMemberDtoS(requestedList);
        return new allFriendListDTO(friendMemberDtoList, requestMemberDtoList, requestedMemberDtoList);
    }

    private List<Friend> getAllList(Long memberId) {
        return friendRepository.findByMemberId(memberId);
    }

    private Friend getOneWayFriendByMemberIds(ClubMember fromMember, ClubMember toMember) {
        return friendRepository.findByFromMemberAndToMember(fromMember, toMember).orElseThrow(() -> new RestApiException(CommonErrorResponseCode.RESOURCE_NOT_FOUND));
    }

    private Optional<Friend> getTwoWayFriendByMemberIds(ClubMember fromMember, ClubMember toMember) {
        return friendRepository.findTwoWayByFromMemberAndToMember(fromMember, toMember);
    }
}


