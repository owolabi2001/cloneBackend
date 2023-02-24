package com.clone.cloneBackend.repository;

import com.clone.cloneBackend.domain.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser,Long> {

    Optional<AppUser> findByEmail(String email);

    AppUser findAppUsersByEmail(String email);
}
