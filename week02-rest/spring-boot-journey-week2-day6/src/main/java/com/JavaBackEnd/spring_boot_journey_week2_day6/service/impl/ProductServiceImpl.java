package com.JavaBackEnd.spring_boot_journey_week2_day6.service.impl;

import com.JavaBackEnd.spring_boot_journey_week2_day6.entity.Product;
import com.JavaBackEnd.spring_boot_journey_week2_day6.repository.ProductRepository;
import com.JavaBackEnd.spring_boot_journey_week2_day6.service.ProductService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    public List<Product> getFilteredProducts(String category, double minPrice, double maxPrice) {
        if (category != null && !category.isBlank()) {
            return productRepository.findByCategoryAndPriceBetween(category, minPrice, maxPrice);
        } else if (minPrice > 0 || maxPrice < 999999.0) {
            return productRepository.findByPriceBetween(minPrice, maxPrice);
        }
        return productRepository.findAll();
    }

    @Override
    public Product createProduct(Product product) {
        // ===== BUSINESS RULE 1: Price must be positive =====
        if (product.getPrice() < 0) {
            throw new IllegalArgumentException(
                "Product price cannot be negative: " + product.getPrice());
        }

        // ===== BUSINESS RULE 2: Name must not be blank =====
        if (product.getName() == null || product.getName().isBlank()) {
            throw new IllegalArgumentException("Product name cannot be blank");
        }

        // ===== BUSINESS RULE 3: Category is required =====
        if (product.getCategory() == null || product.getCategory().isBlank()) {
            throw new IllegalArgumentException("Product category cannot be blank");
        }

        // ===== BUSINESS LOGIC: Apply tax =====
        double taxedPrice = product.getPrice() * 1.15;  // 15% tax
        product.setPrice(taxedPrice);

        // ===== BUSINESS LOGIC: Apply bulk discount =====
        if (product.getStockQuantity() > 100) {
            product.setPrice(product.getPrice() * 0.90);  // 10% discount for bulk
        }

        // ===== BUSINESS LOGIC: Electronics category has special handling =====
        if ("Electronics".equalsIgnoreCase(product.getCategory())) {
            // Electronics products get additional 5% discount
            product.setPrice(product.getPrice() * 0.95);
        }

        return productRepository.save(product);
    }

    @Override
    public Optional<Product> updateProduct(Long id, Product updatedProduct) {
        return productRepository.findById(id)
                .map(existing -> {
                    // ===== BUSINESS RULE: Electronics cannot change category =====
                    if ("Electronics".equalsIgnoreCase(existing.getCategory())
                            && !existing.getCategory().equalsIgnoreCase(updatedProduct.getCategory())) {
                        throw new IllegalStateException(
                            "Cannot change category of Electronics products");
                    }

                    // ===== BUSINESS RULE: Price update validation =====
                    if (updatedProduct.getPrice() < 0) {
                        throw new IllegalArgumentException(
                            "Product price cannot be negative: " + updatedProduct.getPrice());
                    }

                    // ===== BUSINESS LOGIC: Apply tax and discounts on update =====
                    double taxedPrice = updatedProduct.getPrice() * 1.15;
                    updatedProduct.setPrice(taxedPrice);

                    if (updatedProduct.getStockQuantity() > 100) {
                        updatedProduct.setPrice(updatedProduct.getPrice() * 0.90);
                    }

                    if ("Electronics".equalsIgnoreCase(updatedProduct.getCategory())) {
                        updatedProduct.setPrice(updatedProduct.getPrice() * 0.95);
                    }

                    updatedProduct.setId(id);
                    return productRepository.save(updatedProduct);
                });
    }

    @Override
    public boolean deleteProduct(Long id) {
        return productRepository.deleteById(id);
    }
}