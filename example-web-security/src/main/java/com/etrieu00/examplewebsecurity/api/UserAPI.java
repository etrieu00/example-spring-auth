package com.etrieu00.examplewebsecurity.api;

import com.etrieu00.examplewebsecurity.exception.UnauthorizedAccessException;
import com.etrieu00.examplewebsecurity.exception.UserNotFoundException;
import com.etrieu00.examplewebsecurity.request.ProfileUpdateRequest;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Validated
@RequestMapping(value = "app/api/v1/user",
  produces = MediaType.APPLICATION_JSON_VALUE)
public interface UserAPI {

  @ResponseStatus(HttpStatus.OK)
  @GetMapping(value = "/profile")
  Object userProfileDetails();

  @ResponseStatus(HttpStatus.ACCEPTED)
  @PutMapping(value = "/profile", consumes = MediaType.APPLICATION_JSON_VALUE)
  Object updateUserProfile(@RequestBody @Valid final ProfileUpdateRequest body);

  @ResponseStatus(HttpStatus.CONFLICT)
  @ExceptionHandler(UserNotFoundException.class)
  Object handleConflicts(Exception e);

  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  @ExceptionHandler({
    ExpiredJwtException.class,
    UnauthorizedAccessException.class})
  Object handleUnauthorized(Exception e);
}
