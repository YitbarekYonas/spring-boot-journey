# Week 1 Mini-Project: Config-Driven Greeting Service

> **"The Strategy Pattern meets Spring DI — configuration drives behavior."**

---

## 🎯 Learning Objectives Demonstrated

- ✅ Day 1: `@SpringBootApplication`, ComponentScan
- ✅ Day 2: Constructor injection, `@Service`, `@Controller`
- ✅ Day 3: `@Component` with explicit bean names, `@Configuration`
- ✅ Day 4: `@Qualifier`, `@Primary`, Strategy Pattern with DI
- ✅ Day 5: `@ConfigurationProperties`, `@Validated`, Profiles
- ✅ Day 6: Auto-configuration awareness

---

## 💡 What This Project Demonstrates

### 1. Strategy Pattern with Spring DI
The `@Configuration` class reads `active-strategy` from `application.yml` and returns the correct implementation as `@Primary`.

### 2. Externalized Configuration
- Base config in `application.yml`
- Dev config in `application-dev.yml`
- Prod config in `application-prod.yml`

### 3. Fail-Fast Validation
`@ConfigurationProperties` + `@Validated` + `@Pattern` catches invalid config at startup.

### 4. Thin Controllers
Controller delegates 100% to service — zero business logic.

---

## 🚀 How to Run

```bash
mvn spring-boot:run