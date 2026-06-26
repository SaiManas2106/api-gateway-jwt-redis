package com.example.product.repository;

import com.example.product.model.Product;
import com.example.product.model.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsBySkuIgnoreCase(String sku);

    List<Product> findByStatus(ProductStatus status);
}
