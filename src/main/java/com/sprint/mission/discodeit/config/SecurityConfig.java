package com.sprint.mission.discodeit.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.filter.LoginFilter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer.SessionFixationConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

  private static final String AUTH_PATH = "/api/auth/login";

  private final UserDetailsService userDetailsService;
  private final ObjectMapper objectMapper;

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http,
      CookieCsrfTokenRepository cookieCsrfTokenRepository) throws Exception {

    CsrfTokenRequestAttributeHandler handler = new CsrfTokenRequestAttributeHandler();
    handler.setCsrfRequestAttributeName("_csrf");

    return http
        .addFilterBefore(loginFilter(securityContextRepository()),
            UsernamePasswordAuthenticationFilter.class)
        .sessionManagement(session ->
            session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .sessionFixation(SessionFixationConfigurer::changeSessionId))
        .csrf(csrf ->
            csrf
                .csrfTokenRequestHandler(handler)
                .csrfTokenRepository(cookieCsrfTokenRepository)
                .ignoringRequestMatchers(new AntPathRequestMatcher("/api/users", "POST"))
                .ignoringRequestMatchers(new AntPathRequestMatcher("/api/auth/login", "POST"))
        )
        .authorizeHttpRequests(request ->
            request
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
                .requestMatchers("/api/auth/csrf-token", "/api/auth/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
                .anyRequest().authenticated())
        .authenticationProvider(daoAuthenticationProvider())
        .logout(logout -> logout.disable())
        .httpBasic(basic -> basic.disable())
        .formLogin(form -> form.disable())
        .build();
  }

  @Bean
  LoginFilter loginFilter(SecurityContextRepository contextRepository) {
    LoginFilter loginFilter = new LoginFilter(objectMapper);
    loginFilter.setAuthenticationManager(new ProviderManager(List.of(daoAuthenticationProvider())));
    loginFilter.setFilterProcessesUrl(AUTH_PATH);
    loginFilter.setSecurityContextRepository(contextRepository);

    return loginFilter;
  }

  @Bean
  CookieCsrfTokenRepository cookieCsrfTokenRepository() {

    CookieCsrfTokenRepository cookieCsrfTokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();

    cookieCsrfTokenRepository.setCookieName("CSRF-TOKEN");
    cookieCsrfTokenRepository.setHeaderName("X-CSRF-TOKEN");

    return cookieCsrfTokenRepository;
  }

  @Bean
  PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }

  @Bean
  DaoAuthenticationProvider daoAuthenticationProvider() {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setPasswordEncoder(passwordEncoder());
    provider.setUserDetailsService(userDetailsService);

    return provider;
  }

  // SecurityContext를 HttpSession 스토리지에 저장
  @Bean
  SecurityContextRepository securityContextRepository() {
    return new HttpSessionSecurityContextRepository();
  }

}
