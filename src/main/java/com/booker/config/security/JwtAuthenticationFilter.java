package com.booker.config.security;

import java.io.IOException;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.booker.services.JwtService;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
  private final JwtService jwtService;
  private final UserDetailsService userDetailsService;

  @Override
  protected void doFilterInternal(
    HttpServletRequest request,
    HttpServletResponse response,
    FilterChain filterChain
  ) throws ServletException, IOException {
    final String authHeader = request.getHeader(SecurityConstants.HEADER_AUTHORIZATION);

    if (authHeader == null || !authHeader.startsWith(SecurityConstants.BEARER_PREFIX)) {
      filterChain.doFilter(request, response);
      return;
    }

    try {
      final String jwt = authHeader.substring(SecurityConstants.BEARER_PREFIX_LENGTH);
      final String username = jwtService.extractUsername(jwt);

      if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        if (!jwtService.isAccessToken(jwt)) {
          log.warn("Tipo de token inválido. Esperado token de acesso para autenticação. Usuário: {}", username);
          filterChain.doFilter(request, response);

          return;
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (jwtService.isTokenValid(jwt, userDetails)) {
          log.debug("Token JWT validado com sucesso para o usuário: {}", username);

          UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
            userDetails,
            null,
            userDetails.getAuthorities()
          );

          authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

          SecurityContextHolder.getContext().setAuthentication(authToken);
        } else log.warn("Falha na validação do token JWT para o usuário: {}", username);
      }
    } catch (ExpiredJwtException e) {
      log.warn("Token JWT expirado para a requisição: {} {}", request.getMethod(), request.getRequestURI());
    } catch (SignatureException e) {
      log.warn("Assinatura JWT inválida para a requisição: {} {}", request.getMethod(), request.getRequestURI());
    } catch (MalformedJwtException e) {
      log.warn("Token JWT malformado para a requisição: {} {}", request.getMethod(), request.getRequestURI());
    } catch (Exception e) {
      log.error("Erro ao processar o token JWT: {}", e.getMessage());
    }

    filterChain.doFilter(request, response);
  }
}