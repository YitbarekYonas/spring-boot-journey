```markdown
# Day 6: Spring Boot Auto-Configuration

> **"Spring Boot magically configures things—but you can override anything."**

---

## 🎯 Learning Objectives

- ✅ Understand what **Auto-Configuration** is
- ✅ Read the **Conditions Evaluation Report**
- ✅ Identify which **starters** auto-configure which beans
- ✅ Override auto-configuration with **custom beans**

---

## 💡 What I Learned

### 1. What is Auto-Configuration?
Spring Boot automatically configures beans based on dependencies in your classpath.

| Dependency | Auto-Configured Beans |
|------------|----------------------|
| `spring-boot-starter-web` | Tomcat, DispatcherServlet, Jackson |
| `spring-boot-starter-data-jpa` | DataSource, EntityManager, TransactionManager |

### 2. The Conditions Evaluation Report
Enabled with `debug: true` in `application.yml`:
- **Positive matches** → Configurations that applied ✅
- **Negative matches** → Configurations that didn't apply ❌

### 3. 5 Auto-Configured Beans I Found

| Bean | Starter | Purpose |
|------|---------|---------|
| `dispatcherServlet` | spring-boot-starter-web | Handles HTTP requests |
| `requestMappingHandlerAdapter` | spring-boot-starter-web | Processes @RequestMapping |
| `jacksonObjectMapper` | spring-boot-starter-web | JSON serialization |
| `defaultServletHandlerMapping` | spring-boot-starter-web | Serves static resources |
| `errorPageCustomizer` | spring-boot-starter-web | Handles error pages |

### 4. Overriding Auto-Configuration

```java
@Configuration
public class AppConfig {
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }
}
```

**Result:** Report changes from **positive** to **negative** match.

---

## 🔑 Key Concepts

| Concept | My Understanding |
|---------|------------------|
| Auto-Configuration | Spring Boot guesses config based on classpath |
| Conditions Report | Shows what was configured and why |
| Positive Matches | Configurations that applied |
| Negative Matches | Configurations that didn't apply |
| Custom Beans | Override auto-configured beans |
| `debug: true` | Enables auto-configuration report |

---

## 💻 Code Examples

### `application.yml`
```yaml
debug: true
server:
  port: 8080
```

### `AppConfig.java`
```java
@Configuration
public class AppConfig {
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }
}
```

### Checking Beans in Main
```java
String[] keyBeans = {
    "dispatcherServlet",
    "requestMappingHandlerAdapter",
    "jacksonObjectMapper",
    "defaultServletHandlerMapping",
    "errorPageCustomizer"
};

for (String beanName : keyBeans) {
    try {
        Object bean = context.getBean(beanName);
        System.out.println("✅ " + beanName);
    } catch (Exception e) {
        System.out.println("❌ " + beanName + " → Not found");
    }
}
```

---

## ❌ Common Mistakes

```java
// ❌ Forgetting debug: true
application.yml  // No report shown

// ✅ Correct
debug: true
```

```java
// ❌ Overriding without understanding
@Bean
public DataSource dataSource() { ... }

// ✅ Know why you're overriding
@Bean
public ObjectMapper objectMapper() {
    // Need custom date format
}
```

---

## ✅ Checklist

- [x] Enabled `debug: true`
- [x] Found 5 auto-configured beans in positive matches
- [x] Traced each bean to its starter
- [x] Created custom `ObjectMapper` bean
- [x] Verified report changed to negative match

---

## 🎓 Key Takeaway

> **"Auto-configuration is Spring Boot's superpower. Understanding it makes you a better Spring developer."**

**Status**: ✅ Day 6 Complete!
```