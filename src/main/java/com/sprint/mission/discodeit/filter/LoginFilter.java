package com.sprint.mission.discodeit.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.auth.LoginRequest;
import com.sprint.mission.discodeit.entity.user.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

  private final ObjectMapper objectMapper;
  private final UserRepository userRepository;

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request,
      HttpServletResponse response) throws AuthenticationException {

    try {
      LoginRequest loginRequest = objectMapper.readValue(request.getInputStream(),
          LoginRequest.class);

      UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
          loginRequest.username(), loginRequest.password());

      return getAuthenticationManager().authenticate(authentication);

    } catch (IOException e) {
      throw new BadCredentialsException("IOException 발생");
    }
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain, Authentication authResult) throws IOException, ServletException {

    SecurityContextHolder.getContext().setAuthentication(authResult);
    HttpSessionSecurityContextRepository contextRepository = new HttpSessionSecurityContextRepository();
    contextRepository.saveContext(SecurityContextHolder.getContext(), request, response);

    UserDetails principal = (UserDetails) authResult.getPrincipal();
    User user = userRepository.findUserByUsername(principal.getUsername());

    getRememberMeServices().loginSuccess(request, response, authResult);

    UserDto userDto = UserDto.builder()
        .id(user.getId())
        .username(user.getUsername())
        .email(user.getEmail())
        .online(user.isThereHere())
        .Role(user.getRole())
        .build();

    response.setStatus(HttpServletResponse.SC_OK);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    objectMapper.writeValue(response.getWriter(), userDto);
  }

  @Override
  protected void unsuccessfulAuthentication(HttpServletRequest request,
      HttpServletResponse response, AuthenticationException failed)
      throws IOException, ServletException {
    super.unsuccessfulAuthentication(request, response, failed);
  }
}
