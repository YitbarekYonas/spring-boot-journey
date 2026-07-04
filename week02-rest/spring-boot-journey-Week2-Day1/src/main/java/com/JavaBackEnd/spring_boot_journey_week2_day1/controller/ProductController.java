package com.JavaBackEnd.spring_boot_journey_week2_day1.controller;

import com.JavaBackEnd.spring_boot_journey_week2_day1.entity.Product;
import com.JavaBackEnd.spring_boot_journey_week2_day1.service.ProductService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // GET /api/products → returns all products
    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }
}