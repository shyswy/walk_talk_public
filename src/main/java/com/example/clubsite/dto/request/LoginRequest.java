package com.example.clubsite.dto.request;

import com.example.clubsite.enumType.SocialType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {
    private SocialType socialType;
    private String accessToken3rd;
}
