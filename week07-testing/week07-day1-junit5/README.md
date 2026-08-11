# Week 7, Day 1: JUnit 5 Fundamentals

[![Status](https://img.shields.io/badge/Status-Completed-brightgreen.svg)]()
[![Day](https://img.shields.io/badge/Day-1-blue.svg)]()
[![Topic](https://img.shields.io/badge/Topic-JUnit%205-orange.svg)]()

> **"A test that doesn't run fast isn't a unit test — it's an integration test in disguise."**

---

## 🎯 Learning Objectives

- ✅ Understand test lifecycle: `@BeforeAll`, `@BeforeEach`, `@AfterEach`, `@AfterAll`
- ✅ Use JUnit assertions: `assertEquals`, `assertThrows`, `assertTrue`, `assertAll`
- ✅ Use AssertJ fluent assertions: `assertThat().isEqualTo()`, `.contains()`, `.hasSize()`
- ✅ Write `@ParameterizedTest` with `@ValueSource`, `@CsvSource`, `@EnumSource`, `@NullAndEmptySource`
- ✅ Group related tests with `@Nested` and `@DisplayName`
- ✅ Write pure unit tests — NO Spring context, runs in milliseconds

---

## 💡 What I Learned Today

### 1. Test Lifecycle Execution Order

```
@BeforeAll  → runs ONCE before the whole class
  @BeforeEach → runs before EACH test
    @Test
  @AfterEach  → runs after EACH test
  @BeforeEach → runs before EACH test
    @Test
  @AfterEach  → runs after EACH test
@AfterAll   → runs ONCE after the whole class
```

### 2. JUnit Assertions vs AssertJ

| Scenario | JUnit 5 | AssertJ |
|----------|---------|---------|
| Equality | `assertEquals(expected, actual)` | `assertThat(actual).isEqualTo(expected)` |
| Boolean | `assertTrue(condition)` | `assertThat(condition).isTrue()` |
| Exception | `assertThrows(Ex.class, () -> ...)` | `assertThatThrownBy(() -> ...).isInstanceOf(Ex.class)` |
| List size | `assertEquals(3, list.size())` | `assertThat(list).hasSize(3)` |
| List contains | `assertTrue(list.contains("x"))` | `assertThat(list).contains("x")` |
| Multiple | `assertAll(() -> ..., () -> ...)` | chain: `.isNotNull().isGreaterThan(0)` |

> Use **AssertJ** — better failure messages, reads like English, chains naturally.

### 3. Parameterized Test Sources

| Annotation | Use When |
|------------|----------|
| `@ValueSource` | Single param, multiple literal values |
| `@CsvSource` | Multiple params per row (input + expected output) |
| `@EnumSource` | Run for every value of an enum |
| `@NullAndEmptySource` | Test null AND `""` in one annotation |

---

## 💻 Code Examples

### Test Lifecycle

```java
@BeforeAll
static void initAll() { /* runs ONCE — must be static */ }

@BeforeEach
void setUp() {
    calculator = new TaskPricingCalculator(); // fresh instance per test
}

@AfterEach
void tearDown() { calculator = null; }

@AfterAll
static void tearDownAll() { /* runs ONCE — must be static */ }
```

### assertEquals + assertThrows

```java
@Test
void highPriorityFeatureCosts200() {
    Task task = Task.builder().type(FEATURE).priority(HIGH).build();
    assertEquals(new BigDecimal("200.00"), calculator.calculatePrice(task));
}

@Test
void nullTaskThrowsException() {
    IllegalArgumentException ex = assertThrows(
        IllegalArgumentException.class,
        () -> calculator.calculatePrice(null)
    );
    assertEquals("Task cannot be null", ex.getMessage());
}
```

### AssertJ Fluent Assertions

```java
assertThat(price)
    .isNotNull()
    .isGreaterThan(BigDecimal.ZERO)
    .isLessThan(new BigDecimal("442.50"))
    .isEqualByComparingTo(new BigDecimal("398.25"));

assertThat(errors)
    .hasSize(5)
    .contains("Title must be at least 3 characters", "Priority is required");
```

### assertAll — All Assertions Run Even on Failure

```java
assertAll("discount checks",
    () -> assertEquals(new BigDecimal("180.00"), calculator.applyDiscount(price, 10)),
    () -> assertEquals(new BigDecimal("150.00"), calculator.applyDiscount(price, 25)),
    () -> assertEquals(new BigDecimal("0.00"),   calculator.applyDiscount(price, 100))
);
// All three run — all failures reported at once
```

### @ParameterizedTest with @CsvSource

```java
@ParameterizedTest(name = "Type={0}, Priority={1} → ${2}")
@CsvSource({
    "BUG,     LOW,    50.00",
    "BUG,     HIGH,   100.00",
    "FEATURE, MEDIUM, 150.00",
})
void priceMatrix(String type, String priority, String expected) {
    Task task = Task.builder()
        .type(Task.Type.valueOf(type.trim()))
        .priority(Task.Priority.valueOf(priority.trim()))
        .build();
    assertThat(calculator.calculatePrice(task))
        .isEqualByComparingTo(new BigDecimal(expected.trim()));
}
```

### @Nested — Grouped Test Structure

```java
@Nested
@DisplayName("Title validation rules")
class TitleValidationTests {

    @Test
    @DisplayName("Title shorter than 3 chars should add error")
    void shortTitleAddsError() { ... }

    @Test
    @DisplayName("Title exactly 3 chars should pass — boundary check")
    void titleExactly3CharsIsValid() { ... }
}
```

---

## ▶️ How to Run Tests

```bash
# Run all tests
mvn test

# Run only one test class
mvn test -Dtest=TaskPricingCalculatorTest

# Run only one test method
mvn test -Dtest=TaskPricingCalculatorTest#highPriorityFeatureCosts200

# Run with verbose output
mvn test -Dsurefire.useFile=false
```

---

## 📋 Test Summary

| Test Class | Tests | What It Covers |
|-----------|-------|----------------|
| `TaskPricingCalculatorTest` | 18 | Lifecycle, assertEquals, assertThrows, assertAll, AssertJ, @CsvSource, @EnumSource, @Nested |
| `TaskValidatorTest` | 14 | @ValueSource, @NullAndEmptySource, @Nested, AssertJ list assertions |
| **Total** | **32** | **Full JUnit 5 feature coverage** |

---

## ❌ Common Mistakes

| Mistake | Fix |
|---------|-----|
| `@SpringBootTest` on a pure unit test | Remove it — no Spring needed, tests will be 10x faster |
| Sharing mutable state between tests | Use `@BeforeEach` to create a fresh instance per test |
| `@BeforeAll` / `@AfterAll` not static | These must be `static` — JUnit requirement |
| Testing implementation, not behavior | Test what the method does, not how it does it |
| One giant test method asserting everything | One test = one behavior; use `@Nested` to group |
| `assertEquals(actual, expected)` wrong order | Always `assertEquals(EXPECTED, actual)` — expected first |

---

## ✅ Day 1 Checklist

### Lifecycle
- [x] `@BeforeAll` for one-time expensive setup
- [x] `@BeforeEach` for fresh object per test
- [x] `@AfterEach` for cleanup
- [x] `@AfterAll` for one-time teardown

### Assertions
- [x] `assertEquals` / `assertNotEquals`
- [x] `assertTrue` / `assertFalse`
- [x] `assertThrows` with message assertion
- [x] `assertAll` for grouped assertions
- [x] AssertJ `assertThat()` chaining

### Parameterized Tests
- [x] `@ValueSource` — single param, multiple values
- [x] `@CsvSource` — multiple params per row
- [x] `@EnumSource` — all enum values
- [x] `@NullAndEmptySource` — null and empty string

### Structure
- [x] `@DisplayName` on classes and methods
- [x] `@Nested` for grouped test contexts
- [x] Helper method `buildValidTask()` — avoids duplicate setup in every test

---

**Date**: August 9, 2026
**Status**: ✅ Week 7, Day 1 Complete!
**Next**: Day 2 — Mockito (mock the repository, test the service in isolation)

> *"Write tests for behavior, not for lines of code. Coverage is a side effect, not a goal."*
