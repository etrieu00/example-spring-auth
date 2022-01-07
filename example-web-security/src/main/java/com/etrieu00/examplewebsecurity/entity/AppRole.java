package com.etrieu00.examplewebsecurity.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;

@Data
@JsonIgnoreProperties({"id", "appLevel"})
public class AppRole {
  @Id
  @EqualsAndHashCode.Exclude
  private Long id;
  private final Integer appLevel;
  private final String roleName;
}
