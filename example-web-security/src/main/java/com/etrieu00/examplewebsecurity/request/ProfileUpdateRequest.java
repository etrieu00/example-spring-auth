package com.etrieu00.examplewebsecurity.request;

import lombok.Value;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Value
public class ProfileUpdateRequest {
  @Size(min = 1, max = 50, message = "Must be more than 1 character and less than 50 characters")
  String firstname;
  @Size(min = 1, max = 50, message = "Must be more than 1 character and less than 50 characters")
  String lastname;
  @Size(min = 1, max = 16, message = "Must be more than 1 character and less than 15 characters")
  String phone;
  @NotNull
  String gender;
  LocalDate dob;
}
