package com.sprint.mission.discodeit.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

public class LogoutFilter extends OncePerRequestFilter {

  private static final String LOGOUT_URI = "/api/auth/logout";
  private static final String LOGOUT_HTTP_METHOD = "POST";

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    HttpSession session = request.getSession();
    session.invalidate();
    SecurityContextHolder.clearContext();

  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
    return !(LOGOUT_URI.equals(request.getServletPath()) && LOGOUT_HTTP_METHOD.equals(
        request.getMethod()));
  }
}
