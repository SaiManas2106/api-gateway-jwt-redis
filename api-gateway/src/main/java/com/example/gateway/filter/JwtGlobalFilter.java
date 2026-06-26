package com.example.gateway.filter;

import com.example.gateway.http.GatewayErrorWriter;
import com.example.gateway.security.AccessDecision;
import com.example.gateway.security.GatewayPrincipal;
import com.example.gateway.security.RoutePolicyService;
import com.example.gateway.util.JwtUtil;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import reactor.core.publisher.Mono;

@Component
public class JwtGlobalFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;
    private final RoutePolicyService routePolicyService;
    private final GatewayErrorWriter errorWriter;

    public JwtGlobalFilter(JwtUtil jwtUtil, RoutePolicyService routePolicyService, GatewayErrorWriter errorWriter) {
        this.jwtUtil = jwtUtil;
        this.routePolicyService = routePolicyService;
        this.errorWriter = errorWriter;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        String method = exchange.getRequest().getMethod().name();
        AccessDecision policy = routePolicyService.evaluate(path, method);

        if (policy.publicEndpoint()) {
            return chain.filter(exchange);
        }

        String auth = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (auth == null || !auth.startsWith("Bearer ")) {
            return errorWriter.write(exchange, HttpStatus.UNAUTHORIZED, "AUTHENTICATION_REQUIRED",
                    "A Bearer token is required for this endpoint.");
        }

        String token = auth.substring(7);
        try {
            GatewayPrincipal principal = jwtUtil.parsePrincipal(token);
            if (!policy.allows(principal.roles())) {
                return errorWriter.write(exchange, HttpStatus.FORBIDDEN, "ACCESS_DENIED",
                        "The authenticated user does not have permission for this endpoint.");
            }

            ServerWebExchange authenticatedExchange = exchange.mutate()
                    .request(exchange.getRequest().mutate()
                            .headers(headers -> {
                                headers.remove("X-User-Id");
                                headers.remove("X-User-Email");
                                headers.remove("X-User-Roles");
                            })
                            .header("X-User-Id", principal.userId())
                            .header("X-User-Email", principal.email())
                            .header("X-User-Roles", String.join(",", principal.roles()))
                            .build())
                    .build();
            return chain.filter(authenticatedExchange);
        } catch (Exception e) {
            return errorWriter.write(exchange, HttpStatus.UNAUTHORIZED, "INVALID_TOKEN",
                    "The supplied token is invalid or expired.");
        }
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
