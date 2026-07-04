# Week 2, Day 2: Path Variables & Query Parameters

[![Status](https://img.shields.io/badge/Status-Completed-brightgreen.svg)]()
[![Day](https://img.shields.io/badge/Day-2-blue.svg)]()
[![Topic](https://img.shields.io/badge/Topic-Path%20Variables%20%26%20Query%20Params-orange.svg)]()

> **"Path variables = which resource? Query params = how to return it?"**

---

## 🎯 Learning Objectives

- ✅ Use `@PathVariable` to extract values from URL path
- ✅ Use `@RequestParam` for query parameters
- ✅ Understand required vs optional parameters with defaults
- ✅ Combine path variables and query parameters
- ✅ Implement filtering logic in service layer
- ✅ Understand type conversion and error cases

---

## 💡 What I Learned

### 1. Path Variables (`@PathVariable`)
- Identifies a **specific resource**
- Part of the URL structure: `/products/{id}`
- Spring auto-converts types (String → Long, String → Enum)

```java
@GetMapping("/{id}")
public Product getById(@PathVariable Long id) {
    return productService.getProductById(id);
}
```

### 2. Query Parameters (`@RequestParam`)
- **Filters or configures** a collection
- After `?` in URL: `/products?category=Electronics`
- `required = false` → optional parameter
- `defaultValue = "x"` → default when absent

```java
@GetMapping
public List<Product> getAllProducts(
        @RequestParam(required = false) String category,
        @RequestParam(defaultValue = "0.0") double minPrice,
        @RequestParam(defaultValue = "999999.0") double maxPrice) {
    return productService.getFilteredProducts(category, minPrice, maxPrice);
}
```

### 3. Decision Rule

| Use Case | What to Use | Example |
|----------|-------------|---------|
| "Which resource?" | Path Variable | `/products/42` |
| "How to return it?" | Query Param | `/products?category=Electronics` |

### 4. URL Design Examples
```
✅ GET /api/products              → all products
✅ GET /api/products?category=X   → filtered
✅ GET /api/products/42           → specific product
✅ GET /api/products/42/reviews?minRating=4 → nested resource + filter
❌ GET /api/getProducts            → verb in URL
❌ GET /api/products/find/42      → "find" is a verb
```

### 5. Error Cases Observed

| Error | Status | Cause |
|-------|--------|-------|
| Non-existent ID | 500 | Product not found (will fix with ResponseEntity) |
| Wrong type for ID | 400 | Type conversion fails |
| Empty filter | 200 | Returns all products |

---

## 📊 Key Takeaways

| Concept | My Understanding |
|---------|------------------|
| **@PathVariable** | Extracts from URL path, identifies specific resource |
| **@RequestParam** | Extracts from query string, filters/configure response |
| **required = false** | Makes parameter optional |
| **defaultValue** | Provides default when parameter absent |
| **Filtering Logic** | Belongs in **service layer**, not controller |

---

## ✅ Checklist

- [x] Added `GET /api/products/{id}` with `@PathVariable`
- [x] Added `GET /api/products` with `@RequestParam` filters
- [x] Implemented filtering logic in service layer
- [x] Tested all combinations in Postman/curl
- [x] Tested edge cases: wrong type, non-existent, no params

---

