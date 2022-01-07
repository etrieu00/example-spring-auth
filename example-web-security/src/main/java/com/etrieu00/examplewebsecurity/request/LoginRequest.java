package com.etrieu00.examplewebsecurity.request;

import lombok.Value;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Value
public class LoginRequest {
  @Email
  String username;
  @NotNull
  String password;
}
