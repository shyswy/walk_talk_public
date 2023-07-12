package com.example.clubsite.service.socialService;

import com.example.clubsite.dto.clubmember.ClubMemberDTO;
import com.example.clubsite.dto.response.AuthResult;

public interface GoogleService {
    ClubMemberDTO getClubMemberDtoByGoogleToken(String googleToken);

    AuthResult getAuthResultByGoogleToken(String googleToken);
}
