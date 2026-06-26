package com.example.gateway.security;

import java.util.List;

public record GatewayPrincipal(String userId, String email, List<String> roles) {
}
