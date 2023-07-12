package com.example.clubsite.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
@Builder
public class LoginResponse {
    private Long id;
    private String name;
    private String email;
    private String accessToken;
    private String refreshToken;
}
