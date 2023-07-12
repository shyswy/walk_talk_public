package com.example.clubsite.config;

import com.example.clubsite.filter.authentication.AuthFIlter;
import com.example.clubsite.utility.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@Log4j2
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)

public class SecurityConfig {
    private final CorsConfig corsFilter;
    private final JWTUtil jwtUtil;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers(
                        "/redis/**",
                        "/test/**",
                        "/api/v1/login", "/auth/reissue",
                        "/api/v1/config", "/favicon.ico**", "/swagger-ui/**", "/v3/api-docs", "/v2/api-docs", "/configuration/ui", "/swagger-resources/**", "/configuration/security", "/swagger-ui.html", "/webjars/**")
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .addFilter(corsFilter.corsFilter())
                .addFilterBefore(apiCheckFilter(), UsernamePasswordAuthenticationFilter.class)//Authentication Filter
                .csrf()
                .disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling()
                .authenticationEntryPoint((request, response, authException) -> jwtUtil.sendErrorResponse(response, HttpStatus.UNAUTHORIZED));
        return http.build();
    }

    @Bean
    @Primary
    public AuthFIlter apiCheckFilter() {
        return new AuthFIlter(jwtUtil);
    }
}









