package io.github.khaldouun2005.vrp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import io.github.khaldouun2005.vrp.entity.AppUser;
import io.github.khaldouun2005.vrp.repository.AppUserRepository;

public class AuthServiceTest {

    private AppUserRepository repo;
    private PasswordEncoder encoder;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        repo = mock(AppUserRepository.class);
        encoder = new BCryptPasswordEncoder();
        authService = new AuthService(repo, encoder);
    }

    @Test
    void registerHashesPassword() {
        when(repo.existsByEmail("a@b.com")).thenReturn(false);

        AppUser saved = authService.register("a@b.com", "secret");

        assertThat(saved.getPasswordHash()).isNotEqualTo("secret");
        assertThat(encoder.matches("secret", saved.getPasswordHash())).isTrue();
        verify(repo).save(saved);
    }

    @Test
    void registerRejectsDuplicateEmail() {
        when(repo.existsByEmail("a@b.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register("a@b.com", "secret"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("already registered");
    }

    @Test
    void loginReturnsUserForCorrectCredentials() {
        AppUser stored = new AppUser("a@b.com", encoder.encode("secret"), Instant.now());
        when(repo.findByEmail("a@b.com")).thenReturn(Optional.of(stored));

        AppUser result = authService.login("a@b.com", "secret");

        assertThat(result).isEqualTo(stored);
    }

    @Test
    void loginRejectsWrongPassword() {
        AppUser stored = new AppUser("a@b.com", encoder.encode("secret"), Instant.now());
        when(repo.findByEmail("a@b.com")).thenReturn(Optional.of(stored));

        assertThatThrownBy(() -> authService.login("a@b.com", "wrong"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("invalid credentials");
    }

    @Test
    void loginRejectsUnknownEmail() {
        when(repo.findByEmail("nobody@b.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login("nobody@b.com", "secret"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("invalid credentials");
    }
}