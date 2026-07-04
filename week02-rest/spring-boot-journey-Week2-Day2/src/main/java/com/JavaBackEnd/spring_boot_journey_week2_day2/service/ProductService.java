package com.JavaBackEnd.spring_boot_journey_week2_day2.service;

import com.JavaBackEnd.spring_boot_journey_week2_day2.entity.Product;
import com.JavaBackEnd.spring_boot_journey_week2_day2.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    // NEW: Filtering logic in the service layer
    public List<Product> getFilteredProducts(String category, double minPrice, double maxPrice) {
        return productRepository.findAll().stream()
                .filter(p -> category == null || p.getCategory().equalsIgnoreCase(category))
                .filter(p -> p.getPrice() >= minPrice)
                .filter(p -> p.getPrice() <= maxPrice)
                .collect(Collectors.toList());
    }

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public Optional<Product> updateProduct(Long id, Product updatedProduct) {
        return productRepository.findById(id)
                .map(existing -> {
                    updatedProduct.setId(id);
                    return productRepository.save(updatedProduct);
                });
    }

    public boolean deleteProduct(Long id) {
        return productRepository.deleteById(id);
    }
}