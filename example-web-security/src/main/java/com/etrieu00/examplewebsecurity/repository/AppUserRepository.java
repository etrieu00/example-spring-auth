package com.etrieu00.examplewebsecurity.repository;

import com.etrieu00.examplewebsecurity.entity.AppUser;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface AppUserRepository extends CrudRepository<AppUser, Long> {
  Optional<AppUser> findByUuid(String uuid);

  Optional<AppUser> findByUserEmail(String userEmail);
}
