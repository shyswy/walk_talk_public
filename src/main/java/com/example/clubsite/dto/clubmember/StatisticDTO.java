package com.example.clubsite.dto.clubmember;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatisticDTO {
    private Long memberId;
    private String image;
    private String name;
    private Long stepCount;
}
