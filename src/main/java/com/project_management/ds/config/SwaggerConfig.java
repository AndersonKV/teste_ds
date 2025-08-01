package com.project_management.ds.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Sistema de Gerenciamento de Projetos")
                        .description("API REST para gerenciar o ciclo de vida de projetos.")
                        .version("v1.0"));
    }
}
