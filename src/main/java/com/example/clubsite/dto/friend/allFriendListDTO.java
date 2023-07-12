package com.example.clubsite.dto.friend;

import com.example.clubsite.dto.clubmember.ClubMemberDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@Builder
public class allFriendListDTO {
    private List<ClubMemberDTO> friendList;
    private List<ClubMemberDTO> requestList;
    private List<ClubMemberDTO> requestedList;

    public allFriendListDTO(List<ClubMemberDTO> friendList, List<ClubMemberDTO> requestList, List<ClubMemberDTO> requestedList) {
        this.friendList = friendList;
        this.requestList = requestList;
        this.requestedList = requestedList;
    }
}


