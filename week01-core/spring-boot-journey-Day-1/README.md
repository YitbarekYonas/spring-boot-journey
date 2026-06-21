# Day 1: Spring Ecosystem & Project Setup

[![Status](https://img.shields.io/badge/Status-Completed-brightgreen.svg)]()
[![Day](https://img.shields.io/badge/Day-1-blue.svg)]()
[![Topic](https://img.shields.io/badge/Topic-Setup-orange.svg)]()

> **"Hello World" - Your First Spring Boot Application**

---

## 📚 Table of Contents

- [Overview](#-overview)
- [Learning Objectives](#-learning-objectives)
- [Key Concepts](#-key-concepts)
- [Code Examples](#-code-examples)
- [Step-by-Step Guide](#-step-by-step-guide)
- [What I Learned](#-what-i-learned)
- [Common Mistakes](#-common-mistakes)
- [Resources](#-resources)
- [Checklist](#-checklist)

---

## 📖 Overview

Today marks the beginning of my Spring Boot journey. The goal was simple: generate a Spring Boot project, understand its structure, and create a working REST endpoint.

### 🎯 The Big Picture

Spring Boot is a tool that makes it easy to create stand-alone, production-grade Spring-based applications. Today we explored:

- What Spring Boot is and why it exists
- How to generate a Spring Boot project
- Understanding the project structure
- Creating our first REST API endpoint
- Seeing Spring Boot's auto-configuration in action

---

## 🎯 Learning Objectives

By the end of Day 1, I achieved:

- ✅ Generate a Spring Boot project using Spring Initializr
- ✅ Understand the project structure
- ✅ Create and run the main application class
- ✅ Build a simple REST endpoint
- ✅ Observe Spring Boot's auto-configuration
- ✅ Understand the `@SpringBootApplication` annotation

---

## 🔑 Key Concepts

### 1. Spring vs Spring Boot vs Spring Framework

| Term | What It Is |
|------|------------|
| **Spring Framework** | The foundation - provides IOC, DI, etc. |
| **Spring Boot** | Auto-configuration on top of Spring Framework |
| **Spring** | Umbrella term for the entire ecosystem |

**Key Insight:** Spring Boot makes Spring easier to use by providing sensible defaults.

### 2. The `@SpringBootApplication` Annotation

```java
@SpringBootApplication  // This one annotation does THREE things:
public class MyApp {
    public static void main(String[] args) {
        SpringApplication.run(MyApp.class, args);
    }
}