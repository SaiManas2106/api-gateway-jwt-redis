package com.example.product.service;

import com.example.product.api.CreateProductRequest;
import com.example.product.exception.ProductException;
import com.example.product.model.Product;
import com.example.product.model.ProductStatus;
import com.example.product.repository.ProductRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ProductServiceTest {

    private final ProductRepository productRepository = mock(ProductRepository.class);
    private final ProductService productService = new ProductService(productRepository);

    @Test
    void listsOnlyActiveProducts() {
        Product product = new Product("SKU-1", "Keyboard", "Mechanical keyboard", "Accessories",
                new BigDecimal("89.99"), 20, ProductStatus.ACTIVE);
        when(productRepository.findByStatus(ProductStatus.ACTIVE)).thenReturn(List.of(product));

        assertThat(productService.listActive()).hasSize(1);
    }

    @Test
    void rejectsDuplicateSku() {
        when(productRepository.existsBySkuIgnoreCase("SKU-1")).thenReturn(true);

        assertThatThrownBy(() -> productService.create(new CreateProductRequest(
                "SKU-1", "Keyboard", "Mechanical keyboard", "Accessories",
                new BigDecimal("89.99"), 20, ProductStatus.ACTIVE
        ))).isInstanceOf(ProductException.class);
    }

    @Test
    void preventsNegativeInventory() {
        Product product = new Product("SKU-1", "Keyboard", "Mechanical keyboard", "Accessories",
                new BigDecimal("89.99"), 2, ProductStatus.ACTIVE);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> productService.adjustStock(1L, -3))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void createsDraftWhenStatusIsMissing() {
        when(productRepository.existsBySkuIgnoreCase("SKU-2")).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertThat(productService.create(new CreateProductRequest(
                "SKU-2", "Mouse", "Wireless mouse", "Accessories",
                new BigDecimal("49.99"), 10, null
        )).status()).isEqualTo(ProductStatus.DRAFT);
    }
}
