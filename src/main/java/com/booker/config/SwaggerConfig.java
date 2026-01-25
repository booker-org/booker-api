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
          .addSecuritySchemes(
            securitySchemeName,
            new SecurityScheme()
              .type(SecurityScheme.Type.HTTP)
              .scheme("bearer")
              .bearerFormat("JWT")
              .description("Enter the JWT token obtained from the /auth/login or /auth/register endpoint")
          )
      )
      .info(new Info()
        .title("Booker API")
        .version("1.0.0")
        .description("API for managing reading lists and the readers community.")
        .contact(new Contact()
          .name("Booker Team")
          .url("https://github.com/booker-org")
        )
        .license(new License()
          .name("MIT")
          .url("https://opensource.org/licenses/MIT")
        )
      )
    ;
  }
}