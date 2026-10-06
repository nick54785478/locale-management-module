package com.example.demo.config.config;

import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.parameters.Parameter;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Locale Management API")
                        .version("1.0.0")
                        .description("API Documentation for Locale Management Module"));
    }

    /**
     * 全域設定：為每支 API 自動加上 Accept-Language Header 欄位
     */
    @Bean
    public OperationCustomizer customGlobalHeaders() {
        return (operation, handlerMethod) -> {
            operation.addParametersItem(new Parameter()
                    .in("header")
                    .name("Accept-Language")
                    .description("選擇語系 (例如: zh-tw, en-us, zh-cn)")
                    .required(false)
                    .example("zh-tw"));
            return operation;
        };
    }
}
