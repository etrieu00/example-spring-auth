package com.etrieu00.examplewebsecurity.exception;

public class ExistingUserException extends RuntimeException {
  public ExistingUserException(String message) {
    super(message);
  }
}
