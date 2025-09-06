package com.booker.controllers;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/example")
@Tag(name = "Example", description = "Endpoints de exemplo para teste do Swagger")
public class Example {
  @GetMapping("/status")
  @Operation(summary = "Status da aplicação", description = "Retorna informações sobre o status atual da aplicação")
  @ApiResponse(responseCode = "200", description = "Status da aplicação obtido com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"status\": \"running\", \"timestamp\": 1693651200000, \"version\": \"1.0.0\", \"environment\": \"development\"}")))
  public ResponseEntity<Map<String, Object>> getStatus() {
    Map<String, Object> response = new HashMap<>();
    response.put("status", "running");
    response.put("timestamp", System.currentTimeMillis());
    response.put("version", "1.0.0");
    response.put("environment", "development");
    return ResponseEntity.ok(response);
  }
}