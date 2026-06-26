package com.example.user.service;

import com.example.user.model.AppUser;
import com.example.user.model.UserRole;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    @Test
    void issuesTokenWithUserClaims() {
        JwtService jwtService = new JwtService("test-secret-with-at-least-thirty-two-characters", "gateway-security-platform", 3600);
        AppUser user = new AppUser();
        user.setId(99L);
        user.setEmail("admin@example.com");
        user.setRoles(new LinkedHashSet<>(Set.of(UserRole.ADMIN)));

        Claims claims = jwtService.parse(jwtService.issue(user));

        assertThat(claims.getSubject()).isEqualTo("99");
        assertThat(claims.get("email", String.class)).isEqualTo("admin@example.com");
        assertThat(claims.get("roles", java.util.List.class)).containsExactly("ADMIN");
    }
}
