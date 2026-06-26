package com.example.gateway.security;

import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class RoutePolicyService {

    private static final Set<String> USER_OR_ADMIN = Set.of("USER", "ADMIN");
    private static final Set<String> ADMIN_ONLY = Set.of("ADMIN");

    public AccessDecision evaluate(String path, String method) {
        if (path.equals("/actuator/health") || path.startsWith("/fallback/")) {
            return AccessDecision.open();
        }
        if (path.equals("/auth/register") || path.equals("/auth/login")) {
            return AccessDecision.open();
        }
        if (path.startsWith("/products/admin")) {
            return AccessDecision.authenticated(ADMIN_ONLY);
        }
        if (path.startsWith("/products") || path.startsWith("/auth/me")) {
            return AccessDecision.authenticated(USER_OR_ADMIN);
        }
        return AccessDecision.authenticated(ADMIN_ONLY);
    }
}
