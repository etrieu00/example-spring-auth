package com.etrieu00.examplewebsecurity.util.enumeration;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;

public enum Gender {
  MALE,
  FEMALE,
  @JsonEnumDefaultValue
  OTHER
}
