package com.example.clubsite.dto.clubmember;

import com.example.clubsite.entity.ClubMember;
import com.example.clubsite.entity.Step;
import com.example.clubsite.utility.UrlUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ClubMemberWithRankDTO {
    private Long id;
    private String email;
    private String name;
    private int rank;
    private Long stepCount;
    private String profileImageFileUrl;

    public void changeRank(int rank) {
        this.rank = rank;
    }

    public static ClubMemberWithRankDTO of(ClubMember clubMember, UrlUtil urlUtil) {
        if (clubMember == null) return null;
        return ClubMemberWithRankDTO.builder()
                .email(clubMember.getEmail().toLowerCase())
                .name(clubMember.getName())
                .profileImageFileUrl(urlUtil.getProfileUrlWithUrl(clubMember.getProfileFileName()))
                .id(clubMember.getId())
                .stepCount(Optional.ofNullable(clubMember.getStep())
                        .map(Step::getStepCount)
                        .orElse(null)
                )
                .build();
    }
}
