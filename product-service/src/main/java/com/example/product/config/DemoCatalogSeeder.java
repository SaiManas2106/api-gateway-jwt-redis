package com.example.product.config;

import com.example.product.model.Product;
import com.example.product.model.ProductStatus;
import com.example.product.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DemoCatalogSeeder implements CommandLineRunner {

    private final ProductRepository productRepository;

    public DemoCatalogSeeder(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) {
        seed("SKU-HEADPHONES-001", "Wireless Headphones", "Noise-cancelling over-ear headphones.", "Audio", "149.99", 40);
        seed("SKU-KEYBOARD-002", "Mechanical Keyboard", "Compact keyboard with hot-swappable switches.", "Accessories", "89.99", 25);
        seed("SKU-BACKPACK-003", "Travel Backpack", "Carry-on backpack with laptop sleeve.", "Travel", "119.00", 18);
    }

    private void seed(String sku, String name, String description, String category, String price, int stock) {
        if (!productRepository.existsBySkuIgnoreCase(sku)) {
            productRepository.save(new Product(
                    sku,
                    name,
                    description,
                    category,
                    new BigDecimal(price),
                    stock,
                    ProductStatus.ACTIVE
            ));
        }
    }
}
