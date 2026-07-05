package com.JavaBackEnd.spring_boot_journey_Week2_Day3.controller;

import com.JavaBackEnd.spring_boot_journey_Week2_Day3.entity.Product;
import com.JavaBackEnd.spring_boot_journey_Week2_Day3.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // GET /api/products - Get all products
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        // ✅ ALWAYS return 200 OK with [] even if empty
        return ResponseEntity.ok(products);
    }

    // GET /api/products/{id} - Get product by ID
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)           // 200 OK with product
                .orElse(ResponseEntity.notFound().build()); // 404 Not Found
    }

    // POST /api/products - Create new product
    @PostMapping
    public ResponseEntity<Product> createProduct(
            @RequestBody Product product,
            UriComponentsBuilder uriBuilder) {

        Product created = productService.createProduct(product);

        // Build Location header: /api/products/{id}
        URI location = uriBuilder
                .path("/api/products/{id}")
                .buildAndExpand(created.getId())
                .toUri();

        // 201 Created + Location header + body
        return ResponseEntity.created(location).body(created);
    }

    // PUT /api/products/{id} - Full update
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id,
            @RequestBody Product updatedProduct) {

        return productService.updateProduct(id, updatedProduct)
                .map(ResponseEntity::ok)           // 200 OK with updated product
                .orElse(ResponseEntity.notFound().build()); // 404 Not Found
    }

    // DELETE /api/products/{id} - Delete product
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        boolean deleted = productService.deleteProduct(id);

        if (deleted) {
            return ResponseEntity.noContent().build(); // 204 No Content
        } else {
            return ResponseEntity.notFound().build();   // 404 Not Found
        }
    }
}