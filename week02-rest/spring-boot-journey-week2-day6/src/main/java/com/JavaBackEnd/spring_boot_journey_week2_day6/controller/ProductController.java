package com.JavaBackEnd.spring_boot_journey_week2_day6.controller;

import com.JavaBackEnd.spring_boot_journey_week2_day6.entity.Product;
import com.JavaBackEnd.spring_boot_journey_week2_day6.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    // Constructor injection - depends on INTERFACE, not implementation
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // ===== GET ALL =====
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0.0") double minPrice,
            @RequestParam(defaultValue = "999999.0") double maxPrice) {

        List<Product> products = productService.getFilteredProducts(category, minPrice, maxPrice);
        return ResponseEntity.ok(products);
    }

    // ===== GET BY ID =====
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ===== CREATE =====
    @PostMapping
    public ResponseEntity<Product> createProduct(
            @RequestBody Product product,
            UriComponentsBuilder uriBuilder) {

        Product created = productService.createProduct(product);
        URI location = uriBuilder
                .path("/api/products/{id}")
                .buildAndExpand(created.getId())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    // ===== UPDATE =====
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id,
            @RequestBody Product product) {

        return productService.updateProduct(id, product)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ===== DELETE =====
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        boolean deleted = productService.deleteProduct(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    // ===== EXCEPTION HANDLING (Preview of Week 6) =====
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalState(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }
}