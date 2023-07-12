package com.example.clubsite.security.util;


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
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.ZonedDateTime;
import java.util.*;




@Log4j2
@Component
public class JWTUtil {

    @Value("${jwt.secret-key}")
    private String secretKey;
    public static final String BEARER = "Bearer ";
    private static final long ACCESS_TOKEN_EXPIRATION_DATE = 30; //10분
    private static final long REFRESH_TOKEN_EXPIRATION_DATE = 60*24*12;// 2주




    public Token generateToken(Long id)  {
        try {
            return Token.builder().accessToken(generateAccessToken(id, ClubMemberRole.USER)) //기본 권한 USER
                    .refreshToken(generateRefreshToken()).build();
        }catch (Exception e){
            e.printStackTrace();
            throw new RestApiException(CommonErrorResponseCode.INVALID_PARAMETER);
        }

    }
    private String generateAccessToken(Long id, ClubMemberRole role) throws Exception { //JWT 토큰 생성하기.
        return Jwts.builder().setIssuedAt(new Date()) //시작점
                .setExpiration(Date.from(ZonedDateTime.now().plusSeconds(5).toInstant())) //만료 설정
                //.setExpiration(Date.from(ZonedDateTime.now().plusMinutes(ACCESS_TOKEN_EXPIRATION_DATE).toInstant())) //만료 설정
                .claim("id", id).claim("type", TokenType.ACCESS_TOKEN.name()) //name, value의 claim 쌍 여기에 이메일 등 저장할 정보를 "sub"라는 이름으로 추가 -> refresh는
                .claim("role", role.name()) // 권한 claim 추가  (우선은 전부 USER 권한만)
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes("UTF-8")) //알고리즘, 비밀키 설정
                .compact();
    }
    private String generateRefreshToken() throws Exception { //refresh token
        return Jwts.builder().setIssuedAt(new Date()) //시작점
                .setExpiration(Date.from(ZonedDateTime.now().plusSeconds(10).toInstant())) //만료 설정
                //.setExpiration(Date.from(ZonedDateTime.now().plusMinutes(REFRESH_TOKEN_EXPIRATION_DATE).toInstant())) //만료 설정
                .claim("sub", "refresh") // refresh token에는 유저 정보 넣지 않는다.
                .claim("type", TokenType.REFRESH_TOKEN.name()) //name, value의 claim 쌍 여기에 이메일 등 저장할 정보를 "sub"라는 이름으로 추가 -> refresh는
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes("UTF-8")) //알고리즘, 비밀키 설정
                .compact();
    }

    public Optional<MemberAuthDTO> validateAndExtractMemberAuthDTO(String token) throws UnsupportedEncodingException {
            DefaultClaims claims = getClaims(token);
            ClubMemberRole role = ClubMemberRole.valueOf(claims.get("role", String.class));
            Long id = claims.get("id", Long.class);
            checkTokenValidity(id, role);
            return makeMemberAuthDTO(id, role);
    }


    //reissue 의 authorization은 컨트롤러에서 진행 -> Controller에서 예외처리
    public void validateRefreshToken(String token)  {// refresh 토큰 검증용.
        try {
            DefaultClaims claims = getClaims(token);
            if(claims.get("sub", String.class).isEmpty()){
                throw new RestApiException(CommonErrorResponseCode.UNAUTHORIZED);
            }
        }catch (ExpiredJwtException e){
           // throw new ExpiredJwtException(null,null,"expired refresh token ");
            throw new RestApiException(CommonErrorResponseCode.UNAUTHORIZED);
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RestApiException(CommonErrorResponseCode.BAD_REQUEST,"refresh Token error");
        }
    }

    public Optional<MemberAuthDTO> extractMemberAuthDTO(String token) { //reissue에서 만료된 access token에서 정보를 가져올 때 사용.
        try {
            DefaultClaims claims = getClaims(token);
            ClubMemberRole role = ClubMemberRole.valueOf(claims.get("role", String.class));
            Long id = claims.get("id", Long.class);
            checkTokenValidity(id, role);
            return makeMemberAuthDTO(id, role);
        } catch (ExpiredJwtException e) { //만료된 토큰에서 정보 추출
            Map<String, Object> claims = e.getClaims();
            ClubMemberRole role = ClubMemberRole.valueOf((String) claims.get("role"));
            Long id = ((Number) claims.get("id")).longValue();
            return makeMemberAuthDTO(id, role);
        } catch (JwtException | IllegalArgumentException | UnsupportedEncodingException e) {
            throw new RestApiException(CommonErrorResponseCode.UNAUTHORIZED);
        } catch (Exception e){
            e.printStackTrace();
            throw new RestApiException(CommonErrorResponseCode.INTERNAL_SERVER_ERROR);
        }

    }
    private DefaultClaims getClaims(String token) throws UnsupportedEncodingException {

            DefaultJws defaultJws = (DefaultJws) Jwts.parser().setSigningKey(secretKey.getBytes("UTF-8")).parseClaimsJws(token);
            DefaultClaims claims = (DefaultClaims) defaultJws.getBody();
            return claims;

    }
    public static boolean isValidRole(ClubMemberRole role) {
        return role == ClubMemberRole.USER || role == ClubMemberRole.MANAGER || role == ClubMemberRole.ADMIN;
    }
    private static void checkTokenValidity(Long id, ClubMemberRole role) {
        if (id == null || role == null)  throw new RestApiException(CommonErrorResponseCode.INVALID_PARAMETER);
            //throw new IllegalArgumentException("token has empty field!");
        if (!isValidRole(role))  throw new RestApiException(CommonErrorResponseCode.UNAUTHORIZED);
    }
    @NotNull
    private static Optional<MemberAuthDTO> makeMemberAuthDTO(Long id, ClubMemberRole role) {
        try {
            List<GrantedAuthority> authorities = new ArrayList<>();  //권한 부여.
            authorities.add(new SimpleGrantedAuthority(role.toString()));
            return Optional.ofNullable((new MemberAuthDTO(id, authorities))); //nullable -> empty()
        } catch (Exception e) {
            e.printStackTrace();
            //throw new IllegalArgumentException("not valid id, role for authentication ");
            return Optional.empty();
        }
    }


    public void sendErrorResponse(HttpServletResponse response, HttpStatus status){
        ObjectMapper objectMapper = new ObjectMapper();
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        try{
            response.getWriter().write(objectMapper.writeValueAsString(new RootResponse(status.value(), status.getReasonPhrase(), null, null)));
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}

