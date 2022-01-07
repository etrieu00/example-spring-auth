package com.etrieu00.examplewebsecurity.service;

import com.etrieu00.examplewebsecurity.entity.AppProfile;
import com.etrieu00.examplewebsecurity.entity.AppUser;
import com.etrieu00.examplewebsecurity.repository.AppUserRepository;
import com.etrieu00.examplewebsecurity.request.ProfileUpdateRequest;
import com.etrieu00.examplewebsecurity.util.enumeration.Gender;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class UserAggregateServiceTest {

  @MockBean
  private AppUserRepository repository;

  private UserService service;

  private AppUser dummy;

  @BeforeEach
  void setUp() {
    service = spy(new UserService(repository));
    dummy = AppUser.build(user -> user
      .setUuid(UUID.randomUUID().toString())
      .setUserPassword(BCrypt.hashpw("password", BCrypt.gensalt(12)))
      .setUserRole(1, "TESTER")
      .setUserEmail("example@example.com")
      .setProfile(AppProfile.build(profile -> profile
        .setDateOfBirth(LocalDate.parse("2022-01-01"))
        .setGender(Gender.OTHER.name())
        .setLastName("Doe")
        .setFirstName("John")
        .setPhoneNumber("0123456789"))));
  }

  @Test
  void updateUserProfile() {
    when(service.getUuidFromSecurityContext()).thenReturn(Optional.of(dummy.getUuid()));
    when(repository.findByUuid(anyString())).thenReturn(Optional.of(dummy));
    when(repository.save(any())).thenReturn(dummy);
    var request = new ProfileUpdateRequest("Mary", "Jane", "0000000000", Gender.FEMALE.name(), LocalDate.parse("2021-01-01"));
    var results = service.updateUserProfile(request);
    verify(repository, times(1)).save(any());
    assertEquals("Mary", results.getFirstname());
    assertEquals("Jane", results.getLastname());
    assertEquals("FEMALE", results.getGender());
    assertEquals("0000000000", results.getPhone());
    assertEquals(LocalDate.parse("2021-01-01"), results.getDob());
  }

  @Test
  void profileInformation() {
    when(service.getUuidFromSecurityContext()).thenReturn(Optional.of(dummy.getUuid()));
    when(repository.findByUuid(anyString())).thenReturn(Optional.of(dummy));
    var results = service.profileInformation();
    assertEquals("John", results.getFirstname());
    assertEquals("Doe", results.getLastname());
    assertEquals(Gender.OTHER.name(), results.getGender());
    assertEquals("0123456789", results.getPhone());
    assertEquals(LocalDate.parse("2022-01-01"), results.getDob());
  }
}