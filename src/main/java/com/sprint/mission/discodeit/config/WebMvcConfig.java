package com.sprint.mission.discodeit.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

  private final MDCLoggingInterceptor mdcLoggingInterceptor;

  public WebMvcConfig(MDCLoggingInterceptor mdcLoggingInterceptor) {
    this.mdcLoggingInterceptor = mdcLoggingInterceptor;
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(mdcLoggingInterceptor)
        .addPathPatterns("/**");
  }

  @Bean
  MappingJackson2HttpMessageConverter httpConverter() {
    ObjectMapper httpMapper = new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    return new MappingJackson2HttpMessageConverter(httpMapper);
  }
}