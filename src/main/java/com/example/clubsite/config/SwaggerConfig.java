package com.example.clubsite.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
public class SwaggerConfig {
    public static final String CONTACT_URL = "https://github.com/shyswy/walking-talk";
    public static final String CONTACT_NAME = "윤상현";
    public static final String CONTACT_EMAIL = "shyswy@naver.com";
    private String version = "V0.1";
    @Value("${host.url}")
    String hostUrl;

    @Bean
    public Docket api() {
        Server serverLocal = new Server("local", "http://localhost:8080", "for local usages", Collections.emptyList(), Collections.emptyList());
        Server stServer = new Server("test", "https://walktalk.CUSTOMEDinc.co.kr", "for stage server", Collections.emptyList(), Collections.emptyList());
        return new Docket(DocumentationType.SWAGGER_2)
                .servers(stServer, serverLocal)
                .select()
                .apis(RequestHandlerSelectors.any())
//                .paths(PathSelectors.any())
                .paths(PathSelectors.ant("/api/**").or(PathSelectors.ant("/auth/**")).or(PathSelectors.ant("/images/**")).or(PathSelectors.ant("/test/**"))) // "/api/" 및 "/auth/" 패턴 추가
                .build()
                .apiInfo(apiInfo())
                .host(hostUrl)
                .securityContexts(Arrays.asList(securityContext()))
                .securitySchemes(Arrays.asList(apiKey()));
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("WalkTalk-Swagger-UI")
                .description("걸음 수 기반 경쟁 채팅 플랫폼 WalkTalk의 API 설명 문서입니다.\n\n" +
                        "[로직 테스트 수행 방법]\n" +
                        "1:[테스트 로그인 컨트롤러]에서 이메일과 이름을 타이핑하여 access 토큰, refresh 토큰을 발급 받습니다.\n" +
                        "2:우측의 초록색 Authorize 버튼 클릭 후, 발급받은 access 토큰 앞에 [Bearer ] (Bearer+공백1칸)을 추가한 뒤 [Authorize] 버튼을 클릭합니다.  [Ex] [Bearer ACCESS_TOKEN]\n" +
                        "3:액세스토큰의 유효기간은 약 30분입니다. 만약 access 토큰이 만료되었다면, [인증 로직 컨트롤러]의 reissue 로직에 사용한 access 토큰과 refresh 토큰을 넣으면 새로운 토큰들이 발급됩니다.\n" +
                        "4:인증이 완료되었다면, 테스트할 API를 클릭 후, [Try it out] 버튼을 클릭합니다. (ParaMeters의 경우, required 부분만 채워넣으면 됩니다.\n" +
                        "5:사용 완료 후, [테스트 로그인 컨트롤러]에서 [테스트 회원 탈퇴]를 수행하면, 안전하게 로그인 정보가 삭제됩니다.")
                .version(version)
                .contact(new Contact(CONTACT_NAME, CONTACT_URL, CONTACT_EMAIL))
                .build();
    }

    private ApiKey apiKey() {
        return new ApiKey("JWT", "Authorization", "header");
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder().securityReferences(defaultAuth()).build();
    }

    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEveryThing");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Arrays.asList(new SecurityReference("JWT", authorizationScopes));
    }
}




