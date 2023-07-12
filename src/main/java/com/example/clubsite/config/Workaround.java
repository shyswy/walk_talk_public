package com.example.clubsite.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.stereotype.Component;
import springfox.documentation.oas.web.OpenApiTransformationContext;
import springfox.documentation.oas.web.WebMvcOpenApiTransformationFilter;
import springfox.documentation.spi.DocumentationType;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Component
public class Workaround implements WebMvcOpenApiTransformationFilter {//WebMvcOpenApiTransformationFilter: Swagger 또는 OpenAPI 문서 변환과 관련된 필터를 정의하는 데 사용
    @Override
    public OpenAPI transform(OpenApiTransformationContext<HttpServletRequest> context) {
        OpenAPI openApi = context.getSpecification();
        Server testServer = new Server();
        testServer.setDescription("st");
        testServer.setUrl("https://tmp.co.kr");//Domain 설정
        openApi.setServers(Arrays.asList(testServer));
        return openApi;
    }

    @Override
    public boolean supports(DocumentationType documentationType) {
        return documentationType.equals(DocumentationType.OAS_30);
    }
}
