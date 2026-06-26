package com.example.product.service;

import com.example.product.api.CreateProductRequest;
import com.example.product.api.ProductResponse;
import com.example.product.api.UpdateProductRequest;
import com.example.product.exception.ProductException;
import com.example.product.model.Product;
import com.example.product.model.ProductStatus;
import com.example.product.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<ProductResponse> listActive() {
        return productRepository.findByStatus(ProductStatus.ACTIVE).stream()
                .map(ProductResponse::from)
                .toList();
    }

    public ProductResponse get(Long id) {
        return ProductResponse.from(find(id));
    }

    @Transactional
    public ProductResponse create(CreateProductRequest request) {
        if (productRepository.existsBySkuIgnoreCase(request.sku())) {
            throw new ProductException(HttpStatus.CONFLICT, "SKU_ALREADY_EXISTS", "A product with this SKU already exists.");
        }
        Product product = new Product(
                request.sku(),
                request.name(),
                request.description(),
                request.category(),
                request.price(),
                request.stockQuantity(),
                request.status() == null ? ProductStatus.DRAFT : request.status()
        );
        return ProductResponse.from(productRepository.save(product));
    }

    @Transactional
    public ProductResponse update(Long id, UpdateProductRequest request) {
        Product product = find(id);
        product.update(request.name(), request.description(), request.category(), request.price(), request.status());
        return ProductResponse.from(product);
    }

    @Transactional
    public ProductResponse adjustStock(Long id, int delta) {
        Product product = find(id);
        product.adjustStock(delta);
        return ProductResponse.from(product);
    }

    @Transactional
    public ProductResponse discontinue(Long id) {
        Product product = find(id);
        product.discontinue();
        return ProductResponse.from(product);
    }

    private Product find(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductException(HttpStatus.NOT_FOUND, "PRODUCT_NOT_FOUND", "Product was not found."));
    }
}
