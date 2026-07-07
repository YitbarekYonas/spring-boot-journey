# Week 2, Day 6: Layered Architecture (Controller → Service → Repository)

[![Status](https://img.shields.io/badge/Status-Completed-brightgreen.svg)]()
[![Day](https://img.shields.io/badge/Day-6-blue.svg)]()
[![Topic](https://img.shields.io/badge/Topic-Layered%20Architecture-orange.svg)]()

> **"Each layer has one job. Controller = HTTP. Service = Business. Repository = Data."**

---

## 🎯 Learning Objectives

- ✅ Understand Controller → Service → Repository architecture
- ✅ Keep controllers **thin** (no business logic)
- ✅ Keep services focused on **business logic**
- ✅ Keep repositories focused on **data access**
- ✅ Use **interface-based services** for testability
- ✅ Ensure dependencies flow **downward only**

---

## 💡 What I Learned Today

### 1. The Three Layers

```
┌─────────────────────────────────────────────────────┐
│   CONTROLLER    → HTTP translation only             │
└────────────────────┬────────────────────────────────┘
                     ↓
┌─────────────────────────────────────────────────────┐
│   SERVICE       → Business logic only               │
└────────────────────┬────────────────────────────────┘
                     ↓
┌─────────────────────────────────────────────────────┐
│   REPOSITORY    → Data access only                  │
└─────────────────────────────────────────────────────┘
```

### 2. Controller Must Be Thin
```java
// ✅ CORRECT
@RestController
public class ProductController {
    private final ProductService productService;
    
    @PostMapping
    public ResponseEntity<Product> create(@RequestBody Product product) {
        Product created = productService.createProduct(product);  // Delegate
        return ResponseEntity.created(location).body(created);    // Just respond
    }
}

// ❌ WRONG - Controller has business logic
@PostMapping
public ResponseEntity<Product> create(@RequestBody Product product) {
    if (product.getPrice() < 0) { ... }          // ❌ Business rule
    product.setPrice(product.getPrice() * 1.15); // ❌ Business logic
    return ResponseEntity.ok(productRepository.save(product)); // ❌ Direct repo
}
```

### 3. Service Has Business Logic
```java
@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    
    @Override
    public Product createProduct(Product product) {
        // ✅ Business rules
        if (product.getPrice() < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        // ✅ Business logic
        double taxedPrice = product.getPrice() * 1.15;
        product.setPrice(taxedPrice);
        // ✅ Delegate to repository
        return productRepository.save(product);
    }
}
```

### 4. Repository Only Handles Data
```java
@Repository
public class ProductRepository {
    // ✅ Data access only - NO business logic
    public List<Product> findAll() { ... }
    public Optional<Product> findById(Long id) { ... }
    public Product save(Product product) { ... }
}
```

### 5. Interface-Based Services
```java
// Interface - the contract
public interface ProductService {
    List<Product> getAllProducts();
    Product createProduct(Product product);
}

// Implementation - the business logic
@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    // ... implementation
}

// Controller depends on INTERFACE
@RestController
public class ProductController {
    private final ProductService productService;  // Interface type
}
```

### 6. Dependency Direction Rule
```
✅ CORRECT: Controller → Service → Repository
❌ WRONG:   Repository → Service (never)
❌ WRONG:   Service → Controller (never)
```

---

## 🔑 Key Concepts

| Concept | My Understanding |
|---------|------------------|
| **Controller** | HTTP translation only |
| **Service** | Business logic, rules, workflows |
| **Repository** | Data access only |
| **Interface** | Contract between layers, enables testing |
| **Dependency Direction** | Downward only: Controller → Service → Repository |
| **Thin Controller** | No business logic, just delegate |

---

## ❌ Common Mistakes

### Mistake 1: Business Logic in Controller
```java
// ❌ WRONG
@PostMapping
public ResponseEntity<Product> create(@RequestBody Product product) {
    if (product.getPrice() < 0) { ... }  // ❌ Business rule in controller!
    return ResponseEntity.ok(productRepository.save(product)); // ❌ Skipped service!
}
```

### Mistake 2: Controller Talking Directly to Repository
```java
// ❌ WRONG
@RestController
public class ProductController {
    @Autowired
    private ProductRepository productRepository;  // ❌ Controller shouldn't know repository!
}
```

### Mistake 3: HTTP Types in Service
```java
// ❌ WRONG
@Service
public class ProductService {
    public ResponseEntity<Product> createProduct(Product product) {  // ❌ ResponseEntity in service!
        return ResponseEntity.ok(product);  // ❌ HTTP in service!
    }
}
```

---

## ✅ Checklist

### Concepts
- [x] Controller = HTTP only (thin)
- [x] Service = Business logic only
- [x] Repository = Data access only
- [x] Interface-based services
- [x] Dependency direction: Controller → Service → Repository

### Code
- [x] Created interface: `ProductService`
- [x] Created implementation: `ProductServiceImpl`
- [x] Controller depends on interface
- [x] Controller has NO business logic
- [x] Service has ALL business logic
- [x] Repository has NO business logic

---
