package com.etrieu00.examplewebsecurity.service;

import com.etrieu00.examplewebsecurity.entity.AppProfile;
import com.etrieu00.examplewebsecurity.entity.AppUser;
import com.etrieu00.examplewebsecurity.exception.ExistingUserException;
import com.etrieu00.examplewebsecurity.exception.UserNotFoundException;
import com.etrieu00.examplewebsecurity.repository.AppUserRepository;
import com.etrieu00.examplewebsecurity.request.CredentialUpdateRequest;
import com.etrieu00.examplewebsecurity.request.LoginRequest;
import com.etrieu00.examplewebsecurity.request.SignUpRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

  @MockBean
  private AppUserRepository repository;

  @MockBean
  private JwtProviderService jwtProviderService;

  private AuthService service;

  private AppUser dummy;

  @BeforeEach
  void setUp() {
    service = spy(new AuthService(repository, jwtProviderService));
    dummy = AppUser.build(user -> user
      .setUuid(UUID.randomUUID().toString())
      .setUserPassword(BCrypt.hashpw("password", BCrypt.gensalt(12)))
      .setUserRole(1, "TESTER")
      .setUserEmail("example@example.com")
      .setProfile(AppProfile.build(profile -> profile
        .setDateOfBirth(LocalDate.now())
        .setGender("OTHER")
        .setLastName("Doe")
        .setFirstName("John")
        .setPhoneNumber("0123456789"))));
  }

  @Test
  void createUserAccount() {
    var request = new SignUpRequest(
      "example@example.com",
      "password",
      "John",
      "Doe"
    );
    when(repository.findByUserEmail(any())).thenReturn(Optional.empty());
    when(repository.save(any())).thenReturn(dummy);
    when(jwtProviderService.generateAccessToken(anyString())).thenReturn("access");
    when(jwtProviderService.generateRefreshToken(any())).thenReturn("refresh");
    when(jwtProviderService.reissueRefreshToken(anyString())).thenReturn("refresh");
    var results = service.createUserAccount(request);
    verify(repository, times(1)).save(any());
    verify(jwtProviderService, times(1)).generateRefreshToken(any());
    verify(jwtProviderService, times(1)).generateAccessToken(anyString());
    assertEquals("access", results.getAccess());
    assertEquals("refresh", results.getRefresh());
  }

  @Test
  void createUserAccountExists() {
    var request = new SignUpRequest(
      "example@example.com",
      "password",
      "John",
      "Doe"
    );
    when(repository.findByUserEmail(anyString())).thenReturn(Optional.of(dummy));
    assertThrows(ExistingUserException.class, () -> service.createUserAccount(request));
  }

  @Test
  void updateCredentialsPassword() {
    when(service.getUuidFromSecurityContext()).thenReturn(Optional.of(dummy.getUuid()));
    when(repository.findByUuid(anyString())).thenReturn(Optional.of(dummy));
    when(repository.save(any())).thenReturn(dummy);
    when(jwtProviderService.generateAccessToken(anyString())).thenReturn("access");
    when(jwtProviderService.generateRefreshToken(any())).thenReturn("refresh");
    when(jwtProviderService.reissueRefreshToken(anyString())).thenReturn("refresh");
    var credentials = new CredentialUpdateRequest("example@example.com", "password", "password!");
    var results = service.updateCredentials(credentials, "password");
    verify(repository, times(1)).save(any());
    assertEquals("access", results.getAccess());
    assertEquals("refresh", results.getRefresh());
  }

  @Test
  void updateCredentialsPasswordFail() {
    when(service.getUuidFromSecurityContext()).thenReturn(Optional.of(dummy.getUuid()));
    when(repository.findByUuid(anyString())).thenReturn(Optional.of(dummy));
    when(repository.save(any())).thenReturn(dummy);
    when(jwtProviderService.generateAccessToken(anyString())).thenReturn("access");
    when(jwtProviderService.generateRefreshToken(any())).thenReturn("refresh");
    when(jwtProviderService.reissueRefreshToken(anyString())).thenReturn("refresh");
    var credentials = new CredentialUpdateRequest("example@example.com", "password?", "password!");
    assertThrows(UserNotFoundException.class, () -> service.updateCredentials(credentials, "password"));
  }

  @Test
  void updateCredentialsEmail() {
    when(service.getUuidFromSecurityContext()).thenReturn(Optional.of(dummy.getUuid()));
    when(repository.findByUuid(anyString())).thenReturn(Optional.of(dummy));
    when(repository.save(any())).thenReturn(dummy);
    when(jwtProviderService.generateAccessToken(anyString())).thenReturn("access");
    when(jwtProviderService.generateRefreshToken(any())).thenReturn("refresh");
    when(jwtProviderService.reissueRefreshToken(anyString())).thenReturn("refresh");
    var credentials = new CredentialUpdateRequest("example2@example.com", "password", null);
    var results = service.updateCredentials(credentials, "username");
    verify(repository, times(1)).save(any());
    assertEquals("access", results.getAccess());
    assertEquals("refresh", results.getRefresh());
  }

  @Test
  void updateCredentialsEmailFail() {
    when(service.getUuidFromSecurityContext()).thenReturn(Optional.of(dummy.getUuid()));
    when(repository.findByUuid(anyString())).thenReturn(Optional.of(dummy));
    when(repository.save(any())).thenReturn(Optional.empty());
    when(jwtProviderService.generateAccessToken(anyString())).thenReturn("access");
    when(jwtProviderService.generateRefreshToken(any())).thenReturn("refresh");
    when(jwtProviderService.reissueRefreshToken(anyString())).thenReturn("refresh");
    var credentials = new CredentialUpdateRequest("example2@example.com", "password?", "password!");
    assertThrows(UserNotFoundException.class, () -> service.updateCredentials(credentials, "username"));
  }

  @Test
  void updateCredentialsIllegalArgs() {
    var credentials = new CredentialUpdateRequest("example2@example.com", "password?", "password!");
    assertThrows(IllegalArgumentException.class, () -> service.updateCredentials(credentials, "email"));
  }

  @Test
  void loginToUserAccount() {
    when(jwtProviderService.generateAccessToken(anyString())).thenReturn("access");
    when(jwtProviderService.generateRefreshToken(any())).thenReturn("refresh");
    when(jwtProviderService.reissueRefreshToken(anyString())).thenReturn("refresh");
    doReturn(Optional.of(dummy)).when(repository).findByUserEmail(anyString());
    var results = service.loginToUserAccount(new LoginRequest("example@example.com", "password"));
    assertEquals("access", results.getAccess());
    assertEquals("refresh", results.getRefresh());
  }

  @Test
  void generateTokens() {
    when(jwtProviderService.generateAccessToken(anyString())).thenReturn("access");
    when(jwtProviderService.reissueRefreshToken(anyString())).thenReturn("refresh");
    var results = service.generateTokens("refresh");
    assertEquals("access", results.getAccess());
    assertEquals("refresh", results.getRefresh());
  }

  @Test
  void generateOnlyAccessTokens() {
    when(jwtProviderService.generateAccessToken(anyString())).thenReturn("access");
    var results = service.generateTokens("refresh");
    assertEquals("access", results.getAccess());
    assertNull(results.getRefresh());
  }
}