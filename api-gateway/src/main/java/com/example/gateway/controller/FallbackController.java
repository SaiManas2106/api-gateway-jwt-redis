package com.example.gateway.controller;

import com.example.gateway.http.GatewayErrorWriter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;

import java.time.Instant;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/product")
    public ResponseEntity<FallbackResponse> product(ServerWebExchange exchange) {
        return ResponseEntity.ok(response("product-service", exchange));
    }

    @GetMapping("/user")
    public ResponseEntity<FallbackResponse> user(ServerWebExchange exchange) {
        return ResponseEntity.ok(response("user-service", exchange));
    }

    private FallbackResponse response(String service, ServerWebExchange exchange) {
        return new FallbackResponse(
                Instant.now(),
                service,
                "Service is temporarily unavailable. Please retry shortly.",
                exchange.getAttributeOrDefault(GatewayErrorWriter.CORRELATION_ID_HEADER, "")
        );
    }

    public record FallbackResponse(Instant timestamp, String service, String message, String correlationId) {
    }
}
