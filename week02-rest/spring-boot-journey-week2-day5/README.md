# Week 2, Day 5: Headers, Status Codes & Content Negotiation

[![Status](https://img.shields.io/badge/Status-Completed-brightgreen.svg)]()
[![Day](https://img.shields.io/badge/Day-5-blue.svg)]()
[![Topic](https://img.shields.io/badge/Topic-Headers%20%26%20CORS-orange.svg)]()

> **"Headers are metadata about the data. They control how clients and servers interpret each other."**

---

## 🎯 Learning Objectives

- ✅ Read request headers using `@RequestHeader`
- ✅ Write response headers using `ResponseEntity`
- ✅ Implement API versioning via `X-API-Version`
- ✅ Echo `X-Request-Id` back in responses
- ✅ Configure global CORS with `WebMvcConfigurer`
- ✅ Understand preflight `OPTIONS` requests

---

## 💡 What I Learned Today

### 1. HTTP Headers Are Metadata
Headers travel alongside requests/responses and control interpretation:
- **Request Headers** → sent by client (Content-Type, Authorization, X-API-Version)
- **Response Headers** → sent by server (Location, X-Request-Id, Cache-Control)

### 2. `@RequestHeader` Reads Request Headers
```java
@GetMapping
public ResponseEntity<?> getAll(
        @RequestHeader(value = "X-API-Version", defaultValue = "1") String apiVersion,
        @RequestHeader(value = "X-Request-Id", required = false) String requestId) {
    // Use headers
}
```

### 3. `ResponseEntity` Writes Response Headers
```java
return ResponseEntity.ok()
        .header("X-API-Version", "1")
        .header("X-Request-Id", requestId != null ? requestId : UUID.randomUUID().toString())
        .body(products);
```

### 4. API Versioning via Headers
```java
private static final String SUPPORTED_VERSION = "1";

if (!SUPPORTED_VERSION.equals(apiVersion)) {
    return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body("Unsupported API version: " + apiVersion);
}
```

### 5. CORS (Cross-Origin Resource Sharing)
- **Same-Origin Policy**: Browser blocks requests from different origins (protocol + domain + port)
- **Postman bypasses CORS** → it's not a browser
- **Preflight Request**: Browser sends `OPTIONS` first for non-simple requests
- **Solution**: Server responds with `Access-Control-Allow-*` headers

### 6. Global CORS Configuration
```java
@Configuration
public class CorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                    .allowedOrigins("http://localhost:3000")
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                    .allowedHeaders("Content-Type", "Authorization", "X-API-Version")
                    .exposedHeaders("Location", "X-Request-Id")
                    .maxAge(3600);
            }
        };
    }
}
```

---

## 🔑 Key Concepts

| Concept | My Understanding |
|---------|------------------|
| `@RequestHeader` | Extracts request header values |
| `ResponseEntity` | Sets status, headers, and body |
| `HttpStatus.BAD_REQUEST` | 400 - invalid API version |
| `HttpStatus.CREATED` | 201 - resource created |
| `HttpStatus.NO_CONTENT` | 204 - delete successful |
| CORS | Browser security - allows cross-origin requests |
| Preflight | OPTIONS request before non-simple requests |
| `WebMvcConfigurer` | Global CORS configuration |

---

## 💻 Code Examples

### Reading Headers
```java
@GetMapping
public ResponseEntity<?> getAllProducts(
        @RequestHeader(value = "X-API-Version", defaultValue = "1") String apiVersion,
        @RequestHeader(value = "X-Request-Id", required = false) String requestId) {

    if (!"1".equals(apiVersion)) {
        return ResponseEntity.badRequest().body("Unsupported version");
    }

    return ResponseEntity.ok()
            .header("X-Request-Id", requestId != null ? requestId : UUID.randomUUID().toString())
            .body(products);
}
```

### Writing Headers in Response
```java
@PostMapping
public ResponseEntity<Product> createProduct(@RequestBody Product product) {
    Product created = productService.createProduct(product);
    
    return ResponseEntity.created(location)
            .header("X-Created-Id", String.valueOf(created.getId()))
            .header("X-Request-Id", UUID.randomUUID().toString())
            .body(created);
}
```

### Debug All Headers
```java
@GetMapping("/debug")
public ResponseEntity<Map<String, String>> debugHeaders(
        @RequestHeader HttpHeaders headers) {
    Map<String, String> headerMap = new HashMap<>();
    headers.forEach((key, values) ->
            headerMap.put(key, String.join(", ", values)));
    return ResponseEntity.ok(headerMap);
}
```

---

## 🌐 CORS Configuration

### Why CORS?
- **Same-Origin Policy**: Browser restricts cross-origin requests
- **Postman works**: No browser, no CORS restriction
- **Frontend fails**: Browser blocks requests without proper CORS headers

### Preflight Request Flow
```
1. Browser sends OPTIONS request
2. Server responds with Access-Control-Allow-* headers
3. Browser checks if allowed
4. If yes → sends actual request
5. If no → blocks request, shows CORS error
```

### CORS Headers Explained

| Header | Purpose |
|--------|---------|
| `Access-Control-Allow-Origin` | Which origins are allowed |
| `Access-Control-Allow-Methods` | Which HTTP methods are allowed |
| `Access-Control-Allow-Headers` | Which request headers are allowed |
| `Access-Control-Expose-Headers` | Which response headers JS can read |
| `Access-Control-Max-Age` | How long to cache preflight response |

---

## 🚀 Testing in Postman

### Test 1: Valid API Version
```
GET http://localhost:8080/api/products
Headers:
  X-API-Version: 1
  X-Request-Id: test-12345

✅ Expected: 200 OK
Response Headers:
  X-API-Version: 1
  X-Request-Id: test-12345
  X-Total-Count: 4
```

### Test 2: Invalid API Version (400)
```
GET http://localhost:8080/api/products
Headers:
  X-API-Version: 99

❌ Expected: 400 Bad Request
Body: "Unsupported API version: 99. Supported version: 1"
```

### Test 3: No Version Header (Default)
```
GET http://localhost:8080/api/products

✅ Expected: 200 OK (defaults to version 1)
```

### Test 4: CORS Preflight (OPTIONS)
```
OPTIONS http://localhost:8080/api/products
Headers:
  Origin: http://localhost:3000
  Access-Control-Request-Method: GET
  Access-Control-Request-Headers: X-API-Version

✅ Expected: 200 OK
Response Headers:
  Access-Control-Allow-Origin: http://localhost:3000
  Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS
  Access-Control-Allow-Headers: Content-Type, Authorization, X-API-Version
  Access-Control-Max-Age: 3600
```

---

## ❌ Common Mistakes

### Mistake 1: Forgetting `@RequestHeader` is Required by Default
```java
// ❌ Missing header → 400 Bad Request
@GetMapping
public ResponseEntity<?> getAll(
        @RequestHeader("X-API-Version") String apiVersion) { }

// ✅ Fix: Make it optional with defaultValue
@GetMapping
public ResponseEntity<?> getAll(
        @RequestHeader(value = "X-API-Version", defaultValue = "1") String apiVersion) { }
```

### Mistake 2: Using `allowedOrigins("*")` in Production
```java
// ❌ Allows ANY website to call your API
.allowedOrigins("*")

// ✅ List specific origins
.allowedOrigins("https://myapp.com", "https://admin.myapp.com")
```

### Mistake 3: Forgetting `OPTIONS` in allowedMethods
```java
// ❌ CORS preflight fails
.allowedMethods("GET", "POST")

// ✅ Include OPTIONS for preflight
.allowedMethods("GET", "POST", "OPTIONS")
```

### Mistake 4: Not Exposing Headers to Frontend
```java
// ❌ Frontend can't read custom headers
registry.addMapping("/api/**")
    .allowedOrigins("http://localhost:3000");

// ✅ Expose headers for frontend to read
registry.addMapping("/api/**")
    .allowedOrigins("http://localhost:3000")
    .exposedHeaders("Location", "X-Request-Id", "X-Total-Count");
```

---

## ✅ Day 5 Checklist

### Concepts
- [x] Understand HTTP headers (request vs response)
- [x] Use `@RequestHeader` to read request headers
- [x] Use `ResponseEntity` to write response headers
- [x] Implement API versioning via `X-API-Version`
- [x] Echo `X-Request-Id` back in responses
- [x] Understand CORS and Same-Origin Policy
- [x] Understand preflight `OPTIONS` requests
- [x] Configure global CORS with `WebMvcConfigurer`

### Code
- [x] `CorsConfig.java` with global CORS settings
- [x] `ProductController` with `@RequestHeader` and `ResponseEntity`
- [x] API version check with `400 Bad Request`
- [x] `X-Request-Id` echo in response headers
- [x] `X-Total-Count` header in GET all
- [x] `X-Created-Id` header in POST
- [x] Debug endpoint for inspecting all headers

### Testing
- [x] Test valid API version → 200 OK
- [x] Test invalid API version → 400 Bad Request
- [x] Test no version header → default works
- [x] Test `OPTIONS` preflight request
- [x] Test CORS headers in response
- [x] Test `X-Request-Id` echo

---

## 📊 Quick Reference

| Annotation | Purpose |
|------------|---------|
| `@RequestHeader` | Extract request header |
| `ResponseEntity` | Set status, headers, body |
| `HttpStatus.BAD_REQUEST` | 400 |
| `HttpStatus.CREATED` | 201 |
| `HttpStatus.NO_CONTENT` | 204 |
| `@Configuration` | Bean definition class |
| `WebMvcConfigurer` | Configure Spring MVC globally |

---
