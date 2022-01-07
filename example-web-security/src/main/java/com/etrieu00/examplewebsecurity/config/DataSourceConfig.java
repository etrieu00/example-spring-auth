package com.etrieu00.examplewebsecurity.config;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jdbc.repository.config.EnableJdbcAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
@EnableJdbcAuditing
@RequiredArgsConstructor
public class DataSourceConfig {

  @Bean
  public AuditorAware<String> auditorAware() {
    return () -> Optional.of(SecurityContextHolder.getContext())
      .map(SecurityContext::getAuthentication)
      .filter(Authentication::isAuthenticated)
      .map(Authentication::getPrincipal)
      .map(String.class::cast)
      .or(() -> Optional.of("SYSTEM"));
  }
}