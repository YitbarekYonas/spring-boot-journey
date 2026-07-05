# Week 2, Day 3: Request Body & Response Handling

[![Status](https://img.shields.io/badge/Status-Completed-brightgreen.svg)]()
[![Day](https://img.shields.io/badge/Day-3-blue.svg)]()
[![Topic](https://img.shields.io/badge/Topic-Request%20Body%20%26%20Response-orange.svg)]()

> **"Full control over status codes, headers, and body."**

---

## 🎯 Learning Objectives

- ✅ Use `@RequestBody` to read JSON → Java object
- ✅ Use `ResponseEntity<T>` for full response control
- ✅ Return `201 Created` with `Location` header for POST
- ✅ Return `200 OK` for successful GET/PUT
- ✅ Return `204 No Content` for DELETE
- ✅ Return `404 Not Found` for non-existent resources
- ✅ Understand Jackson serialization/deserialization
- ✅ Test all endpoints with proper status codes

---

## 💡 What I Learned

### 1. @RequestBody - JSON → Java Object
```java
@PostMapping
public ResponseEntity<Product> create(@RequestBody Product product) {
    // Jackson deserializes JSON into Product object automatically
}
```

### 2. ResponseEntity - Full Control
| Method | Status | Use Case |
|--------|--------|----------|
| `ResponseEntity.ok(body)` | 200 OK | GET/PUT success with body |
| `ResponseEntity.created(uri)` | 201 Created | POST success + Location |
| `ResponseEntity.noContent()` | 204 No Content | DELETE success |
| `ResponseEntity.notFound()` | 404 Not Found | Resource doesn't exist |

### 3. HTTP Status Codes I Must Use
| Code | Meaning | When to Use |
|------|---------|-------------|
| 200 OK | Success with body | GET, PUT, PATCH |
| 201 Created | Resource created | POST + Location header |
| 204 No Content | Success, no body | DELETE |
| 400 Bad Request | Client error | Malformed JSON, validation fail |
| 404 Not Found | Resource doesn't exist | GET/PUT/DELETE on invalid ID |
| 500 Internal Server Error | Our bug | Unhandled exception (never intentional) |

### 4. Important Design Rule
```java
// ✅ CORRECT - Empty collection = 200 OK with []
@GetMapping
public ResponseEntity<List<Product>> getAll() {
    return ResponseEntity.ok(productService.getAll());
}

// ❌ WRONG - Empty collection is NOT 404
if (products.isEmpty()) {
    return ResponseEntity.notFound().build(); // WRONG!
}
```

**Empty collection = resource exists but has no items → 200 OK with []**

---

## 📝 Complete Controller

```java
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
        return ResponseEntity.ok(productService.getAllProducts());
    }

    // GET /api/products/{id} - Get product by ID
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/products - Create new product
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

    // PUT /api/products/{id} - Full update
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id,
            @RequestBody Product updatedProduct) {

        return productService.updateProduct(id, updatedProduct)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/products/{id} - Delete product
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        boolean deleted = productService.deleteProduct(id);
        return deleted
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
```

---

## ✅ Checklist

- [x] POST → 201 Created + Location header
- [x] GET by ID → 200 OK or 404 Not Found
- [x] PUT → 200 OK or 404 Not Found
- [x] DELETE → 204 No Content or 404 Not Found
- [x] GET all → 200 OK with [] (never 404)
- [x] Malformed JSON → 400 Bad Request (automatic)
- [x] Tested all cases in Postman/IntelliJ HTTP Client

---

## 🔥 Deliberate Break: What Not to Do

```java
// ❌ WRONG - Return 500 for client error
@GetMapping("/{id}")
public ResponseEntity<Product> getById(@PathVariable Long id) {
    try {
        return ResponseEntity.ok(productService.getById(id));
    } catch (ProductNotFoundException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // ❌
    }
}
```

**Correct:**
```java
// ✅ CORRECT - 404 for resource not found
return productService.getById(id)
    .map(ResponseEntity::ok)
    .orElse(ResponseEntity.notFound().build());
```

---

## 🎯 Key Takeaways

| Concept | My Understanding |
|---------|------------------|
| `@RequestBody` | Reads JSON from request body → converts to Java object |
| `ResponseEntity` | Wraps response with status code + headers + body |
| `201 Created` | POST success + Location header |
| `204 No Content` | DELETE success |
| `404 Not Found` | Resource doesn't exist |
| Empty collection | 200 OK with [] — NOT 404! |

---
