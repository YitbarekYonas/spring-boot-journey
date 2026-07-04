package com.JavaBackEnd.spring_boot_journey_week2_day2.controller;

import com.JavaBackEnd.spring_boot_journey_week2_day2.entity.Product;
import com.JavaBackEnd.spring_boot_journey_week2_day2.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // GET /api/products - Get all products (from Day 1)
    // GET /api/products?category=Electronics - Filter by category
    // GET /api/products?minPrice=100&maxPrice=500 - Filter by price range
    // GET /api/products?category=Electronics&minPrice=100&maxPrice=500 - Combined filters
    @GetMapping
    public List<Product> getAllProducts(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0.0") double minPrice,
            @RequestParam(defaultValue = "999999.0") double maxPrice) {

        return productService.getFilteredProducts(category, minPrice, maxPrice);
    }

    // GET /api/products/42 - Get product by ID (NEW from Day 2)
    @GetMapping("/{id}")
    public Product getById(@PathVariable Long id) {
        return productService.getProductById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }
}