package com.example.product.api;

import com.example.product.model.ProductStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record UpdateProductRequest(
        @NotBlank String name,
        @NotBlank String description,
        @NotBlank String category,
        @NotNull @DecimalMin("0.01") BigDecimal price,
        @NotNull ProductStatus status
) {
}
