package com.booker.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
  @Bean
  OpenAPI customOpenAPI() {
    final String securitySchemeName = "bearerAuth";

    return new OpenAPI()
      .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
      .components(
        new Components()
          .addSecuritySchemes(securitySchemeName,
            new SecurityScheme()
              .type(SecurityScheme.Type.HTTP)
              .scheme("bearer")
              .bearerFormat("JWT")
              .description("Insira o token JWT obtido no endpoint /auth/login ou /auth/register")))
      .info(new Info()
        .title("Booker API")
        .version("1.0.0")
        .description("API para gerenciamento de leituras e comunidade de leitores.")
        .contact(new Contact()
          .name("Equipe Booker")
          .url("https://github.com/booker-org"))
        .license(new License()
          .name("MIT")
          .url("https://opensource.org/licenses/MIT")));
  }
}