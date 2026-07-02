# Day 5: Configuration & Profiles

[![Status](https://img.shields.io/badge/Status-Completed-brightgreen.svg)]()
[![Day](https://img.shields.io/badge/Day-5-blue.svg)]()
[![Topic](https://img.shields.io/badge/Topic-Configuration%20%26%20Profiles-orange.svg)]()

> **"Externalize everything. Never hardcode configuration."**

---

## 📚 Table of Contents

- [Overview](#-overview)
- [Learning Objectives](#-learning-objectives)
- [What I Learned Today](#-what-i-learned-today)
- [Key Concepts](#-key-concepts)
- [@ConfigurationProperties](#-configurationproperties)
- [Profiles](#-profiles)
- [Validation](#-validation)
- [Code Examples](#-code-examples)
- [Deliberately Breaking It](#-deliberately-breaking-it-learning-experience)
- [Common Mistakes](#-common-mistakes)
- [Resources](#-resources)
- [Checklist](#-checklist)

---

## 📖 Overview

Today we learn how to **externalize configuration**—one of Spring Boot's most powerful features. Instead of hardcoding values, we put them in configuration files. We also learn about **profiles** to have different configurations for different environments (dev, test, prod).

### 🎯 The Big Picture

**Before Today:** You hardcoded values like `localhost:2525` in your code.
**After Today:** You externalize configuration and switch between environments easily.

---

## 🎯 Learning Objectives

By the end of Day 5, you should be able to:

- ✅ Use `@ConfigurationProperties` for type-safe configuration
- ✅ Create profile-specific configuration files
- ✅ Switch profiles at runtime
- ✅ Validate configuration with `@Validated` and `@NotBlank`
- ✅ Understand configuration precedence

---

## 💡 What I Learned Today

### From the Exercise

1. **@ConfigurationProperties**
   - Binds YAML properties to Java objects
   - Type-safe with auto-completion in IDE
   - Centralizes all mail-related config in one class
   - `@ConfigurationProperties(prefix = "app.mail")` binds all properties under `app.mail`

2. **Profile-Specific Files**
   - `application-dev.yml` loads when profile is `dev`
   - `application-prod.yml` loads when profile is `prod`
   - Profile-specific files **override** default `application.yml`
   - Command line: `-Dspring-boot.run.profiles=dev`

3. **Configuration Validation**
   - `@Validated` enables validation on configuration
   - `@NotBlank` ensures required properties are present
   - Application **fails fast** if configuration is missing
   - Clear error messages tell you exactly what's wrong

4. **Precedence (Highest to Lowest)**
   - Command line arguments (`--server.port=9090`)
   - Environment variables (`SPRING_APPLICATION_JSON`)
   - Profile-specific files (`application-dev.yml`)
   - Default `application.yml`

### Key Insights

| Concept | My Understanding |
|---------|------------------|
| **@ConfigurationProperties** | Binds YAML properties to Java objects |
| **Profiles** | Different configs for different environments |
| **@Validated** | Enables validation on configuration |
| **@NotBlank** | Ensures required properties are present |
| **Precedence** | Command line > Env vars > Profile files > Default files |
| **Fail Fast** | Validation ensures app fails if config is missing |

---

## 🔑 Key Concepts

### 1. @ConfigurationProperties

**What I Understood:**

Instead of using `@Value("${app.mail.host}")` on each field, I can bind all mail-related properties to a single Java object.

**Before (@Value):**
```java
@Component
public class MailConfig {
    @Value("${app.mail.host}")
    private String host;
    
    @Value("${app.mail.port}")
    private Integer port;
    
    @Value("${app.mail.ssl}")
    private boolean ssl;
}