package com.sprint.mission.discodeit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
public class SecurityConfig {

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http,
      CookieCsrfTokenRepository cookieCsrfTokenRepository) throws Exception {

    return http
        .csrf(csrf -> csrf.csrfTokenRepository(cookieCsrfTokenRepository))
        .authorizeHttpRequests(request -> request
            .requestMatchers(
                "/static/**",
                "/",
                "/error",
                "/swagger-ui/**",
                "/v3/api-docs/**",
                "/actuator/**",
                "/assets/**",
                "/index.html",
                "/favicon.ico").permitAll()
            .requestMatchers("/api/auth/csrf-token").permitAll()
            .anyRequest().authenticated())
        .logout(logout -> logout.disable())
        .formLogin(form -> form.disable())
        .httpBasic(basic -> basic.disable())
        .build();
  }

  @Bean
  CookieCsrfTokenRepository cookieCsrfTokenRepository() {

    CookieCsrfTokenRepository cookieCsrfTokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();

    cookieCsrfTokenRepository.setCookieName("CSRF-TOKEN");
    cookieCsrfTokenRepository.setHeaderName("X-CSRF-TOKEN");

    return cookieCsrfTokenRepository;
  }
}
