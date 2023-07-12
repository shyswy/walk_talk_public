package com.example.clubsite.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReissueResponse {
    private String accessToken;
    private String refreshToken;
}
