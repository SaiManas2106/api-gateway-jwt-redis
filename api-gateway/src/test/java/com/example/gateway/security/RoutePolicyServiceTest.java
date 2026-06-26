package com.example.gateway.security;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RoutePolicyServiceTest {

    private final RoutePolicyService service = new RoutePolicyService();

    @Test
    void allowsLoginWithoutToken() {
        AccessDecision decision = service.evaluate("/auth/login", "POST");

        assertThat(decision.publicEndpoint()).isTrue();
    }

    @Test
    void requiresAdminForProductAdministration() {
        AccessDecision decision = service.evaluate("/products/admin/42/stock", "PATCH");

        assertThat(decision.publicEndpoint()).isFalse();
        assertThat(decision.allows(List.of("USER"))).isFalse();
        assertThat(decision.allows(List.of("ADMIN"))).isTrue();
    }

    @Test
    void allowsUserOrAdminForCatalogReads() {
        AccessDecision decision = service.evaluate("/products/42", "GET");

        assertThat(decision.allows(List.of("USER"))).isTrue();
        assertThat(decision.allows(List.of("ADMIN"))).isTrue();
    }
}
