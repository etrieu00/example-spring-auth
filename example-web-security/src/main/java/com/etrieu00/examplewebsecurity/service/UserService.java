package com.etrieu00.examplewebsecurity.service;

import com.etrieu00.examplewebsecurity.entity.AppProfile;
import com.etrieu00.examplewebsecurity.entity.AppUser;
import com.etrieu00.examplewebsecurity.exception.UnauthorizedAccessException;
import com.etrieu00.examplewebsecurity.exception.UserNotFoundException;
import com.etrieu00.examplewebsecurity.repository.AppUserRepository;
import com.etrieu00.examplewebsecurity.request.ProfileUpdateRequest;
import com.etrieu00.examplewebsecurity.response.ProfileResponse;
import com.etrieu00.examplewebsecurity.util.AuthUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {

  private final AppUserRepository repository;

  /**
   * Update the authenticated user profile
   *
   * @param request from the authenticated user
   * @return latest profile information
   */
  public ProfileResponse updateUserProfile(final ProfileUpdateRequest request) {
    return getUuidFromSecurityContext()
      .map(repository::findByUuid)
      .filter(Optional::isPresent)
      .orElseThrow(() -> new UserNotFoundException("The user was not found."))
      .map(user -> updateProfile(user, request))
      .map(repository::save)
      .map(AppUser::getProfile)
      .map(ProfileResponse::new)
      .orElseThrow(() -> new UnauthorizedAccessException("Unauthorized access..."));
  }

  /**
   * Get the latest user profile information
   *
   * @return profile information
   */
  public ProfileResponse profileInformation() {
    return getUuidFromSecurityContext()
      .map(uuid -> repository
        .findByUuid(uuid)
        .map(AppUser::getProfile)
        .orElseThrow(() -> new UserNotFoundException("The user was not found.")))
      .map(ProfileResponse::new)
      .orElseThrow(() -> new UnauthorizedAccessException("Unauthorized access..."));
  }

  /**
   * Update the user profile
   *
   * @param user    from database
   * @param request from the authenticated user
   * @return updated user entry to be saved
   */
  private AppUser updateProfile(AppUser user, ProfileUpdateRequest request) {
    AppProfile profile = user.getProfile();
    profile.setFirstName(request.getFirstname() != null ? request.getFirstname() : profile.getFirstName())
      .setLastName(request.getLastname() != null ? request.getLastname() : profile.getLastName())
      .setPhoneNumber(request.getPhone() != null ? request.getPhone() : profile.getPhoneNumber())
      .setGender(request.getGender() != null ? request.getGender() : profile.getGender())
      .setDateOfBirth(request.getDob() != null ? request.getDob() : profile.getDateOfBirth());
    return user;
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
