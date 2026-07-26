# Week 5, Day 5: JWT Filter & Integration

[![Status](https://img.shields.io/badge/Status-Completed-brightgreen.svg)]()
[![Day](https://img.shields.io/badge/Day-5-blue.svg)]()
[![Topic](https://img.shields.io/badge/Topic-JWT%20Filter-orange.svg)]()

> **"JWT enables stateless authentication - one token per request, no database lookup."**

---

## 🎯 Learning Objectives

- ✅ Build JwtAuthenticationFilter with OncePerRequestFilter
- ✅ Register filter in SecurityConfig
- ✅ Update login endpoint to return JWT
- ✅ Add JwtAuthenticationEntryPoint (401 handler)
- ✅ Add JwtAccessDeniedHandler (403 handler)
- ✅ Test complete JWT authentication flow
- ✅ Understand filter chain positioning

---

## 💡 What I Learned Today

### 1. JWT Authentication Flow

```
1. Login → Server verifies credentials → Returns JWT token
2. Client stores token
3. Every request: Authorization: Bearer <token>
4. Server validates token signature → No DB call
5. SecurityContextHolder populated
6. Request reaches controller with authentication
```

### 2. JwtAuthenticationFilter Steps

```
1. Extract token from Authorization header
2. Skip if no token present
3. Extract email from token (validates signature)
4. Load UserDetails from DB (verify user still exists)
5. Validate token against UserDetails
6. Create UsernamePasswordAuthenticationToken
7. Set in SecurityContextHolder
8. Continue filter chain
```

### 3. Filter Chain Position

```java
.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
```

**Why:** JWT filter must run BEFORE Spring's default authentication filter.

### 4. Error Handlers

| Handler | Status | When Called |
|---------|--------|-------------|
| JwtAuthenticationEntryPoint | 401 | No authentication provided |
| JwtAccessDeniedHandler | 403 | Authenticated but wrong role |

---

## 💻 Code Examples

### JwtAuthenticationFilter

```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) {
        String token = extractTokenFromRequest(request);
        
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }
        
        String email = jwtService.extractEmail(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        
        if (jwtService.isTokenValid(token, userDetails)) {
            UsernamePasswordAuthenticationToken authToken = 
                new UsernamePasswordAuthenticationToken(userDetails, null, 
                    userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }
        
        filterChain.doFilter(request, response);
    }
    
    private String extractTokenFromRequest(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
```

### SecurityConfig with JWT

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/books/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/books").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(authEntryPoint)
                .accessDeniedHandler(accessDeniedHandler)
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .csrf(csrf -> csrf.disable());
        return http.build();
    }
}
```

### Login Returns JWT

```java
@PostMapping("/login")
public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
    Authentication auth = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
    );
    
    CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
    String token = jwtService.generateToken(userDetails);
    
    return ResponseEntity.ok(new LoginResponse(token, userDetails.getUsername(), 
        userDetails.getUser().getName(), userDetails.getUser().getRole(), 7200000L));
}
```

---

## 📋 Postman Testing

### Environment Variables

| Variable | Value |
|----------|-------|
| `baseUrl` | `http://localhost:8080` |
| `apiVersion` | `/api` |
| `jwtToken` | Set from login response |

### Test Users

| Username | Password | Role |
|----------|----------|------|
| `admin@library.com` | `admin123` | ADMIN |
| `jane@library.com` | `lib123` | LIBRARIAN |
| `john@library.com` | `mem123` | MEMBER |

### Endpoints

| Method | URL | Auth | Status |
|--------|-----|------|--------|
| POST | `/auth/login` | None | 200 (token) |
| GET | `/auth/me` | Bearer | 200 |
| GET | `/auth/me` | None | 401 |
| GET | `/loans/stats/books` | Bearer (MEMBER) | 403 |
| GET | `/books` | None | 200 |
| POST | `/books` | Bearer (ADMIN) | 201 |
| POST | `/books` | Bearer (MEMBER) | 403 |

---

## 📊 401 vs 403 Responses

### 401 Unauthorized (No Token)
```json
{
    "timestamp": "2026-07-26T22:30:00",
    "status": 401,
    "error": "Unauthorized",
    "message": "Authentication required. Please provide a valid JWT token.",
    "path": "/api/auth/me"
}
```

### 403 Forbidden (Wrong Role)
```json
{
    "timestamp": "2026-07-26T22:30:00",
    "status": 403,
    "error": "Forbidden",
    "message": "You don't have permission to access this resource.",
    "path": "/api/loans/stats/books"
}
```

---

## ✅ Day 5 Checklist

### Code
- [x] JwtAuthenticationFilter extends OncePerRequestFilter
- [x] Filter registered with addFilterBefore
- [x] Login endpoint returns JWT
- [x] JwtAuthenticationEntryPoint (401)
- [x] JwtAccessDeniedHandler (403)
- [x] SecurityConfig updated

### Testing
- [x] Login → get token
- [x] Valid token → 200
- [x] No token → 401
- [x] Wrong role → 403
- [x] Wrong password → 401
- [x] Invalid token → 401
- [x] Public endpoint → 200 (no token)

---
