package com.example.clubsite.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberIdsRequest {
    private List<Long> memberIds;
    private String title;
}
