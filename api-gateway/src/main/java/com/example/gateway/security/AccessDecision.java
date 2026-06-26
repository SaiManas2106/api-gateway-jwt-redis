package com.example.gateway.security;

import java.util.List;
import java.util.Set;

public record AccessDecision(boolean publicEndpoint, Set<String> allowedRoles) {

    public static AccessDecision open() {
        return new AccessDecision(true, Set.of());
    }

    public static AccessDecision authenticated(Set<String> allowedRoles) {
        return new AccessDecision(false, allowedRoles);
    }

    public boolean allows(List<String> userRoles) {
        if (publicEndpoint) {
            return true;
        }
        return userRoles.stream().anyMatch(allowedRoles::contains);
    }
}
