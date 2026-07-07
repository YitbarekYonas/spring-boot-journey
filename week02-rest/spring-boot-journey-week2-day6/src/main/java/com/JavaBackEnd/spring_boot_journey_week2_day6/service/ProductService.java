package com.JavaBackEnd.spring_boot_journey_week2_day6.service;

import com.JavaBackEnd.spring_boot_journey_week2_day6.entity.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {

    List<Product> getAllProducts();

    Optional<Product> getProductById(Long id);

    List<Product> getFilteredProducts(String category, double minPrice, double maxPrice);

    Product createProduct(Product product);

    Optional<Product> updateProduct(Long id, Product product);

    boolean deleteProduct(Long id);
}