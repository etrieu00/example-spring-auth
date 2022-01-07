package com.etrieu00.examplewebsecurity.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AuthUtil {

  /**
   * Get the UUID of the authenticated user
   *
   * @return uuid of the authenticated user
   */
  public static Optional<String> getUuid() {
    return Optional.of(SecurityContextHolder.getContext())
      .map(SecurityContext::getAuthentication)
      .filter(Authentication::isAuthenticated)
      .map(Authentication::getPrincipal)
      .map(String.class::cast);
  }

  /**
   * Get the access token that was used to authenticate the user
   *
   * @return access token
   */
  public static Optional<String> getAccessToken() {
    return Optional.of(SecurityContextHolder.getContext())
      .map(SecurityContext::getAuthentication)
      .filter(Authentication::isAuthenticated)
      .map(Authentication::getCredentials)
      .map(String.class::cast);
  }

  /**
   * Get the roles of the authenticated user
   *
   * @return list of roles as strings
   */
  public static List<String> getRoles() {
    return Optional.of(SecurityContextHolder.getContext())
      .map(SecurityContext::getAuthentication)
      .filter(Authentication::isAuthenticated)
      .map(Authentication::getAuthorities)
      .orElseThrow()
      .stream()
      .map(SimpleGrantedAuthority.class::cast)
      .map(SimpleGrantedAuthority::getAuthority)
      .collect(Collectors.toList());
  }
}
