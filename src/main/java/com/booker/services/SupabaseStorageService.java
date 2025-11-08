package com.booker.services;

import java.io.IOException;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

@Service
public class SupabaseStorageService {
  @Value("${supabase.project-id}")
  private String SUPABASE_PROJECT_ID;

  @Value("${supabase.api-key}")
  private String SUPABASE_API_KEY;

  @Value("${storage.bucket}")
  private String STORAGE_BUCKET;

  private final RestTemplate restTemplate = new RestTemplate();

  public String uploadCover(MultipartFile file) throws IOException {
    String fileName = generateFileName(file.getOriginalFilename());
    String uploadUrl = "https://" + SUPABASE_PROJECT_ID + ".supabase.co/storage/v1/object/" + STORAGE_BUCKET + "/" + fileName;
    HttpHeaders headers = new HttpHeaders();

    headers.setBearerAuth(SUPABASE_API_KEY);
    headers.set("apikey", SUPABASE_API_KEY);

    String contentType = file.getContentType();
    headers.setContentType(MediaType.parseMediaType(contentType != null ? contentType : "application/octet-stream"));

    HttpEntity<byte[]> entity = new HttpEntity<>(file.getBytes(), headers);

    ResponseEntity<String> response = restTemplate.postForEntity(uploadUrl, entity, String.class);

    if (response.getStatusCode().is2xxSuccessful()) return getPublicUrl(fileName);
    else throw new RuntimeException("Erro no upload: " + response.getBody());
  }

  public void deleteCover(String fileName) {
    String deleteUrl = "https://" + SUPABASE_PROJECT_ID + ".supabase.co/storage/v1/object/" + STORAGE_BUCKET + "/" + fileName;
    HttpHeaders headers = new HttpHeaders();

    headers.setBearerAuth(SUPABASE_API_KEY);
    headers.set("apikey", SUPABASE_API_KEY);

    HttpEntity<Void> entity = new HttpEntity<>(headers);

    restTemplate.exchange(deleteUrl, HttpMethod.DELETE, entity, String.class);
  }

  public String replaceCover(String oldCoverUrl, MultipartFile newFile) throws IOException {
    if (oldCoverUrl == null || oldCoverUrl.isEmpty()) return uploadCover(newFile);

    String fileName = extractFileNameFromUrl(oldCoverUrl);

    if (fileName == null) return uploadCover(newFile);

    String updateUrl = "https://" + SUPABASE_PROJECT_ID + ".supabase.co/storage/v1/object/" + STORAGE_BUCKET + "/" + fileName;
    HttpHeaders headers = new HttpHeaders();

    headers.setBearerAuth(SUPABASE_API_KEY);
    headers.set("apikey", SUPABASE_API_KEY);

    String contentType = newFile.getContentType();
    headers.setContentType(MediaType.parseMediaType(contentType != null ? contentType : "application/octet-stream"));

    HttpEntity<byte[]> entity = new HttpEntity<>(newFile.getBytes(), headers);

    ResponseEntity<String> response = restTemplate.exchange(updateUrl, HttpMethod.PUT, entity, String.class);

    if (response.getStatusCode().is2xxSuccessful()) return oldCoverUrl;
    else throw new RuntimeException("Erro ao atualizar capa: " + response.getBody());
  }

  public String uploadOrReplaceCover(String existingCoverUrl, MultipartFile newFile) throws IOException {
    if (existingCoverUrl == null || existingCoverUrl.isEmpty()) return uploadCover(newFile);
    else return replaceCover(existingCoverUrl, newFile);
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