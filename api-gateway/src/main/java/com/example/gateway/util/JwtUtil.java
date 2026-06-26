package com.example.gateway.util;

import com.example.gateway.security.GatewayPrincipal;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.List;

@Component
public class JwtUtil {

    private final Key key;
    private final String issuer;

    public JwtUtil(@Value("${security.jwt.secret}") String secret,
                   @Value("${security.jwt.issuer}") String issuer) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.issuer = issuer;
    }

    public GatewayPrincipal parsePrincipal(String token) {
        Claims claims = parseClaims(token);
        List<String> roles = claims.get("roles", List.class);
        return new GatewayPrincipal(
                claims.getSubject(),
                claims.get("email", String.class),
                roles == null ? List.of() : roles
        );
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .requireIssuer(issuer)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
