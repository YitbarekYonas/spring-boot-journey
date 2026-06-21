# 🚀 Spring Boot Backend Development Journey

[![Java](https://img.shields.io/badge/Java-25-blue.svg)](https://adoptium.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Maven](https://img.shields.io/badge/Maven-3.9.0-red.svg)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](http://makeapullrequest.com)

> **An 8-Week Journey from Spring Boot Beginner to Production-Ready Developer**

Welcome to my Spring Boot learning journey! This repository documents my complete path from understanding the fundamentals to building production-ready APIs with Spring Boot 4.1.0 and Java 25.

---

## 📚 Table of Contents

- [Overview](#-overview)
- [Why This Repository](#-why-this-repository)
- [Tech Stack](#-tech-stack)
- [8-Week Curriculum](#-8-week-curriculum)
- [How to Navigate](#-how-to-navigate)
- [How to Run](#-how-to-run)
- [Project Structure](#-project-structure)
- [Progress Tracking](#-progress-tracking)
- [Key Learnings So Far](#-key-learnings-so-far)
- [Resources](#-resources)
- [Connect With Me](#-connect-with-me)

---

## 📖 Overview

This repository is a **hands-on, project-based learning journey** through Spring Boot development. Each week builds upon the previous, progressively adding complexity until we reach a production-ready application.

### 🎯 Course Goals

| Goal | Description |
|------|-------------|
| **Master Fundamentals** | Understand IOC, DI, Beans, Configuration |
| **Build REST APIs** | Create production-ready RESTful services |
| **Work with Databases** | JPA, Hibernate, Relationships |
| **Secure Applications** | Spring Security, JWT Authentication |
| **Write Tests** | Unit, Integration, Testcontainers |
| **Deploy to Production** | Docker, CI/CD, Cloud Deployment |
| **Create Portfolio** | Build a complete project to showcase |

### 📅 Journey Timeline

- **Start Date:** June 21, 2026
- **Duration:** 8 Weeks
- **Commitment:** 2-3 hours daily
- **Format:** Daily concepts + Weekly mini-projects

---

## 🤔 Why This Repository

This isn't just a collection of code snippets. This repository represents:

| Aspect | What It Shows |
|--------|---------------|
| **Consistent Learning** | 8 weeks of dedicated daily practice |
| **Progressive Complexity** | Each week builds on the previous |
| **Real-World Projects** | Mini-projects that demonstrate practical skills |
| **Clean Code** | Following Spring Boot best practices from Day 1 |
| **Documentation** | Each concept is explained, not just implemented |
| **Portfolio Ready** | Complete projects to showcase to employers |

### What You'll Find Here

| Element | Description |
|---------|-------------|
| **Weekly Projects** | Working Spring Boot applications for each week |
| **READMEs** | Detailed explanations of each week's concepts |
| **Postman Collections** | API testing documentation |
| **Common Mistakes** | Problems I encountered and how to fix them |
| **Best Practices** | Production-grade code from Day 1 |

---

## 🛠️ Tech Stack

### Core Technologies

| Category | Technology | Version |
|----------|------------|---------|
| **Language** | Java | 25 LTS |
| **Framework** | Spring Boot | 4.1.0 |
| **Build Tool** | Maven | 3.9.x |
| **Web Server** | Embedded Tomcat | 11.0.x |

### Database & Persistence

| Category | Technology |
|----------|------------|
| **ORM** | Spring Data JPA, Hibernate |
| **Dev Database** | H2 (In-Memory) |
| **Production Database** | PostgreSQL 15+ |
| **Migrations** | Flyway / Liquibase |

### Security & API

| Category | Technology |
|----------|------------|
| **Security** | Spring Security 6.x |
| **Authentication** | JWT (JSON Web Tokens) |
| **API Testing** | Postman, IntelliJ HTTP Client |
| **Documentation** | Springdoc OpenAPI (Swagger) |

### Testing & Quality

| Category | Technology |
|----------|------------|
| **Unit Testing** | JUnit 5 |
| **Mocking** | Mockito |
| **Integration Testing** | Testcontainers |
| **Assertions** | AssertJ |

### DevOps & Deployment

| Category | Technology |
|----------|------------|
| **Containerization** | Docker |
| **Orchestration** | Docker Compose |
| **CI/CD** | GitHub Actions |
| **Cloud Deployment** | Railway / Render |

### Developer Tools

| Category | Technology |
|----------|------------|
| **IDE** | IntelliJ IDEA |
| **Code Generation** | Lombok |
| **Live Reload** | Spring Boot DevTools |
| **Profiling** | Spring Boot Actuator |

---

## 📚 8-Week Curriculum

### Complete Roadmap

| Week | Topic | Focus Areas | Mini-Project | Status |
|------|-------|-------------|--------------|--------|
| **Week 1** | [Spring Boot Core](week01-core/) | IOC, DI, Beans, Configuration | Config-Driven Greeting Service | ✅ Complete |
| **Week 2** | REST APIs & Controllers | HTTP Methods, Request/Response | In-Memory Task Manager | ⏳ Upcoming |
| **Week 3** | Database Integration | JPA, Hibernate, Relationships | Library Management (Persistent) | ⏳ Upcoming |
| **Week 4** | Advanced JPA | JPQL, Native Queries, Pagination | Library Management (Query Layer) | ⏳ Upcoming |
| **Week 5** | Security & JWT | Spring Security, Authentication | JWT Auth Service | ⏳ Upcoming |
| **Week 6** | Production Ready | Validation, DTOs, Exceptions | Production-Hardened Task Manager | ⏳ Upcoming |
| **Week 7** | Testing | JUnit, Mockito, Testcontainers | Complete Test Suite | ⏳ Upcoming |
| **Week 8** | Deployment | Docker, CI/CD, Cloud | Deployed Application | ⏳ Upcoming |
| **Capstone** | [Final Project](final-capstone/) | Complete Blog Platform API | Production-Ready App | ⏳ Upcoming |

---

## 📊 Weekly Breakdown

### Week 1: Spring Boot Core 
**Goal:** Understand what Spring actually does under the hood

| Day | Topic | Key Concepts |
|-----|-------|--------------|
| Day 1 | Project Setup & First Endpoint | Spring Initializr, `@RestController` |
| Day 2 | IOC & Dependency Injection | `@Service`, Constructor Injection |
| Day 3 | Beans & Annotations | `@Component`, `@Bean`, `@Configuration` |
| Day 4 | Autowiring & Qualifiers | `@Autowired`, `@Qualifier`, `@Primary` |
| Day 5 | Configuration & Profiles | `application.yml`, Profiles |
| Day 6 | Auto-Configuration | `@EnableAutoConfiguration` |
| Day 7 | Mini-Project | Config-Driven Greeting Service |

**Key Achievement:** Spring Boot registered **153 beans** automatically without any configuration!

---

### Week 2: REST APIs & Controllers
**Goal:** Build real, testable HTTP endpoints

| Day | Topic | Key Concepts |
|-----|-------|--------------|
| Day 1 | REST Fundamentals | HTTP Methods, Resources |
| Day 2 | Path Variables & Query Params | `@PathVariable`, `@RequestParam` |
| Day 3 | Request Body & Response | `@RequestBody`, `ResponseEntity` |
| Day 4 | Postman Mastery | Collections, Environments |
| Day 5 | Headers & CORS | `@RequestHeader`, `@CrossOrigin` |
| Day 6 | Layered Architecture | Controller → Service → Repository |
| Day 7 | Mini-Project | In-Memory Task Manager API |

---

### Week 3: Database Integration
**Goal:** Persist data for real

| Day | Topic | Key Concepts |
|-----|-------|--------------|
| Day 1 | JDBC → JPA → Hibernate | Understanding the stack |
| Day 2 | Entities & Mapping | `@Entity`, `@Id`, `@GeneratedValue` |
| Day 3 | Spring Data JPA Repositories | `JpaRepository`, Derived Queries |
| Day 4 | One-to-Many Relationships | `@OneToMany`, `@ManyToOne` |
| Day 5 | Many-to-Many Relationships | `@ManyToMany`, Join Tables |
| Day 6 | Hibernate Internals | Lifecycle, N+1 Problem |
| Day 7 | Mini-Project | Library Management (Persistent) |

---

### Week 4: Advanced JPA
**Goal:** Query like a professional

| Day | Topic | Key Concepts |
|-----|-------|--------------|
| Day 1 | JPQL Basics | `@Query` with JPQL |
| Day 2 | Native Queries | `@Query(nativeQuery = true)` |
| Day 3 | Pagination & Sorting | `Pageable`, `Page<T>` |
| Day 4 | Projections & DTOs | Interface vs Constructor projections |
| Day 5 | Transactions Deep Dive | `@Transactional`, Propagation |
| Day 6 | Auditing & Optimization | `@CreatedDate`, Fetch Joins |
| Day 7 | Mini-Project | Library Management (Query Layer) |

---

### Week 5: Security & JWT
**Goal:** Secure your APIs the way production does

| Day | Topic | Key Concepts |
|-----|-------|--------------|
| Day 1 | Spring Security Fundamentals | Filter Chain, SecurityFilterChain |
| Day 2 | Authentication vs Authorization | UserDetails, PasswordEncoder |
| Day 3 | BCrypt & Password Handling | `BCryptPasswordEncoder` |
| Day 4 | JWT Fundamentals | Structure, Generation, Validation |
| Day 5 | JWT Filter & Integration | OncePerRequestFilter |
| Day 6 | Refresh Tokens | Token Expiry, Refresh Logic |
| Day 7 | Mini-Project | JWT Auth Service |

---

### Week 6: Production Ready
**Goal:** Make the API robust and clean

| Day | Topic | Key Concepts |
|-----|-------|--------------|
| Day 1 | Role-Based Authorization | `@PreAuthorize`, Roles vs Authorities |
| Day 2 | Global Exception Handling | `@ControllerAdvice`, `@ExceptionHandler` |
| Day 3 | Bean Validation | `@Valid`, `@NotNull`, Custom Validators |
| Day 4 | DTOs & Mapping | Request/Response DTOs, MapStruct |
| Day 5 | Lombok Deep Dive | `@Data`, `@Builder`, `@Slf4j` |
| Day 6 | Logging & Clean Code | SLF4J, Logback, Log Levels |
| Day 7 | Mini-Project | Production-Hardened Task Manager |

---

### Week 7: Testing
**Goal:** Be able to prove your code works

| Day | Topic | Key Concepts |
|-----|-------|--------------|
| Day 1 | JUnit 5 Fundamentals | `@Test`, `@BeforeEach`, Assertions |
| Day 2 | Mockito Basics | `@Mock`, `@InjectMocks`, `verify()` |
| Day 3 | @WebMvcTest | Controller Layer Testing, MockMvc |
| Day 4 | @DataJpaTest | Repository Layer Testing |
| Day 5 | Testcontainers | Real Database Integration Tests |
| Day 6 | Full Integration Tests | End-to-End Testing |
| Day 7 | Mini-Project | Complete Test Suite |

---

### Week 8: Deployment
**Goal:** Ship it!

| Day | Topic | Key Concepts |
|-----|-------|--------------|
| Day 1 | Docker Fundamentals | Dockerfile, Images, Containers |
| Day 2 | Docker Compose | Multi-Container Setup |
| Day 3 | Environment Configuration | Externalizing Secrets, Actuator |
| Day 4 | CI with GitHub Actions | Automated Build & Test |
| Day 5 | CD & Build Artifacts | Docker Image, Container Registry |
| Day 6 | Cloud Deployment | Railway / Render / AWS |
| Day 7 | Final Review & Polish | Documentation, Cleanup |

---


