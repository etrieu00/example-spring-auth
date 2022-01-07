package com.etrieu00.examplewebsecurity.config;

import com.etrieu00.examplewebsecurity.exception.UnauthorizedAccessException;
import com.etrieu00.examplewebsecurity.service.JwtProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

  private AuthenticationManager manager;
  private AuthenticationConverter converter;
  private AuthenticationSuccessHandler successHandler;

  @Autowired
  public void setManager(@Lazy AuthenticationManager manager) {
    this.manager = manager;
  }

  @Autowired
  public void setConverter(@Lazy AuthenticationConverter converter) {
    this.converter = converter;
  }

  @Autowired
  public void setSuccessHandler(@Lazy AuthenticationSuccessHandler successHandler) {
    this.successHandler = successHandler;
  }

  @Override
  public void configure(WebSecurity web) throws Exception {
    web.ignoring().antMatchers(
      "/app/api/v1/auth/signup",
      "/app/api/v1/auth/login",
      "/app/api/v1/auth/refresh"
    );
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    var filter = new AuthenticationFilter(manager, converter);
    filter.setSuccessHandler(successHandler);
    http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
      .anonymous().disable()
      .httpBasic().disable()
      .formLogin().disable()
      .logout().disable()
      .csrf().disable()
      .cors()
      .and()
      .authorizeHttpRequests()
      .antMatchers("/app/api/v1/user/**", "/app/api/v1/auth/user")
      .authenticated().and()
      .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
  }

  @Bean
  public CorsFilter corsFilter() {
    UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
    CorsConfiguration corsConfiguration = new CorsConfiguration();
    corsConfiguration.setAllowCredentials(true);
    corsConfiguration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:4200"));
    corsConfiguration.setAllowedHeaders(Arrays.asList(
      "Origin",
      "Access-Control-Allow-Origin",
      "Content-Type",
      "Accept", "Jwt-Token",
      "Authorization",
      "Origin, Accept",
      "X-Requested-With",
      "Access-Control-Request-Method",
      "Access-Control-Request-Headers"
    ));
    corsConfiguration.setExposedHeaders(Arrays.asList(
      "Origin",
      "Content-Type",
      "Accept",
      "Jwt-Token",
      "Authorization",
      "Access-Control-Allow-Origin",
      "Access-Control-Allow-Origin",
      "Access-Control-Allow-Credentials", "Filename"
    ));
    corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
    urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
    return new CorsFilter(urlBasedCorsConfigurationSource);
  }

  @Bean
  public AuthenticationConverter authenticationConverter() {
    return request -> Optional.of(request)
      .map(data -> data.getHeader("Authorization"))
      .filter(bearer -> !bearer.isEmpty())
      .map(token -> token.split(" ")[1])
      .map(access -> new UsernamePasswordAuthenticationToken(access, access))
      .orElseThrow(() -> new UnauthorizedAccessException("Missing access token"));
  }

  @Bean
  public AuthenticationManager authenticationManager(JwtProviderService provider) {
    return authentication -> Optional.of(authentication)
      .map(auth -> provider.parseAccessToken(auth.getCredentials().toString()))
      .map(jwt -> new UsernamePasswordAuthenticationToken(
        jwt.getBody().get("id", String.class),
        authentication.getCredentials(),
        convertRolesToAuthorities(jwt.getBody().get("role", List.class))))
      .orElseThrow(() -> new UnauthorizedAccessException("Authentication failed"));
  }

  @Bean
  public AuthenticationSuccessHandler authenticationSuccessHandler() {
    return (req, res, chain) -> {
    };
  }

  protected List<SimpleGrantedAuthority> convertRolesToAuthorities(List<String> roles) {
    return roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
  }
}