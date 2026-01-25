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
      FilterChain filterChain) throws ServletException, IOException {
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
          log.warn("Invalid token type. Expected access token for authentication. User: {}", username);
          filterChain.doFilter(request, response);

          return;
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (jwtService.isTokenValid(jwt, userDetails)) {
          log.debug("JWT token successfully validated for user: {}", username);

          UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
              userDetails,
              null,
              userDetails.getAuthorities());

          authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

          SecurityContextHolder.getContext().setAuthentication(authToken);
        } else
          log.warn("JWT token validation failed for user: {}", username);
      }
    } catch (ExpiredJwtException e) {
      log.warn("Expired JWT token for request: {} {}", request.getMethod(), request.getRequestURI());
    } catch (SignatureException e) {
      log.warn("Invalid JWT signature for request: {} {}", request.getMethod(), request.getRequestURI());
    } catch (MalformedJwtException e) {
      log.warn("Malformed JWT token for request: {} {}", request.getMethod(), request.getRequestURI());
    } catch (Exception e) {
      log.error("Error processing JWT token: {}", e.getMessage());
    }

    filterChain.doFilter(request, response);
  }
}