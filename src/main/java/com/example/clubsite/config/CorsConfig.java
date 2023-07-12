package com.example.clubsite.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {
    //CORS: 웹 브라우저에서 실행되는 JavaScript 코드가 동일 출처 정책(Same-Origin Policy)을 우회하여 다른 도메인에서 리소스를 요청할 수 있도록 허용하는 메커니즘
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true); //쿠키, 인증 헤더 등 자격 증명 정보 허용
        config.addAllowedOriginPattern("*"); //모든 도메인 요청 허용
        config.addAllowedHeader("*"); //모든 요청 헤더 허용
        config.addAllowedMethod("*"); //모든 HTTP 메소드 허용
        source.registerCorsConfiguration("/api/**", config); //해당 패턴에 CORS 설정 적용
        return new CorsFilter(source);
    }
}
