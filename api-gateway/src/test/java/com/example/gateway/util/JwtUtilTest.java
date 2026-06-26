package com.example.gateway.util;

import com.example.gateway.security.GatewayPrincipal;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtUtilTest {

    private static final String SECRET = "test-secret-with-at-least-thirty-two-characters";
    private static final String ISSUER = "gateway-security-platform";

    private final JwtUtil jwtUtil = new JwtUtil(SECRET, ISSUER);

    @Test
    void parsesUserIdentityAndRoles() {
        String token = token(ISSUER);

        GatewayPrincipal principal = jwtUtil.parsePrincipal(token);

        assertThat(principal.userId()).isEqualTo("123");
        assertThat(principal.email()).isEqualTo("buyer@example.com");
        assertThat(principal.roles()).containsExactly("USER", "ADMIN");
    }

    @Test
    void rejectsUnexpectedIssuer() {
        String token = token("other-issuer");

        assertThatThrownBy(() -> jwtUtil.parsePrincipal(token)).isInstanceOf(RuntimeException.class);
    }

    private String token(String issuer) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject("123")
                .setIssuer(issuer)
                .claim("email", "buyer@example.com")
                .claim("roles", List.of("USER", "ADMIN"))
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(300)))
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }
}
