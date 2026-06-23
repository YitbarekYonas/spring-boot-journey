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
- [Key Concepts](#-key-concepts)
- [What is a Bean?](#-what-is-a-bean)
- [The Four Stereotype Annotations](#-the-four-stereotype-annotations)
- [@Component vs @Service vs @Repository vs @Controller](#-component-vs-service-vs-repository-vs-controller)
- [@Configuration and @Bean](#-configuration-and-bean)
- [@Bean vs @Component](#-bean-vs-component)
- [Bean Scopes](#-bean-scopes)
- [Bean Lifecycle](#-bean-lifecycle)
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

### From the Exercise

1. **What is a Bean?**
   - A bean is a Java object **managed by Spring** instead of me using `new`
   - Spring handles creation, injection, and destruction

2. **The Four Stereotype Annotations**
   - `@Component` - Generic Spring bean
   - `@Service` - Business logic layer
   - `@Repository` - Data access layer  
   - `@Controller` - Web layer
   - All do the same thing (create a bean) but communicate intent

3. **@Component vs @Bean**
   - `@Component` → For MY classes (I own the code)
   - `@Bean` → For THIRD-PARTY classes (I don't own the code)
   - `@Bean` requires `@Configuration` class

4. **Bean Scopes**
   - **Singleton** (default) → One instance for entire app
   - **Prototype** → New instance every time
   - Singleton is safe for stateless services
   - Prototype is needed for stateful objects

5. **Bean Lifecycle**
   - `@PostConstruct` → Runs after dependencies are injected (initialization)
   - `@PreDestroy` → Runs before bean is destroyed (cleanup)

### Key Insights

| Concept | My Understanding |
|---------|------------------|
| **Bean** | Java object managed by Spring |
| **@Component** | Tells Spring: "Make this class a bean" |
| **@Service** | Specialized @Component for business logic |
| **@Repository** | Specialized @Component for data access |
| **@Configuration** | Tells Spring: "This class has bean definitions" |
| **@Bean** | Tells Spring: "Call this method to create a bean" |
| **Singleton** | Default scope - one instance per container |
| **Prototype** | New instance every time |
| **@PostConstruct** | Runs after dependencies are injected |
| **@PreDestroy** | Runs before bean is destroyed |

---

