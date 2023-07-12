package com.example.clubsite.service.socialService;

import com.example.clubsite.dto.clubmember.ClubMemberDTO;
import com.example.clubsite.dto.response.AuthResult;
import com.example.clubsite.exhandler.errorcode.CommonErrorResponseCode;
import com.example.clubsite.exhandler.exception.RestApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class GoogleServiceImpl implements GoogleService {
    public  String OAUTH_GOOGLE_API_URL = "https://oauth2.googleapis.com";
    public  String TOKEN_INFO_URL = "/tokeninfo?id_token=";

    @Override
    public ClubMemberDTO getClubMemberDtoByGoogleToken(String googleToken) {
        WebClient webClient = WebClient.create(OAUTH_GOOGLE_API_URL);
        String url = TOKEN_INFO_URL + googleToken;
        return Optional.ofNullable(webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(ClubMemberDTO.class)
                .block()).orElseThrow(() -> new RestApiException(CommonErrorResponseCode.UNAUTHORIZED));
    }

    @Override
    public AuthResult getAuthResultByGoogleToken(String googleToken) {
        WebClient webClient = WebClient.create(OAUTH_GOOGLE_API_URL);
        String url = TOKEN_INFO_URL + googleToken;
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(AuthResult.class)
                .block();
    }
}
