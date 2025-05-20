package com.sprint.mission.discodeit.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.filter.LoginFilter;
import com.sprint.mission.discodeit.filter.LogoutFilter;
import com.sprint.mission.discodeit.redis.RedisTokenRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.status.UserSessionService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer.SessionFixationConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;

@Slf4j
@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private static final String AUTH_PATH = "/api/auth/login";

  private final UserDetailsService userDetailsService;
  private final ObjectMapper objectMapper;
  private final UserRepository userRepository;
  private final RedisTokenRepository redisTokenRepository;
  private final FindByIndexNameSessionRepository<? extends Session> findByIndexNameSessionRepository;
  private final UserSessionService userSessionService;

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http,
      CookieCsrfTokenRepository cookieCsrfTokenRepository) throws Exception {

    CsrfTokenRequestAttributeHandler handler = new CsrfTokenRequestAttributeHandler();
    handler.setCsrfRequestAttributeName("_csrf");

    return http
        .addFilterBefore(loginFilter(securityContextRepository(), rememberMeServices()),
            UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(new LogoutFilter(redisTokenRepository, rememberMeServices()),
            BasicAuthenticationFilter.class)
        .sessionManagement(session ->
            session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .sessionFixation(SessionFixationConfigurer::changeSessionId)
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
                .sessionRegistry(sessionRegistry(findByIndexNameSessionRepository))
        )
        .rememberMe(rememberMe -> rememberMe
            .rememberMeCookieName("remember-me")
            .rememberMeParameter("remember-me")
            .tokenRepository(redisTokenRepository)
            .userDetailsService(userDetailsService)
            .tokenValiditySeconds(60 * 60 * 24 * 21)
        )
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
                .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                .anyRequest().hasRole("USER"))
        .authenticationProvider(daoAuthenticationProvider())
        .logout(logout -> logout.disable())
        .httpBasic(basic -> basic.disable())
        .formLogin(form -> form.disable())
        .build();
  }

  @Bean
  SpringSessionBackedSessionRegistry<? extends Session> sessionRegistry(
      FindByIndexNameSessionRepository<? extends Session> sessionRepository) {

    return new SpringSessionBackedSessionRegistry<>(sessionRepository);
  }

  @Bean
  LoginFilter loginFilter(SecurityContextRepository contextRepository,
      PersistentTokenBasedRememberMeServices rememberMeServices) {

    LoginFilter loginFilter = new LoginFilter(objectMapper, userRepository,
        findByIndexNameSessionRepository, userSessionService);
    loginFilter.setRememberMeServices(rememberMeServices);
    loginFilter.setAuthenticationManager(new ProviderManager(List.of(daoAuthenticationProvider())));
    loginFilter.setFilterProcessesUrl(AUTH_PATH);
    loginFilter.setSecurityContextRepository(contextRepository);

    return loginFilter;
  }


  @Bean
  PersistentTokenBasedRememberMeServices rememberMeServices() {

    PersistentTokenBasedRememberMeServices rememberMe = new PersistentTokenBasedRememberMeServices(
        "remember-me", userDetailsService,
        redisTokenRepository);

    rememberMe.setParameter("remember-me");
    rememberMe.setTokenValiditySeconds(60 * 60 * 24 * 21);

    return rememberMe;
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

  @Bean
  RoleHierarchy roleHierarchy() {
    return RoleHierarchyImpl.fromHierarchy(
        "ROLE_ADMIN > ROLE_CHANNEL_MANAGER\n" +
            "ROLE_CHANNEL_MANAGER > ROLE_USER"
    );
  }

}
