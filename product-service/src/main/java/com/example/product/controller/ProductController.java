package com.example.product.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/products")
public class ProductController {

    @GetMapping
    public ResponseEntity<List<Map<String,Object>>> list(@RequestHeader(value = "X-User-Id", required = false) String userId) {
        return ResponseEntity.ok(List.of(
                Map.of("id", 1, "name", "Widget", "owner", userId == null ? "anonymous" : userId),
                Map.of("id", 2, "name", "Gadget", "owner", userId == null ? "anonymous" : userId)
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String,Object>> get(@PathVariable int id) {
        return ResponseEntity.ok(Map.of("id", id, "name", "Product-"+id));
    }
}
