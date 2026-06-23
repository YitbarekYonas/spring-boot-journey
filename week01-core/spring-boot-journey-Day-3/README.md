# 📄 Day 3 README.md (Full Version with Learning Description)

```markdown
# Day 3: Beans & Bean Annotations

[![Status](https://img.shields.io/badge/Status-Completed-brightgreen.svg)]()
[![Day](https://img.shields.io/badge/Day-3-blue.svg)]()
[![Topic](https://img.shields.io/badge/Topic-Beans%20%26%20Annotations-orange.svg)]()

> **"Beans are the heart of Spring. Understanding them is understanding Spring itself."**

---

## 📚 Table of Contents

- [Overview](#-overview)
- [Learning Objectives](#-learning-objectives)
- [What I Learned Today](#-what-i-learned-today)
- [What is a Bean?](#-what-is-a-bean)
- [The Four Stereotype Annotations](#-the-four-stereotype-annotations)
- [@Component vs @Service vs @Repository vs @Controller](#-component-vs-service-vs-repository-vs-controller)
- [@Configuration and @Bean](#-configuration-and-bean)
- [@Bean vs @Component](#-bean-vs-component)
- [Bean Scopes](#-bean-scopes)
- [Bean Lifecycle](#-bean-lifecycle)
- [Code Examples](#-code-examples)
- [Common Mistakes](#-common-mistakes)
- [Resources](#-resources)
- [Checklist](#-checklist)

---

## 📖 Overview

Today we dive deep into **Spring Beans**—the objects that form the backbone of your application. Understanding beans is crucial because everything in Spring revolves around them. We'll learn about bean annotations, configuration, and how to control bean creation and lifecycle.

### 🎯 The Big Picture

**Before Today:** You knew Spring creates objects, but not how or why.
**After Today:** You understand beans, their lifecycle, and how to configure them properly.

---

## 🎯 Learning Objectives

By the end of Day 3, you should be able to:

- ✅ Define what a **Spring Bean** is
- ✅ Use the **four stereotype annotations**: `@Component`, `@Service`, `@Repository`, `@Controller`
- ✅ Distinguish between **@Component** and **@Bean**
- ✅ Create **@Configuration** classes with **@Bean** methods
- ✅ Understand **bean scopes** (Singleton, Prototype, Request, Session)
- ✅ Control bean lifecycle with `@PostConstruct` and `@PreDestroy`
- ✅ Understand **when and where** to use `@Bean` with `@Configuration`

---

## 💡 What I Learned Today

### 1. What is a Bean?

**Simple Definition:** A bean is any object whose **lifecycle is managed by Spring's IOC container**.

| Aspect | Explanation |
|--------|-------------|
| **Who creates it?** | Spring Container (ApplicationContext) |
| **Who manages it?** | Spring Container |
| **How to declare?** | `@Component`, `@Service`, `@Bean`, etc. |
| **Default scope** | Singleton (one instance per container) |

```java
// This class becomes a bean because of @Component
@Component
public class EmailService {
    public void send(String message) {
        System.out.println("Sending: " + message);
    }
}

// Spring now manages this object's lifecycle
// - Creates it at startup
// - Injects it where needed
// - Destroys it on shutdown
```

### 2. The Four Stereotype Annotations

Spring provides four main stereotype annotations, all meta-annotated with `@Component`:

```java
// 1. @Component - Generic bean
@Component
public class UtilityComponent { }

// 2. @Service - Business logic
@Service
public class UserService { }

// 3. @Repository - Data access
@Repository
public class UserRepository { }

// 4. @Controller - Web layer
@Controller
public class UserController { }

// 5. @RestController - REST API (specialization of @Controller)
@RestController
public class UserRestController { }
```

### 3. @Configuration and @Bean (What I Really Learned)

**The Problem:** Sometimes you need to create beans from classes you **cannot modify**—third-party libraries, external APIs, or classes without `@Component`.

**The Solution:** `@Configuration` + `@Bean` let you tell Spring exactly how to create these beans.

**Real Example I Worked With:**

```java
// ❌ I CAN'T DO THIS - RestTemplate is from a library
// I cannot add @Component to RestTemplate
@Component  // Can't modify third-party code!
import org.springframework.web.client.RestTemplate;

// ✅ THIS IS WHAT I LEARNED - Use @Configuration + @Bean
@Configuration  // Tells Spring: "This class has bean definitions"
public class AppConfig {
    
    @Bean  // Tells Spring: "Call this method to get a bean"
    public RestTemplate restTemplate() {
        // I control exactly how this bean is created
        return new RestTemplate();
    }
}
```

**What This Tells the ApplicationContext:**

When I write `@Bean` in a `@Configuration` class, I'm telling Spring:

1. **"Hey Spring, create this object for me"** → Call the method
2. **"Here's exactly how to create it"** → Execute my custom logic
3. **"Name it after the method"** → Bean name = `restTemplate`
4. **"Manage it for me"** → Apply singleton scope (default)
5. **"Wire it if needed"** → Inject any dependencies

**Why This Matters:**

```java
@Configuration
public class AppConfig {
    
    // Scenario 1: Third-party class with custom configuration
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        // I can configure it however I want
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }
    
    // Scenario 2: Multiple beans of same type
    @Bean
    public DataSource devDataSource() {
        // Development database
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:h2:mem:devdb");
        return ds;
    }
    
    @Bean
    public DataSource prodDataSource() {
        // Production database
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:postgresql://prod-db:5432/mydb");
        ds.setUsername("admin");
        ds.setPassword("prod-password");
        return ds;
    }
    
    // Scenario 3: Bean with complex creation logic
    @Bean
    public EmailService emailService() {
        // I control every aspect of creation
        EmailService service = new EmailService();
        service.setHost("smtp.gmail.com");
        service.setPort(587);
        service.setUsername("myapp@gmail.com");
        service.setPassword("app-password");
        return service;
    }
}
```

### 4. Key Difference I Learned: @Component vs @Bean

| Aspect | `@Component` | `@Bean` |
|--------|--------------|---------|
| **Where I put it** | On the class itself | Inside `@Configuration` class |
| **Who controls creation** | Spring controls | I control |
| **Can I use it on third-party classes?** | ❌ No | ✅ Yes |
| **When to use** | For my own classes | For third-party classes, custom config |

**What I Learned:**
- If I own the class → Use `@Component` (or `@Service`, etc.)
- If I don't own the class → Use `@Configuration` + `@Bean`

### 5. Bean Scopes I Learned

| Scope | What It Means | When I Would Use It |
|-------|---------------|---------------------|
| **Singleton** (default) | One instance for entire app | Stateless services, utilities |
| **Prototype** | New instance every time | Stateful objects, shopping carts |
| **Request** | One per HTTP request | Request-specific data |
| **Session** | One per user session | User session data |

**Example I Tried:**

```java
// Singleton (default) - One instance shared everywhere
@Service
public class AppStats {
    private int count = 0;  // ❌ Shared state - problematic!
}

// Prototype - Each use gets a fresh instance
@Component
@Scope("prototype")
public class ShoppingCart {
    private List<String> items = new ArrayList<>();  // ✅ Each user has their own
}
```

### 6. Bean Lifecycle I Observed

```java
@Component
public class DatabaseConnection {
    
    @PostConstruct
    public void init() {
        // Called AFTER Spring creates the bean
        // Perfect for initialization
        System.out.println("🔌 Connection established!");
    }
    
    @PreDestroy
    public void cleanup() {
        // Called BEFORE Spring destroys the bean
        // Perfect for cleanup
        System.out.println("🔌 Connection closed!");
    }
}
```

---

## 🔑 Key Concepts

### 1. What is a Bean?

**Official Definition:** In Spring, the objects that form the backbone of your application and are managed by the Spring IOC container are called **beans**. A bean is an object that is instantiated, assembled, and managed by a Spring IOC container.

**In Simple Terms:** Beans are the Java objects that Spring creates and manages for you. You don't use `new`—Spring does it.

```java
// Before Spring (you manage)
EmailService service = new EmailService();  // You create it
service.send("Hello");                      // You manage it

// With Spring (Spring manages)
@Component
public class EmailService {
    public void send(String message) {
        System.out.println("Sending: " + message);
    }
}

// Somewhere else (Spring injects it)
@Autowired
private EmailService service;  // Spring gives you a fully-managed bean
```

### 2. Bean Lifecycle

```
┌─────────────────────────────────────────────────────────────────┐
│                    Bean Lifecycle                               │
│                                                                 │
│  1. Spring scans for @Component/@Service/etc.                  │
│     ↓                                                          │
│  2. Bean Definition Created (metadata)                         │
│     ↓                                                          │
│  3. Bean Instantiated (constructor called)                     │
│     ↓                                                          │
│  4. Dependencies Injected (@Autowired)                         │
│     ↓                                                          │
│  5. @PostConstruct Method Called                               │
│     ↓                                                          │
│  6. Bean Ready for Use                                         │
│     ↓                                                          │
│  7. Application Runs                                           │
│     ↓                                                          │
│  8. @PreDestroy Method Called (on shutdown)                    │
│     ↓                                                          │
│  9. Bean Destroyed                                             │
└─────────────────────────────────────────────────────────────────┘
```

---

## 🏷️ The Four Stereotype Annotations

### Complete Comparison

| Annotation | Layer | Purpose | When I Use It |
|------------|-------|---------|---------------|
| `@Component` | Generic | Any Spring-managed bean | Utility classes that don't fit other categories |
| `@Service` | Service | Business logic | Classes containing business rules and algorithms |
| `@Repository` | Persistence | Data access | Classes that interact with the database |
| `@Controller` | Web | Web endpoints | Classes that handle web requests and return views |
| `@RestController` | Web | REST APIs | REST API endpoints (combines `@Controller` + `@ResponseBody`) |

### Why Separate Annotations?

```java
// All are meta-annotated with @Component
@Service      // → @Component
@Repository   // → @Component
@Controller   // → @Component

// They exist for:
// 1. Semantic clarity - makes code self-documenting
// 2. AOP targeting - can apply aspects to specific layers
// 3. Exception translation (for @Repository)
```

### Example: Exception Translation for @Repository

```java
@Repository
public class UserRepository {
    public User findById(Long id) {
        try {
            // Database operation
        } catch (SQLException e) {
            // Spring automatically translates to DataAccessException
            // So you don't deal with vendor-specific exceptions!
        }
    }
}
```

---

## ⚙️ @Configuration and @Bean Deep Dive

### What is @Configuration?

**@Configuration** marks a class as a source of bean definitions. It tells Spring: *"This class contains methods that produce beans for the container."*

```java
@Configuration  // ← Tells Spring: "This class has bean definitions"
public class AppConfig {
    
    @Bean  // ← Tells Spring: "Call this method to get a bean"
    public EmailService emailService() {
        return new EmailService();
    }
}
```

### When to Use @Configuration + @Bean

| Scenario | Use @Configuration + @Bean | Example |
|----------|---------------------------|---------|
| **Third-party classes** | ✅ Yes | `RestTemplate`, `ObjectMapper` |
| **Custom creation logic** | ✅ Yes | Complex configuration |
| **Conditional beans** | ✅ Yes | `@ConditionalOnProperty` |
| **Multiple beans same type** | ✅ Yes | Different implementations |
| **Your own classes** | ❌ Use `@Component` | `UserService`, `UserRepository` |

### The @Bean Method Explained

```java
@Configuration
public class AppConfig {
    
    // 1. Simple bean creation
    @Bean
    public EmailService emailService() {
        return new EmailService();
    }
    
    // 2. Bean with dependencies
    @Bean
    public OrderService orderService() {
        // Spring calls emailService() to get the dependency
        return new OrderService(emailService());
    }
    
    // 3. Bean with complex configuration
    @Bean
    public DataSource dataSource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:postgresql://localhost:5432/mydb");
        ds.setUsername("admin");
        ds.setPassword("password");
        ds.setMaximumPoolSize(10);
        ds.setConnectionTimeout(30000);
        return ds;
    }
    
    // 4. Bean from third-party library
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();  // Can't use @Component
    }
}
```

### What @Bean Tells the ApplicationContext

When Spring processes `@Bean` methods, it tells the ApplicationContext:

1. **"Create This Bean"** - Call the method and register the returned object
2. **"Here's How"** - Execute the method body for custom creation logic
3. **"Name It"** - Use method name as bean name (or specify `name` attribute)
4. **"Manage It"** - Apply default singleton scope
5. **"Wire Dependencies"** - Inject dependencies automatically

```java
// What Spring sees:
@Bean
public EmailService emailService() {  // Bean name = "emailService"
    return new EmailService();        // Bean instance
}

// What Spring registers in ApplicationContext:
// "emailService" → EmailService instance (managed by Spring)
```

---

## 🆚 @Bean vs @Component

### When I Use Each

```java
// ✅ I use @Component for MY classes
@Service
public class UserService { }  // I own this class

@Repository
public class UserRepository { }  // I own this class

// ✅ I use @Bean for THIRD-PARTY classes
@Configuration
public class AppConfig {
    
    @Bean
    public RestTemplate restTemplate() {  // I don't own this class
        return new RestTemplate();
    }
    
    @Bean
    public ObjectMapper objectMapper() {  // I don't own this class
        return new ObjectMapper();
    }
}
```

### Quick Decision Guide

| Question | Answer |
|----------|--------|
| Do I own the class? | ✅ Yes → Use `@Component` (or `@Service`/`@Repository`/`@Controller`) |
| Is it from a third-party library? | ✅ Yes → Use `@Configuration` + `@Bean` |
| Do I need complex creation logic? | ✅ Yes → Use `@Configuration` + `@Bean` |
| Do I need multiple beans of same type? | ✅ Yes → Use `@Configuration` + `@Bean` |

---

## 🔄 Bean Scopes Complete Guide

### Singleton (Default)

```java
@Component  // @Scope("singleton") by default
public class AppConfig { }

// One instance per container
@Service
public class TestService {
    @Autowired
    private AppConfig config1;
    
    @Autowired
    private AppConfig config2;
    
    public void test() {
        System.out.println(config1 == config2);  // true - same instance!
    }
}
```

**When to use:** Stateless services, utility classes, configuration beans.

### Prototype

```java
@Component
@Scope("prototype")
public class ShoppingCart { }

// New instance every time
@Service
public class CartService {
    @Autowired
    private ShoppingCart cart1;
    
    @Autowired
    private ShoppingCart cart2;
    
    public void test() {
        System.out.println(cart1 == cart2);  // false - different instances!
    }
}
```

**When to use:** Stateful objects, user sessions, shopping carts.

### Request (Web Applications)

```java
@Component
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class RequestScopedBean {
    private String requestId;
    
    public void setRequestId(String id) {
        this.requestId = id;
    }
}
```

**When to use:** Web applications, request-specific data.

### Session (Web Applications)

```java
@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class UserSession {
    private String username;
    
    public void setUsername(String username) {
        this.username = username;
    }
}
```

**When to use:** User session data, shopping carts per user.

### Scope Comparison

| Scope | Description | Lifecycle | When to Use |
|-------|-------------|-----------|-------------|
| **Singleton** | One instance per container | Created at startup | Stateless services |
| **Prototype** | New instance each time | Created on demand | Stateful objects |
| **Request** | One per HTTP request | Request lifecycle | Web request data |
| **Session** | One per HTTP session | Session lifecycle | User session data |

---

## 💻 Complete Code Examples

### Example 1: All Four Stereotypes

```java
// 1. @Repository - Data Access
@Repository
public class UserRepository {
    private final List<User> users = new ArrayList<>();
    
    public User save(User user) {
        users.add(user);
        return user;
    }
    
    public List<User> findAll() {
        return users;
    }
}

// 2. @Service - Business Logic
@Service
public class UserService {
    private final UserRepository userRepository;
    
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    public User registerUser(User user) {
        // Business logic
        return userRepository.save(user);
    }
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}

// 3. @RestController - REST API
@RestController
@RequestMapping("/api/users")
public class UserRestController {
    private final UserService userService;
    
    public UserRestController(UserService userService) {
        this.userService = userService;
    }
    
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.registerUser(user));
    }
    
    @GetMapping
    public List<User> getUsers() {
        return userService.getAllUsers();
    }
}

// 4. @Component - Generic Bean (if needed)
@Component
public class PasswordValidator {
    public boolean isValid(String password) {
        return password.length() >= 8;
    }
}
```

### Example 2: @Configuration with @Bean (What I Really Learned)

```java
@Configuration
public class AppConfig {
    
    // Bean from third-party library - I can't add @Component to this
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    
    // Bean with custom logic - I control creation
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }
    
    // Bean with complex configuration - I decide how it's created
    @Bean
    public DataSource dataSource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:postgresql://localhost:5432/mydb");
        ds.setUsername("admin");
        ds.setPassword("password");
        ds.setMaximumPoolSize(10);
        return ds;
    }
    
    // Bean with dependencies - Spring injects automatically
    @Bean
    public GreetingService greetingService(GreetingStrategy strategy) {
        return new GreetingService(strategy);
    }
}
```

### Example 3: Different Bean Scopes

```java
// Singleton (default)
@Component
public class AppStats {
    private int requestCount = 0;
    
    public void increment() {
        requestCount++;
    }
    
    public int getCount() {
        return requestCount;  // Shared across all uses
    }
}

// Prototype
@Component
@Scope("prototype")
public class ShoppingCart {
    private final List<String> items = new ArrayList<>();
    
    public void addItem(String item) {
        items.add(item);
    }
    
    public List<String> getItems() {
        return items;
    }
}

// Testing scopes
@Service
public class CartService {
    @Autowired
    private ApplicationContext context;
    
    public void testScopes() {
        // Singleton - same instance
        AppStats stats1 = context.getBean(AppStats.class);
        AppStats stats2 = context.getBean(AppStats.class);
        System.out.println(stats1 == stats2);  // true
        
        // Prototype - different instances
        ShoppingCart cart1 = context.getBean(ShoppingCart.class);
        ShoppingCart cart2 = context.getBean(ShoppingCart.class);
        System.out.println(cart1 == cart2);  // false
    }
}
```

### Example 4: Main Application

```java
@SpringBootApplication
public class SpringBootJourneyApplication {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(SpringBootJourneyApplication.class, args);
        
        System.out.println("\n✅ Application Started Successfully!");
        System.out.println("📊 Total beans: " + context.getBeanDefinitionCount());
        
        // Print all stereotype beans
        String[] beanNames = context.getBeanDefinitionNames();
        System.out.println("\n📋 Beans in container:");
        for (String name : beanNames) {
            if (name.contains("Service") || name.contains("Repository") || 
                name.contains("Controller") || name.contains("Config")) {
                System.out.println("  - " + name);
            }
        }
    }
}
```

---

## ❌ Common Mistakes

### Mistake 1: Forgetting @Configuration with @Bean

```java
// ❌ WRONG - @Bean only works in @Configuration
public class AppConfig {
    @Bean
    public EmailService emailService() {
        return new EmailService();
    }
}

// ✅ CORRECT
@Configuration
public class AppConfig {
    @Bean
    public EmailService emailService() {
        return new EmailService();
    }
}
```

### Mistake 2: Using @Component on Third-Party Classes

```java
// ❌ WRONG - Can't modify third-party code
@Component  // Can't add this to RestTemplate!
import org.springframework.web.client.RestTemplate;

// ✅ CORRECT - Use @Bean instead
@Configuration
public class AppConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
```

### Mistake 3: Not Understanding Singleton Default

```java
// ❌ WRONG - Assuming prototype behavior
@Component
public class Counter {
    private int count = 0;
    
    public int increment() {
        return ++count;  // Shared state across app!
    }
}

// ✅ CORRECT - Use prototype for stateful beans
@Component
@Scope("prototype")
public class Counter {
    private int count = 0;
    
    public int increment() {
        return ++count;  // Each instance has its own count
    }
}
```

### Mistake 4: Using Wrong Stereotype

```java
// ❌ WRONG - Controller should not have business logic
@Controller
public class UserController {
    @Autowired
    private UserRepository repo;  // Direct repository access
    
    @GetMapping("/users")
    public String getUsers() {
        return repo.findAll();  // Bypasses service layer
    }
}

// ✅ CORRECT - Proper separation
@Service
public class UserService {
    private final UserRepository repo;
    // Business logic here
}

@Controller
public class UserController {
    private final UserService service;
    // Just delegates to service
}
```

---

## 📝 Quick Reference: Bean Annotations

| Annotation | Purpose | Example |
|------------|---------|---------|
| `@Component` | Generic bean | Utility classes |
| `@Service` | Business logic | Service layer |
| `@Repository` | Data access | DAO/Repository |
| `@Controller` | Web endpoints | Web controllers |
| `@RestController` | REST endpoints | `@Controller` + `@ResponseBody` |
| `@Configuration` | Bean definitions | Contains `@Bean` methods |
| `@Bean` | Manual bean creation | Third-party classes |
| `@Scope` | Bean lifecycle | Singleton, Prototype, etc. |
| `@PostConstruct` | Init callback | After dependencies injected |
| `@PreDestroy` | Destroy callback | Before bean destroyed |

---

## ✅ Day 3 Checklist

### Concepts
- [x] Define what a **Spring Bean** is
- [x] Understand **the four stereotype annotations**
- [x] Know when to use `@Component` vs `@Service` vs `@Repository` vs `@Controller`
- [x] Understand **@Configuration** and **@Bean** usage
- [x] Distinguish **@Component vs @Bean**
- [x] Understand **bean scopes** (Singleton, Prototype, Request, Session)
- [x] Use **@PostConstruct** and **@PreDestroy**

### What I Learned Specifically
- [x] **@Configuration + @Bean** is for third-party classes I can't modify
- [x] **@Component** is for my own classes I can annotate
- [x] **@Bean** tells ApplicationContext to call my method and register the result
- [x] **Singleton** is default scope - one instance shared everywhere
- [x] **Prototype** creates new instance each time - good for stateful objects

### Code
- [x] Created service classes with `@Service`
- [x] Created repository classes with `@Repository`
- [x] Created controllers with `@RestController`
- [x] Created `@Configuration` class with `@Bean`
- [x] Used `@Scope` for prototype beans
- [x] Implemented lifecycle callbacks

### Documentation
- [x] Created this README
- [x] Committed to Git
- [x] Pushed to GitHub

---

## 📚 Resources

### Official Documentation
- [Spring Beans](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans)
- [Bean Scopes](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-factory-scopes)
- [@Configuration](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-java)
- [Stereotype Annotations](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-stereotype-annotations)

### Tutorials
- [Baeldung - Spring Beans](https://www.baeldung.com/spring-bean-annotations)
- [Baeldung - Spring Stereotype Annotations](https://www.baeldung.com/spring-component-repository-service)
- [Amigoscode - Spring Boot Tutorial (YouTube)](https://www.youtube.com/watch?v=9SGDpanrc8U)

---

## 🎓 Key Takeaway

> **"Beans are the atoms of Spring. Every Spring application is built from them. Understanding beans means understanding Spring itself."**

### Summary of What @Bean Tells the ApplicationContext

When you write `@Bean` in a `@Configuration` class, you're telling Spring:

1. **"Create this object"** - Call this method and register the result
2. **"Here's how"** - Execute this custom creation logic
3. **"Name it"** - Use the method name as bean name
4. **"Manage it"** - Apply the specified scope (singleton by default)
5. **"Wire it"** - Inject any needed dependencies

**The ApplicationContext then:**
- Executes the method at the right time
- Registers the object as a bean
- Manages its lifecycle (creation → usage → destruction)
- Makes it available for dependency injection

---

**Date**: June 23, 2026  
**Status**: ✅ Day 3 Complete!  
**Next**: Day 4 - Autowiring & Qualifiers

---

> *"A bean is just a Java object, but Spring makes it so much more."*
```

---

