package com.etrieu00.examplewebsecurity.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.util.function.Function;

@Data
@Table("app_profile")
public class AppProfile {
  @Id
  private Long id;
  private String firstName;
  private String lastName;
  private String phoneNumber;
  private String gender;
  private LocalDate dateOfBirth;

  public static AppProfile build(Function<AppProfile, AppProfile> builder) {
    return builder.apply(new AppProfile());
  }

  public AppProfile setFirstName(String firstName) {
    this.firstName = firstName;
    return this;
  }

  public AppProfile setLastName(String lastName) {
    this.lastName = lastName;
    return this;
  }

  public AppProfile setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
    return this;
  }

  public AppProfile setGender(String gender) {
    this.gender = gender;
    return this;
  }

  public AppProfile setDateOfBirth(LocalDate dateOfBirth) {
    this.dateOfBirth = dateOfBirth;
    return this;
  }
}
