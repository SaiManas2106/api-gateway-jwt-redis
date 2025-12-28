package com.example.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    @GetMapping("/me")
    public ResponseEntity<Map<String,Object>> me(@RequestHeader(value = "X-User-Id", required = false) String userId,
                                                 @RequestHeader(value = "X-User-Roles", required = false) String roles) {
        return ResponseEntity.ok(Map.of("id", userId == null ? "anonymous" : userId, "roles", roles == null ? "" : roles));
    }
}
