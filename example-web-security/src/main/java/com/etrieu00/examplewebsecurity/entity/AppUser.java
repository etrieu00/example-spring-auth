package com.etrieu00.examplewebsecurity.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

@Getter
@Setter
@NoArgsConstructor
@Table("app_user")
public class AppUser {
  @Id
  private Long id;
  private String uuid;
  private Set<AppRole> userRoles = new HashSet<>();
  private String userEmail;
  private String userPassword;
  private AppProfile profile;
  @CreatedBy
  private String ufc;
  @LastModifiedBy
  private String ulm;
  @CreatedDate
  private LocalDateTime dtc;
  @LastModifiedDate
  private LocalDateTime dtm;

  public static AppUser build(Function<AppUser, AppUser> builder) {
    return builder.apply(new AppUser());
  }

  public AppUser setUuid(String uuid) {
    this.uuid = uuid;
    return this;
  }

  public AppUser setUserRole(Integer level, String name) {
    this.userRoles.add(new AppRole(level, name));
    return this;
  }

  public AppUser removeUserRole(String name) {
    this.userRoles.removeIf(role -> role.getRoleName().equals(name));
    return this;
  }

  public AppUser setUserEmail(String userEmail) {
    this.userEmail = userEmail;
    return this;
  }

  public AppUser setUserPassword(String userPassword) {
    this.userPassword = userPassword;
    return this;
  }

  public AppUser setProfile(AppProfile profile) {
    this.profile = profile;
    return this;
  }
}
