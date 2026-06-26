package com.example.product.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sku;
    private String name;
    private String description;
    private String category;
    private BigDecimal price;
    private int stockQuantity;

    @Enumerated(EnumType.STRING)
    private ProductStatus status;

    private Instant createdAt;
    private Instant updatedAt;

    protected Product() {
    }

    public Product(String sku, String name, String description, String category,
                   BigDecimal price, int stockQuantity, ProductStatus status) {
        this.sku = sku;
        this.name = name;
        this.description = description;
        this.category = category;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.status = status;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public void update(String name, String description, String category, BigDecimal price, ProductStatus status) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.price = price;
        this.status = status;
        this.updatedAt = Instant.now();
    }

    public void adjustStock(int delta) {
        int nextStock = this.stockQuantity + delta;
        if (nextStock < 0) {
            throw new IllegalArgumentException("Stock cannot become negative.");
        }
        this.stockQuantity = nextStock;
        this.updatedAt = Instant.now();
    }

    public void discontinue() {
        this.status = ProductStatus.DISCONTINUED;
        this.updatedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getSku() {
        return sku;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public ProductStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
