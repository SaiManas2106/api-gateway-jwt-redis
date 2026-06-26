package com.example.gateway.filter;

import com.example.gateway.http.GatewayErrorWriter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class CorrelationIdFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String correlationId = exchange.getRequest().getHeaders()
                .getFirst(GatewayErrorWriter.CORRELATION_ID_HEADER);
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
        }

        ServerHttpRequest request = exchange.getRequest().mutate()
                .header(GatewayErrorWriter.CORRELATION_ID_HEADER, correlationId)
                .build();
        exchange.getAttributes().put(GatewayErrorWriter.CORRELATION_ID_HEADER, correlationId);
        ServerWebExchange mutated = exchange.mutate()
                .request(request)
                .build();

        mutated.getResponse().getHeaders().set(GatewayErrorWriter.CORRELATION_ID_HEADER, correlationId);
        return chain.filter(mutated);
    }

    @Override
    public int getOrder() {
        return -10;
    }
}
