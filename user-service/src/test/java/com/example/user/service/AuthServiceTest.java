package com.example.user.service;

import com.example.user.api.AuthResponse;
import com.example.user.api.LoginRequest;
import com.example.user.api.RegisterRequest;
import com.example.user.exception.AuthException;
import com.example.user.model.AppUser;
import com.example.user.model.UserRole;
import com.example.user.repository.UserRepository;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthServiceTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final JwtService jwtService = new JwtService("test-secret-with-at-least-thirty-two-characters", "gateway-security-platform", 3600);
    private final AuthService authService = new AuthService(userRepository, jwtService);

    @Test
    void registersNewUserWithUserRole() {
        when(userRepository.existsByEmailIgnoreCase("new@example.com")).thenReturn(false);
        when(userRepository.save(any(AppUser.class))).thenAnswer(invocation -> {
            AppUser user = invocation.getArgument(0);
            user.setId(10L);
            return user;
        });

        AuthResponse response = authService.register(new RegisterRequest("new@example.com", "New User", "password123"));

        assertThat(response.user().email()).isEqualTo("new@example.com");
        assertThat(response.user().roles()).containsExactly(UserRole.USER);
        assertThat(response.accessToken()).isNotBlank();
    }

    @Test
    void rejectsInvalidLoginPassword() {
        AppUser user = new AppUser();
        user.setEmail("admin@example.com");
        user.setPasswordHash("$2a$10$Qx0r0qP9GYG7SU8gwQ2gleIIbG6U296fyVneptwX2vnm8TDqlAY1C");
        user.setRoles(new LinkedHashSet<>(Set.of(UserRole.ADMIN)));
        when(userRepository.findByEmailIgnoreCase("admin@example.com")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> authService.login(new LoginRequest("admin@example.com", "wrong-password")))
                .isInstanceOf(AuthException.class);
    }
}
