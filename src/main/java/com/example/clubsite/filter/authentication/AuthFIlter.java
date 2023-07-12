package com.example.clubsite.filter.authentication;

import com.example.clubsite.dto.security.MemberAuthDTO;
import com.example.clubsite.utility.JWTUtil;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Log4j2
@RequiredArgsConstructor
public class AuthFIlter extends OncePerRequestFilter {
    public static final String BEARER = "Bearer ";
    public static final String AUTHORIZATION = "Authorization";
    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) {
        try {
            Authentication authentication = getAuthentication(request);
            if (authentication != null) {
                SecurityContext context = SecurityContextHolder.getContext();
                context.setAuthentication(authentication);
            }
            chain.doFilter(request, response);
        } catch (JwtException e) {
            jwtUtil.sendErrorResponse(response, HttpStatus.UNAUTHORIZED);
        } catch (ServletException | IOException e) {
            jwtUtil.sendErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    private Authentication getAuthentication(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader == null || authorizationHeader.length() <= BEARER.length()) {
            return null;
        }
        String token = authorizationHeader.substring(BEARER.length());
        MemberAuthDTO memberAuthDTO = jwtUtil.validateAndExtractMemberAuthDTO(token)
                .orElseThrow(() -> new JwtException("token have problem (Authority, invalid, ...")); //
        return new UsernamePasswordAuthenticationToken(memberAuthDTO, null, memberAuthDTO.getAuthorities());
    }
}










