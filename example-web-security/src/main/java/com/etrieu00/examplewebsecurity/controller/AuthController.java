package com.etrieu00.examplewebsecurity.controller;

import com.etrieu00.examplewebsecurity.api.AuthAPI;
import com.etrieu00.examplewebsecurity.request.CredentialUpdateRequest;
import com.etrieu00.examplewebsecurity.request.LoginRequest;
import com.etrieu00.examplewebsecurity.request.SignUpRequest;
import com.etrieu00.examplewebsecurity.response.AuthResponse;
import com.etrieu00.examplewebsecurity.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthController implements AuthAPI {

  private final AuthService service;

  @Override
  public AuthResponse createUserAccount(SignUpRequest body) {
    return service.createUserAccount(body);
  }

  @Override
  public AuthResponse updateCredentials(CredentialUpdateRequest body, String type) {
    return service.updateCredentials(body, type);
  }

  @Override
  public AuthResponse loginUserAccount(LoginRequest body) {
    return service.loginToUserAccount(body);
  }

  @Override
  public AuthResponse issueAccessToken(String token) {
    return service.generateTokens(token);
  }

  @Override
  public Map<String, ?> handleConflicts(Exception e) {
    return Map.of("status", 409, "message", e.getMessage());
  }

  @Override
  public Map<String, ?> handleUnauthorized(Exception e) {
    return Map.of("status", 401, "message", e.getMessage());
  }
}
