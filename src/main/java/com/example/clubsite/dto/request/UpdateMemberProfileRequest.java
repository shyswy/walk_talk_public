package com.example.clubsite.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMemberProfileRequest {
    private String name;
    private String email;
    private String profileImageFileUrl;
}
