package com.example.clubsite.dto.clubmember;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Token {
    public String accessToken;
    public String refreshToken;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Token token = (Token) o;
        return Objects.equals(getAccessToken(), token.getAccessToken()) && Objects.equals(getRefreshToken(), token.getRefreshToken());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAccessToken(), getRefreshToken());
    }
}
