package com.sprint.mission.discodeit.swaggerconfig;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.List;

@Configuration
//@EnableWebMvc
public class SwaggerConfig {

    /* http://localhost:8080/swagger-ui/index.html 확인 경로 */
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(apiInfo())
                .servers(List.of(apiServer()));
    }

    private io.swagger.v3.oas.models.info.Info apiInfo() {
        return new io.swagger.v3.oas.models.info.Info()
                .title("Discodeit API 문서")
                .description("Discodeit 프로젝트의 Swagger API 문서입니다.");
    }

    private io.swagger.v3.oas.models.servers.Server apiServer() {
        return new io.swagger.v3.oas.models.servers.Server()
                .url("http://localhost:8080")
                .description("로컬 서버");
    }
}