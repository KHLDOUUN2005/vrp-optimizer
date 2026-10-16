package io.github.khaldouun2005.vrp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.khaldouun2005.vrp.entity.AppUser;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByEmail(String email);
    boolean existsByEmail(String email);
}
