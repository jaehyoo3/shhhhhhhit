package com.foorend.api.common.config;

import com.foorend.api.common.constants.GlobalConstants;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**-------------------------------------------------------------
 # FileName : SwaggerConfig.java
 # Author   : foodinko
 # Desc     : springdoc-openapi (Swagger 3) 설정
 #            기본 Response Message : 401 (UNAUTHORIZED)
 # Date     : 2025-08-05 (Refactored)
 /**----------------------------------------------------------**/
@Configuration
public class SwaggerConfig {

    @Value("${swagger.enable.flag:true}")
    Boolean isSwaggerEnable;

    @Bean
    public OpenAPI openAPI() {
        if (!isSwaggerEnable) {
            return new OpenAPI();
        }

        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .info(apiInfo());
    }

    private Info apiInfo() {
        Contact contact = new Contact()
                .name("foodinko");

        return new Info()
                .title("foodinko API")
                .description("foodinko service REST API")
                .version("1.0")
                .contact(contact);
    }

    // Global Response Message 설정
    @Bean
    public GlobalOpenApiCustomizer globalOpenApiCustomizer() {
        return openApi -> {
            // 모든 API에 401 Unauthorized 응답 메시지 추가
            ApiResponse unauthorizedResponse = new ApiResponse()
                    .description(GlobalConstants.API_UNAUTHORIZED_401_MSG);

            openApi.getPaths().values().stream()
                    .flatMap(pathItem -> pathItem.readOperations().stream())
                    .forEach(operation -> {
                        if (operation.getResponses() == null) {
                            operation.setResponses(new ApiResponses());
                        }
                        operation.getResponses().addApiResponse(String.valueOf(GlobalConstants.API_UNAUTHORIZED_401), unauthorizedResponse);
                    });
        };
    }

}