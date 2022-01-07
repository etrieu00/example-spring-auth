package com.etrieu00.examplewebsecurity.util.enumeration;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;

public enum CategoryType {
  INCOME,
  INVESTMENT,
  RETIREMENT,
  LOANS,
  RENT,
  INSURANCE,
  FOOD,
  NECESSITIES,
  OTHER,
  PROJECTION,
  @JsonEnumDefaultValue
  NONE
}
