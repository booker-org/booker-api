package com.booker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BookerApplication {
  public static void main(String[] args) {
    SpringApplication.run(BookerApplication.class, args);

    System.out.println("Aplicação rodando em: http://localhost:8080");
  }
}