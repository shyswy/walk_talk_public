package com.example.clubsite.utility;

import com.example.clubsite.dto.clubmember.Token;
import com.example.clubsite.dto.response.RootResponse;
import com.example.clubsite.dto.security.MemberAuthDTO;
import com.example.clubsite.enumType.ClubMemberRole;
import com.example.clubsite.enumType.TokenType;
import com.example.clubsite.exhandler.errorcode.CommonErrorResponseCode;
import com.example.clubsite.exhandler.exception.RestApiException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClaims;
import io.jsonwebtoken.impl.DefaultJws;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.*;


@Log4j2
@Component
public class JWTUtil {
    public static final String ID = "id";
    public static final String SUB = "sub";
    public static final String REFRESH = "refresh";
    public static final String TYPE = "type";
    public static final String ROLE = "role";

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${token.access.expiration}")
    private long ACCESS_TOKEN_EXPIRATION_DATE;

    @Value("${token.refresh.expiration}")
    private long REFRESH_TOKEN_EXPIRATION_DATE;

    public Token generateToken(Long id) {
        return Token.builder().accessToken(generateAccessToken(id, ClubMemberRole.USER)) //기본 권한 USER
                .refreshToken(generateRefreshToken()).build();
    }

    private String generateAccessToken(Long id, ClubMemberRole role) { //JWT 토큰 생성하기.
            return Jwts.builder().setIssuedAt(new Date()) //시작점
                     .setExpiration(Date.from(ZonedDateTime.now().plusSeconds(ACCESS_TOKEN_EXPIRATION_DATE).toInstant())) //만료 설정
                    .claim(ID, id).claim(TYPE, TokenType.ACCESS_TOKEN.name()) //name, value의 claim 쌍 여기에 이메일 등 저장할 정보를 "sub"라는 이름으로 추가 -> refresh는
                    .claim(ROLE, role.name()) // 권한 claim 추가  (우선은 전부 USER 권한만)
                    .signWith(SignatureAlgorithm.HS256, secretKey.getBytes(StandardCharsets.UTF_8)) //알고리즘, 비밀키 설정
                    .compact();
    }


    private String generateRefreshToken() { //refresh token
            return Jwts.builder().setIssuedAt(new Date()) //시작점
                    .setExpiration(Date.from(ZonedDateTime.now().plusSeconds(REFRESH_TOKEN_EXPIRATION_DATE).toInstant())) //만료 설정
                    .claim(SUB, REFRESH) // refresh token에는 유저 정보 넣지 않는다.
                    .claim(TYPE, TokenType.REFRESH_TOKEN.name()) //name, value의 claim 쌍 여기에 이메일 등 저장할 정보를 "sub"라는 이름으로 추가 -> refresh는
                    .signWith(SignatureAlgorithm.HS256, secretKey.getBytes(StandardCharsets.UTF_8)) //알고리즘, 비밀키 설정
                    .compact();
    }

    public Optional<MemberAuthDTO> validateAndExtractMemberAuthDTO(String token) {
        DefaultClaims claims = getClaims(token);
        ClubMemberRole role = ClubMemberRole.valueOf(claims.get(ROLE, String.class));
        Long id = claims.get(ID, Long.class);
        checkTokenValidity(id, role);
        return makeMemberAuthDTO(id, role);
    }

    public void validateRefreshToken(String token) {
        DefaultClaims claims = getClaims(token);
        if (claims.get(SUB, String.class).isEmpty()) {
            throw new RestApiException(CommonErrorResponseCode.UNAUTHORIZED);
        }
    }

    public Optional<MemberAuthDTO> extractMemberAuthDTO(String token) {
        try {
            DefaultJws defaultJws = (DefaultJws) Jwts.parser().setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8)).parseClaimsJws(token);
            DefaultClaims claims = (DefaultClaims) defaultJws.getBody();
            ClubMemberRole role = ClubMemberRole.valueOf(claims.get(ROLE, String.class));
            Long id = claims.get(ID, Long.class);
            checkTokenValidity(id, role);
            return makeMemberAuthDTO(id, role);
        } catch (ExpiredJwtException e) { //만료된 토큰에서 정보 추출
            Map<String, Object> claims = e.getClaims();
            ClubMemberRole role = ClubMemberRole.valueOf((String) claims.get(ROLE));
            Long id = ((Number) claims.get(ID)).longValue();
            return makeMemberAuthDTO(id, role);
        } catch (JwtException | IllegalArgumentException e) {
            throw new RestApiException(CommonErrorResponseCode.UNAUTHORIZED, e.getMessage());
        }
    }

    private DefaultClaims getClaims(String token) {
        try {
            DefaultJws defaultJws = (DefaultJws) Jwts.parser().setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8)).parseClaimsJws(token);
            return (DefaultClaims) defaultJws.getBody();
        } catch (IllegalArgumentException | JwtException e) {
            throw new RestApiException(CommonErrorResponseCode.UNAUTHORIZED, e.getMessage());
        }
    }

    private void checkTokenValidity(Long id, ClubMemberRole role) {
        if (id == null || role == null) throw new RestApiException(CommonErrorResponseCode.INVALID_PARAMETER);
        if (!isValidRole(role)) throw new RestApiException(CommonErrorResponseCode.UNAUTHORIZED);
    }

    private boolean isValidRole(ClubMemberRole role) {
        return role == ClubMemberRole.USER || role == ClubMemberRole.MANAGER || role == ClubMemberRole.ADMIN;
    }

    private Optional<MemberAuthDTO> makeMemberAuthDTO(Long id, ClubMemberRole role) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role.toString()));
        return Optional.ofNullable((new MemberAuthDTO(id, authorities)));
    }

    public void sendErrorResponse(HttpServletResponse response, HttpStatus status) {
        ObjectMapper objectMapper = new ObjectMapper();
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        try {
            response.getWriter().write(objectMapper.writeValueAsString(new RootResponse(status.value(), status.getReasonPhrase(), null, null)));
        } catch (IOException e) {
            throw new RestApiException(CommonErrorResponseCode.IO_ERROR, e.getMessage());
        }
    }
}

