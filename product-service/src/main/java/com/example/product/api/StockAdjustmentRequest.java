package com.example.product.api;

import jakarta.validation.constraints.NotNull;

public record StockAdjustmentRequest(@NotNull Integer delta) {
}
