package com.etrieu00.examplewebsecurity.controller;

import com.etrieu00.examplewebsecurity.api.UserAPI;
import com.etrieu00.examplewebsecurity.request.ProfileUpdateRequest;
import com.etrieu00.examplewebsecurity.response.ProfileResponse;
import com.etrieu00.examplewebsecurity.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserController implements UserAPI {

  private final UserService service;

  @Override
  public ProfileResponse userProfileDetails() {
    return service.profileInformation();
  }

  @Override
  public ProfileResponse updateUserProfile(ProfileUpdateRequest body) {
    return service.updateUserProfile(body);
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
