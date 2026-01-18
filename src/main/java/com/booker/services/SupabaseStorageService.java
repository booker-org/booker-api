package com.booker.services;

import java.io.IOException;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.reactive.function.client.WebClient;

@Service @RequiredArgsConstructor
public class SupabaseStorageService {
  @Value("${supabase.project-id}")
  private String SUPABASE_PROJECT_ID;

  @Value("${supabase.api-key}")
  private String SUPABASE_API_KEY;

  @Value("${storage.bucket}")
  private String STORAGE_BUCKET;

  private final WebClient.Builder webClientBuilder;

  public String uploadCover(MultipartFile file) throws IOException {
    String fileName = generateFileName(file.getOriginalFilename());
    String contentType = file.getContentType();

    WebClient webClient = webClientBuilder.baseUrl("https://" + SUPABASE_PROJECT_ID + ".supabase.co").build();

    webClient.post()
      .uri("/storage/v1/object/" + STORAGE_BUCKET + "/" + fileName)
      .header("Authorization", "Bearer " + SUPABASE_API_KEY)
      .header("apikey", SUPABASE_API_KEY)
      .contentType(MediaType.parseMediaType(contentType != null ? contentType : "application/octet-stream"))
      .bodyValue(file.getBytes())
      .retrieve()
      .bodyToMono(String.class)
      .block()
    ;

    return getPublicUrl(fileName);
  }

  public void deleteCover(String fileName) {
    WebClient webClient = webClientBuilder.baseUrl("https://" + SUPABASE_PROJECT_ID + ".supabase.co").build();

    webClient.delete()
      .uri("/storage/v1/object/" + STORAGE_BUCKET + "/" + fileName)
      .header("Authorization", "Bearer " + SUPABASE_API_KEY)
      .header("apikey", SUPABASE_API_KEY)
      .retrieve()
      .bodyToMono(String.class)
      .block();
  }

  public String replaceCover(String oldCoverUrl, MultipartFile newFile) throws IOException {
    if (oldCoverUrl == null || oldCoverUrl.isEmpty()) return uploadCover(newFile);

    String fileName = extractFileNameFromUrl(oldCoverUrl);

    if (fileName == null) return uploadCover(newFile);

    String contentType = newFile.getContentType();

    WebClient webClient = webClientBuilder.baseUrl("https://" + SUPABASE_PROJECT_ID + ".supabase.co").build();

    webClient.put()
      .uri("/storage/v1/object/" + STORAGE_BUCKET + "/" + fileName)
      .header("Authorization", "Bearer " + SUPABASE_API_KEY)
      .header("apikey", SUPABASE_API_KEY)
      .contentType(MediaType.parseMediaType(contentType != null ? contentType : "application/octet-stream"))
      .bodyValue(newFile.getBytes())
      .retrieve()
      .bodyToMono(String.class)
      .block()
    ;

    return oldCoverUrl;
  }

  public String uploadOrReplaceCover(String existingCoverUrl, MultipartFile newFile) throws IOException {
    if (existingCoverUrl == null || existingCoverUrl.isEmpty()) return uploadCover(newFile);

    return replaceCover(existingCoverUrl, newFile);
  }

  private String generateFileName(String originalFileName) {
    String extension = originalFileName.substring(originalFileName.lastIndexOf("."));

    return "covers/" + UUID.randomUUID().toString() + extension;
  }

  private String getPublicUrl(String fileName) {
    return "https://" + SUPABASE_PROJECT_ID + ".supabase.co/storage/v1/object/public/" + STORAGE_BUCKET + "/" + fileName;
  }

  public String extractFileNameFromUrl(String url) {
    if (url == null || url.isEmpty()) return null;

    // Extrai o nome do arquivo da URL pública do Supabase
    // Exemplo: https://project.supabase.co/storage/v1/object/public/bucket/covers/file.jpg
    String publicPath = "/storage/v1/object/public/" + STORAGE_BUCKET + "/";
    int index = url.indexOf(publicPath);

    if (index != -1) return url.substring(index + publicPath.length());

    return null;
  }
}