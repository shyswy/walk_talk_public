package com.example.clubsite.dto.clubmember;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;


@Getter
@NoArgsConstructor
@Builder
public class RankListDTO {
    private ClubMemberWithRankDTO me;
    private List<ClubMemberWithRankDTO> rankList;

    public RankListDTO(ClubMemberWithRankDTO me, List<ClubMemberWithRankDTO> rankList) {
        this.me = me;
        this.rankList = rankList;
    }

    public static RankListDTO of(ClubMemberWithRankDTO myClubMemberDTOWithRankDTO, List<ClubMemberWithRankDTO> rankList) {
        for (int i = 0; i < rankList.size(); i++) {
            rankList.get(i).changeRank(i + 1);
            if (myClubMemberDTOWithRankDTO.getId().equals(rankList.get(i).getId()))
                myClubMemberDTOWithRankDTO.changeRank(i + 1);
        }
        return new RankListDTO(myClubMemberDTOWithRankDTO, rankList);
    }
}
