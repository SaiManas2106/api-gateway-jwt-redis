package com.example.gateway.controller;

import com.example.gateway.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtUtil jwtUtil;

    public AuthController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/token")
    public ResponseEntity<Map<String,String>> token(@RequestBody Map<String,Object> body) {
        String userId = (String) body.getOrDefault("userId", "user1");
        List<String> roles = (List<String>) body.getOrDefault("roles", List.of("USER"));
        String token = jwtUtil.generateToken(userId, roles);
        return ResponseEntity.ok(Map.of("token", token));
    }
}
