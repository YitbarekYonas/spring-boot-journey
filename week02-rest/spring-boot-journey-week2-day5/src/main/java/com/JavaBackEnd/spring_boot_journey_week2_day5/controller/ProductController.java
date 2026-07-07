package com.JavaBackEnd.spring_boot_journey_week2_day5.controller;

import com.JavaBackEnd.spring_boot_journey_week2_day5.entity.Product;
import com.JavaBackEnd.spring_boot_journey_week2_day5.service.ProductService;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private static final String SUPPORTED_VERSION = "1";

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * GET /api/products
     * Headers: X-API-Version, X-Request-Id
     * Query Params: category, minPrice, maxPrice
     */
    @GetMapping
    public ResponseEntity<?> getAllProducts(
            @RequestHeader(value = "X-API-Version", defaultValue = "1") String apiVersion,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0.0") double minPrice,
            @RequestParam(defaultValue = "999999.0") double maxPrice,
            @RequestHeader(value = "X-Request-Id", required = false) String requestId) {

        // Version check
        if (!SUPPORTED_VERSION.equals(apiVersion)) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Unsupported API version: " + apiVersion
                          + ". Supported version: " + SUPPORTED_VERSION);
        }

        List<Product> products = productService.getFilteredProducts(category, minPrice, maxPrice);

        return ResponseEntity.ok()
                .header("X-API-Version", SUPPORTED_VERSION)
                .header("X-Total-Count", String.valueOf(products.size()))
                .header("X-Request-Id", requestId != null ? requestId : UUID.randomUUID().toString())
                .body(products);
    }

    /**
     * GET /api/products/{id}
     * Headers: X-Request-Id
     */
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(
            @PathVariable Long id,
            @RequestHeader(value = "X-Request-Id", required = false) String requestId) {

        return productService.getProductById(id)
                .map(product -> ResponseEntity.ok()
                        .header("X-Request-Id",
                                requestId != null ? requestId : UUID.randomUUID().toString())
                        .body(product))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/products
     * Headers: X-API-Version, X-Request-Id
     */
    @PostMapping
    public ResponseEntity<Product> createProduct(
            @RequestBody Product product,
            @RequestHeader(value = "X-API-Version", defaultValue = "1") String apiVersion,
            @RequestHeader(value = "X-Request-Id", required = false) String requestId,
            UriComponentsBuilder uriBuilder) {

        // Version check
        if (!SUPPORTED_VERSION.equals(apiVersion)) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        Product created = productService.createProduct(product);

        URI location = uriBuilder
                .path("/api/products/{id}")
                .buildAndExpand(created.getId())
                .toUri();

        return ResponseEntity.created(location)
                .header("X-Created-Id", String.valueOf(created.getId()))
                .header("X-Request-Id", requestId != null ? requestId : UUID.randomUUID().toString())
                .body(created);
    }

    /**
     * PUT /api/products/{id}
     * Headers: X-Request-Id
     */
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id,
            @RequestBody Product updatedProduct,
            @RequestHeader(value = "X-Request-Id", required = false) String requestId) {

        return productService.updateProduct(id, updatedProduct)
                .map(product -> ResponseEntity.ok()
                        .header("X-Request-Id",
                                requestId != null ? requestId : UUID.randomUUID().toString())
                        .body(product))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * DELETE /api/products/{id}
     * Headers: X-Request-Id
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable Long id,
            @RequestHeader(value = "X-Request-Id", required = false) String requestId) {

        boolean deleted = productService.deleteProduct(id);
        if (deleted) {
            return ResponseEntity.noContent()
                    .header("X-Request-Id",
                            requestId != null ? requestId : UUID.randomUUID().toString())
                    .build();
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Debug endpoint - shows all headers
     */
    @GetMapping("/debug/headers")
    public ResponseEntity<Map<String, String>> debugHeaders(
            @RequestHeader HttpHeaders headers) {

        Map<String, String> headerMap = new HashMap<>();
        headers.forEach((key, values) ->
                headerMap.put(key, String.join(", ", values)));

        return ResponseEntity.ok(headerMap);
    }
}