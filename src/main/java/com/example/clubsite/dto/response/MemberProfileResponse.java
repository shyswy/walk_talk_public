package com.example.clubsite.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberProfileResponse {
    private Long id;
    private String name;
    private String email;
    private String profileImageUrl;
}
