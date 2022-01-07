package com.etrieu00.examplewebsecurity.service;

import com.etrieu00.examplewebsecurity.entity.AppRole;
import com.etrieu00.examplewebsecurity.entity.AppUser;
import com.etrieu00.examplewebsecurity.response.AuthResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Setter
@Component
@ConfigurationProperties("jwt")
public class JwtProviderService {
  private Map<String, String> secret;
  private String issuer;
  private Map<String, String> url;
  private Map<String, Long> expiration;

  private SecretKey ACCESS_SECRET;
  private SecretKey REFRESH_SECRET;

  @PostConstruct
  public void init() {
    ACCESS_SECRET = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret.get("access")));
    REFRESH_SECRET = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret.get("refresh")));
  }

  public AuthResponse generateAuthResponse() {
    return null;
  }

  public String generateRefreshToken(@NonNull final AppUser user) {
    Instant now = Instant.now();
    return Jwts.builder()
      .setSubject("example-auth")
      .setIssuer(issuer)
      .setId(UUID.randomUUID().toString())
      .claim("id", user.getUuid())
      .claim("role", user.getUserRoles().stream().map(AppRole::getRoleName).collect(Collectors.toList()))
      .claim("email", user.getUserEmail())
      .claim("refresh_url", url.get("refresh"))
      .claim("entitlements", url.get("entitlement"))
      .setIssuedAt(Date.from(now))
      .setExpiration(Date.from(now.plus(expiration.get("access"), ChronoUnit.DAYS)))
      .signWith(REFRESH_SECRET)
      .compact();
  }

  /**
   * Issue new access token if the refresh token is expiring soon
   *
   * @param token refresh token
   * @return new refresh token with new expiration date
   */
  public String reissueRefreshToken(@NonNull final String token) {
    Instant now = Instant.now();
    Instant before = now.minus(10, ChronoUnit.DAYS);
    Jws<Claims> claims = parseRefreshToken(token);
    boolean expiringSoon = claims.getBody().getExpiration().before(Date.from(before));
    return !expiringSoon ? token :
      Jwts.builder()
        .setIssuer(issuer)
        .setId(UUID.randomUUID().toString())
        .claim("id", claims.getBody().get("id", String.class))
        .claim("role", claims.getBody().get("role", List.class))
        .setIssuedAt(Date.from(now))
        .setExpiration(Date.from(now.plus(expiration.get("refresh"), ChronoUnit.MINUTES)))
        .signWith(ACCESS_SECRET)
        .compact();
  }

  /**
   * Generate an access token with the refresh token
   *
   * @param token refresh token
   * @return access token
   */
  public String generateAccessToken(@NonNull final String token) {
    Instant now = Instant.now();
    Jws<Claims> claims = parseRefreshToken(token);
    return Jwts.builder()
      .setIssuer(issuer)
      .setId(UUID.randomUUID().toString())
      .claim("id", claims.getBody().get("id", String.class))
      .claim("role", claims.getBody().get("role", List.class))
      .setIssuedAt(Date.from(now))
      .setExpiration(Date.from(now.plus(expiration.get("access"), ChronoUnit.MINUTES)))
      .signWith(ACCESS_SECRET)
      .compact();
  }

  /**
   * Parse the refresh token
   *
   * @param token refresh token
   * @return claims from refresh token
   */
  public Jws<Claims> parseRefreshToken(@NonNull final String token) {
    return Jwts.parserBuilder()
      .setAllowedClockSkewSeconds(10)
      .requireIssuer(issuer)
      .setSigningKey(REFRESH_SECRET)
      .build()
      .parseClaimsJws(token);
  }

  /**
   * Parse the access token
   *
   * @param token access token
   * @return claims from access token
   */
  public Jws<Claims> parseAccessToken(@NonNull final String token) {
    return Jwts.parserBuilder()
      .setAllowedClockSkewSeconds(10)
      .requireIssuer(issuer)
      .setSigningKey(ACCESS_SECRET)
      .build()
      .parseClaimsJws(token);
  }
}
