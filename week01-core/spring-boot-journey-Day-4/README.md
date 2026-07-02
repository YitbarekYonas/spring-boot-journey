# Day 4: Autowiring & Qualifiers

[![Status](https://img.shields.io/badge/Status-Completed-brightgreen.svg)]()
[![Day](https://img.shields.io/badge/Day-4-blue.svg)]()
[![Topic](https://img.shields.io/badge/Topic-Autowiring%20%26%20Qualifiers-orange.svg)]()

> **"When Spring finds multiple beans, tell it which one to use."**

---

## 📚 Table of Contents

- [Overview](#-overview)
- [Learning Objectives](#-learning-objectives)
- [What I Learned Today](#-what-i-learned-today)
- [Key Concepts](#-key-concepts)
- [The Problem: Multiple Beans](#-the-problem-multiple-beans)
- [The Solution: @Qualifier](#-the-solution-qualifier)
- [@Primary vs @Qualifier](#-primary-vs-qualifier)
- [Circular Dependencies](#-circular-dependencies)
- [Code Examples](#-code-examples)
- [Deliberately Breaking It](#-deliberately-breaking-it-learning-experience)
- [Common Mistakes](#-common-mistakes)
- [Resources](#-resources)
- [Checklist](#-checklist)

---

## 📖 Overview

Today we learn how to handle situations where Spring has **multiple beans of the same type**. We'll use `@Qualifier` to tell Spring exactly which bean to inject. We'll also explore **circular dependencies**—what they are, how to recognize them, and how to fix them.

### 🎯 The Big Picture

**Before Today:** Spring could inject dependencies automatically when there was only one bean.
**After Today:** You can guide Spring to inject the right bean when multiple exist.

---

## 🎯 Learning Objectives

By the end of Day 4, you should be able to:

- ✅ Use `@Qualifier` to select specific beans
- ✅ Inject different implementations into different controllers
- ✅ Understand the difference between `@Qualifier` and `@Primary`
- ✅ Recognize circular dependency errors
- ✅ Fix circular dependencies by extracting shared logic

---

## 💡 What I Learned Today

### From the Exercise

1. **The Problem: Multiple Beans of Same Type**
   - When two classes implement the same interface, Spring doesn't know which to inject
   - Error: `Parameter 0 required a single bean, but 2 were found`

2. **The Solution: @Qualifier**
   - `@Qualifier("beanName")` tells Spring exactly which bean to inject
   - The bean name is the method name or class name with lowercase first letter

3. **@Primary for Default**
   - `@Primary` marks a bean as the default choice
   - Used when you have a "main" implementation

4. **Circular Dependencies**
   - When ServiceA needs ServiceB and ServiceB needs ServiceA
   - Spring fails with: `Requested bean is currently in creation`
   - Fix: Extract shared logic to a third service

### Key Insights

| Concept | My Understanding |
|---------|------------------|
| **@Qualifier** | Tells Spring which bean to inject when multiple exist |
| **@Primary** | Marks a bean as the default when no @Qualifier specified |
| **Circular Dependency** | A ↔ B (both need each other) - Spring can't resolve |
| **Fix for Circular** | Extract shared logic to a third class C |
| **Bean Name** | Default is class name with lowercase first letter |

---

## 🔑 Key Concepts

### 1. The Problem: Multiple Beans

```java
// Two implementations of the same interface
@Service
public class FormalGreetingService implements GreetingService {
    @Override
    public String getGreeting() {
        return "Good morning.";
    }
}

@Service
public class CasualGreetingService implements GreetingService {
    @Override
    public String getGreeting() {
        return "Hey! What's up?";
    }
}

// ❌ Spring doesn't know which one to inject!
@RestController
public class GreetingController {
    private final GreetingService greetingService;
    
    public GreetingController(GreetingService greetingService) {
        this.greetingService = greetingService;  // ERROR! 2 beans found
    }
}