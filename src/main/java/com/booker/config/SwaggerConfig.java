package com.booker.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
      .info(new Info()
        .title("Booker API")
        .version("1.0.0")
        .description("API para gerenciamento de leituras e comunidade de leitores.")
        .contact(new Contact()
          .name("Equipe Booker")
          .url("https://github.com/booker-org"))
        .license(new License()
          .name("MIT")
          .url("https://opensource.org/licenses/MIT")
        )
      )
    ;
  }
}