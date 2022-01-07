package com.etrieu00.examplewebsecurity.service;

import com.etrieu00.examplewebsecurity.entity.AppProfile;
import com.etrieu00.examplewebsecurity.entity.AppUser;
import com.etrieu00.examplewebsecurity.exception.ExistingUserException;
import com.etrieu00.examplewebsecurity.exception.UserNotFoundException;
import com.etrieu00.examplewebsecurity.repository.AppUserRepository;
import com.etrieu00.examplewebsecurity.request.CredentialUpdateRequest;
import com.etrieu00.examplewebsecurity.request.LoginRequest;
import com.etrieu00.examplewebsecurity.request.SignUpRequest;
import com.etrieu00.examplewebsecurity.response.AuthResponse;
import com.etrieu00.examplewebsecurity.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

  private static final Integer SALT = 12;
  private final AppUserRepository repository;
  private final JwtProviderService jwtProviderService;

  /**
   * Create a new user account
   *
   * @param request contains information to create a new account
   * @return new tokens
   */
  public AuthResponse createUserAccount(final SignUpRequest request) {
    return Optional.of(repository.findByUserEmail(request.getUsername().toLowerCase()))
      .filter(Optional::isEmpty)
      .orElseThrow(() -> new ExistingUserException("The user already exist"))
      .or(() -> Optional.of(createNewUserFromRequest(request)))
      .map(jwtProviderService::generateRefreshToken)
      .map(this::generateTokens)
      .orElseThrow(() -> new UserNotFoundException("The user was not found."));
  }

  /**
   * Update user credentials
   *
   * @param request new credentials
   * @param type    which credential to update
   * @return new tokens
   */
  public AuthResponse updateCredentials(final CredentialUpdateRequest request, final String type) {
    switch (type) {
      case "password":
        return getUuidFromSecurityContext()
          .map(repository::findByUuid)
          .filter(Optional::isPresent)
          .orElseThrow(() -> new UserNotFoundException("The user was not found."))
          .filter(user -> BCrypt.checkpw(request.getOldPassword(), user.getUserPassword()))
          .map(user -> user.setUserPassword(BCrypt.hashpw(request.getNewPassword(), BCrypt.gensalt(SALT))))
          .map(repository::save)
          .map(jwtProviderService::generateRefreshToken)
          .map(this::generateTokens)
          .orElseThrow(() -> new UserNotFoundException("The username or password is incorrect."));
      case "username":
        return getUuidFromSecurityContext()
          .map(repository::findByUuid)
          .filter(Optional::isPresent)
          .orElseThrow(() -> new UserNotFoundException("The user was not found."))
          .filter(user -> BCrypt.checkpw(request.getOldPassword(), user.getUserPassword()))
          .map(user -> user.setUserEmail(request.getUsername().toLowerCase()))
          .map(repository::save)
          .map(jwtProviderService::generateRefreshToken)
          .map(this::generateTokens)
          .orElseThrow(() -> new UserNotFoundException("The username or password is incorrect."));
      default:
        throw new IllegalArgumentException("Invalid param");
    }
  }

  /**
   * Login to user account with password and email
   *
   * @param request contains password and email
   * @return new tokens
   */
  public AuthResponse loginToUserAccount(final LoginRequest request) {
    return repository.findByUserEmail(request.getUsername())
      .filter(user -> BCrypt.checkpw(request.getPassword(), user.getUserPassword()))
      .map(jwtProviderService::generateRefreshToken)
      .map(this::generateTokens)
      .orElseThrow(() -> new UserNotFoundException("The user was not found."));
  }

  /**
   * Generate new tokens for the authenticated user
   *
   * @param refresh current refresh token
   * @return new tokens
   */
  public AuthResponse generateTokens(final String refresh) {
    return new AuthResponse(
      jwtProviderService.reissueRefreshToken(refresh),
      jwtProviderService.generateAccessToken(refresh)
    );
  }

  protected AppUser createNewUserFromRequest(SignUpRequest request) {
    return repository.save(AppUser.build(user -> user
      .setUserEmail(request.getUsername().toLowerCase())
      .setUserPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt(SALT)))
      .setUserRole(1, "USER")
      .setUserRole(2, "DEVELOPER")
      .setUuid(UUID.randomUUID().toString())
      .setProfile(AppProfile.build(profile -> profile
        .setFirstName(request.getFirstname())
        .setLastName(request.getLastname())))));
  }

  /**
   * Mock this ti make it testable
   * Use the util to get the uuid
   *
   * @return optional of the uuid
   */
  protected Optional<String> getUuidFromSecurityContext() {
    return AuthUtil.getUuid();
  }
}
