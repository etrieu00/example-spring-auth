package com.etrieu00.examplewebsecurity.response;

import com.etrieu00.examplewebsecurity.entity.AppProfile;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.function.Function;


@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfileResponse {
  private String firstname;
  private String lastname;
  private String phone;
  private String gender;
  private LocalDate dob;

  public ProfileResponse(AppProfile profile) {
    this.firstname = profile.getFirstName();
    this.lastname = profile.getLastName();
    this.phone = profile.getPhoneNumber();
    this.gender = profile.getGender();
    this.dob = profile.getDateOfBirth();
  }

  public static ProfileResponse build(Function<ProfileResponse, ProfileResponse> builder) {
    return builder.apply(new ProfileResponse());
  }

  public ProfileResponse setFirstname(String firstname) {
    this.firstname = firstname;
    return this;
  }

  public ProfileResponse setLastname(String lastname) {
    this.lastname = lastname;
    return this;
  }

  public ProfileResponse setPhone(String phone) {
    this.phone = phone;
    return this;
  }

  public ProfileResponse setGender(String gender) {
    this.gender = gender;
    return this;
  }

  public ProfileResponse setDob(LocalDate dob) {
    this.dob = dob;
    return this;
  }
}


