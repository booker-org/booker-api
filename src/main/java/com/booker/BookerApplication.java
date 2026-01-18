package com.booker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BookerApplication {
  public static void main(String[] args) {
    SpringApplication.run(BookerApplication.class, args);
    
    System.out.println("Application running at: http://localhost:8080");
    System.out.println("Swagger UI: http://localhost:8080/swagger-ui.html");
  }
}