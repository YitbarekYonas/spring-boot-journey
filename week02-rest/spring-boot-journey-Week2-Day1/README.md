```
# Week 2, Day 1: REST Fundamentals & First Controller

[![Status](https://img.shields.io/badge/Status-Completed-brightgreen.svg)]()
[![Day](https://img.shields.io/badge/Day-1-blue.svg)]()
[![Topic](https://img.shields.io/badge/Topic-REST%20%26%20First%20Controller-orange.svg)]()

> **"URLs are nouns. HTTP methods are verbs."**

---

## 🎯 Learning Objectives

- ✅ Understand REST architecture and resource-based URLs
- ✅ Know when to use `@RestController` vs `@Controller`
- ✅ Build first REST controller with GET endpoint
- ✅ Understand the request lifecycle (DispatcherServlet → HandlerMapping → Controller)
- ✅ See Jackson auto-serialization in action
- ✅ Recognize ambiguous mapping errors

---

## 💡 What I Learned

### 1. REST Core Rules
- **Resources** = nouns (e.g., `/products`)
- **HTTP Methods** = verbs (GET, POST, PUT, DELETE)
- **Never put verbs in URLs**:
  - ❌ `/getProducts`
  - ✅ `/products`

### 2. HTTP Methods Mapping
| Method | CRUD | Idempotent |
|--------|------|------------|
| GET | Read | ✅ Yes |
| POST | Create | ❌ No |
| PUT | Full Update | ✅ Yes |
| PATCH | Partial Update | ❌ No |
| DELETE | Delete | ✅ Yes |

### 3. @RestController vs @Controller
- `@Controller` → Returns **view names** (HTML pages)
- `@RestController` → Returns **data** (JSON via Jackson)
- `@RestController = @Controller + @ResponseBody`

### 4. Request Lifecycle
```
Request → DispatcherServlet → HandlerMapping → Controller → 
@ResponseBody → Jackson → JSON Response
```

### 5. Jackson Serialization
- Converts Java objects → JSON automatically
- Controlled with: `@JsonProperty`, `@JsonIgnore`

### 6. Ambiguous Mapping Error
```java
❌ Duplicate mapping on same path + method
@GetMapping  // First
public List<Product> getAll() { }

@GetMapping  // Second → ERROR!
public String getMessage() { }
```

**Error:** `IllegalStateException: Ambiguous mapping`

---

## 📊 Key Takeaways

| Concept | My Understanding |
|---------|------------------|
| **REST** | Resources (nouns) via URLs, actions via HTTP methods |
| **@RestController** | Returns JSON data, not HTML views |
| **DispatcherServlet** | Single entry point for all requests |
| **Jackson** | Auto-serializes Java objects to JSON |
| **Ambiguous Mapping** | Can't have two methods with same HTTP method + path |

---

## ✅ Checklist

- [x] Created Product entity
- [x] Created in-memory repository (CopyOnWriteArrayList)
- [x] Created ProductService
- [x] Created ProductController with GET /api/products
- [x] Tested in browser and Postman
- [x] Verified JSON response
- [x] Deliberately created ambiguous mapping
- [x] Read the startup exception

---


