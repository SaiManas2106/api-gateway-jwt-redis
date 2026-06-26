package com.example.product.controller;

import com.example.product.api.CreateProductRequest;
import com.example.product.api.ProductResponse;
import com.example.product.api.StockAdjustmentRequest;
import com.example.product.api.UpdateProductRequest;
import com.example.product.exception.ProductException;
import com.example.product.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> list() {
        return ResponseEntity.ok(productService.listActive());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(productService.get(id));
    }

    @PostMapping("/admin")
    public ResponseEntity<ProductResponse> create(@RequestHeader("X-User-Roles") String roles,
                                                  @Valid @RequestBody CreateProductRequest request) {
        requireAdmin(roles);
        ProductResponse product = productService.create(request);
        return ResponseEntity.created(URI.create("/products/" + product.id())).body(product);
    }

    @PutMapping("/admin/{id}")
    public ResponseEntity<ProductResponse> update(@RequestHeader("X-User-Roles") String roles,
                                                  @PathVariable Long id,
                                                  @Valid @RequestBody UpdateProductRequest request) {
        requireAdmin(roles);
        return ResponseEntity.ok(productService.update(id, request));
    }

    @PatchMapping("/admin/{id}/stock")
    public ResponseEntity<ProductResponse> adjustStock(@RequestHeader("X-User-Roles") String roles,
                                                       @PathVariable Long id,
                                                       @Valid @RequestBody StockAdjustmentRequest request) {
        requireAdmin(roles);
        return ResponseEntity.ok(productService.adjustStock(id, request.delta()));
    }

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<ProductResponse> discontinue(@RequestHeader("X-User-Roles") String roles,
                                                       @PathVariable Long id) {
        requireAdmin(roles);
        return ResponseEntity.ok(productService.discontinue(id));
    }

    private void requireAdmin(String roles) {
        boolean admin = Arrays.stream(roles.split(","))
                .map(String::trim)
                .anyMatch("ADMIN"::equals);
        if (!admin) {
            throw new ProductException(HttpStatus.FORBIDDEN, "ADMIN_REQUIRED", "Admin role is required.");
        }
    }
}
