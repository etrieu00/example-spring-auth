package com.etrieu00.examplewebsecurity.request;

import lombok.Value;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Value
public class SignUpRequest {
  @Email
  String username;
  String password;
  @Size(min = 1, max = 50, message = "Must be more than 1 character and less than 50 characters")
  String firstname;
  @Size(min = 1, max = 50, message = "Must be more than 1 character and less than 50 characters")
  String lastname;
}