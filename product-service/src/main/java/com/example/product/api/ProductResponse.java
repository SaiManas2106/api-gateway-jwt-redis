package com.example.product.api;

import com.example.product.model.Product;
import com.example.product.model.ProductStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record ProductResponse(
        Long id,
        String sku,
        String name,
        String description,
        String category,
        BigDecimal price,
        int stockQuantity,
        ProductStatus status,
        Instant createdAt,
        Instant updatedAt
) {
    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getSku(),
                product.getName(),
                product.getDescription(),
                product.getCategory(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getStatus(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }
}
