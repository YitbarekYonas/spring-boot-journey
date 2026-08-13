# Week 7, Day 3: @WebMvcTest — Controller Layer Testing

[![Status](https://img.shields.io/badge/Status-Completed-brightgreen.svg)]()
[![Day](https://img.shields.io/badge/Day-3-blue.svg)]()
[![Topic](https://img.shields.io/badge/Topic-%40WebMvcTest-orange.svg)]()

> **"Test the HTTP contract — status codes, JSON shape, headers, and validation — without touching the database."**

---

## 🎯 Learning Objectives

- ✅ Understand what `@WebMvcTest` loads and what it skips
- ✅ Use `MockMvc` to simulate GET, POST, PATCH, DELETE requests
- ✅ Assert HTTP status codes with `andExpect(status().isOk())`
- ✅ Assert JSON response body with `jsonPath("$.field").value(...)`
- ✅ Use `@MockBean` to replace the service layer
- ✅ Test validation failure responses (400 + fieldErrors)
- ✅ Test exception handler responses (404, 409) through MockMvc
- ✅ Combine `verify()` with MockMvc to check service interactions

---

## 💡 What I Learned Today

### 1. @WebMvcTest vs @SpringBootTest vs @ExtendWith(MockitoExtension)

| | `@ExtendWith(MockitoExtension)` | `@WebMvcTest` | `@SpringBootTest` |
|---|---|---|---|
| What loads | Nothing (pure Java) | Web layer only | Full app context |
| Speed | Milliseconds | ~1 second | 3–10 seconds |
| Use for | Service + utility tests | Controller tests | Integration tests |
| DB needed | No | No | Yes (or mock) |
| MockMvc | No | Yes (auto-injected) | Yes (with config) |

### 2. @Mock vs @MockBean

| | `@Mock` (Day 2) | `@MockBean` (Day 3) |
|---|---|---|
| Context | No Spring context | Inside Spring context |
| Use with | `@InjectMocks` | `@WebMvcTest` / `@SpringBootTest` |
| Registered as bean | No | Yes — Spring injects it |
| Behaviour | Same — `when().thenReturn()` works identically |

### 3. MockMvc Request Building

```java
mockMvc.perform(
    post("/api/tasks")                        // HTTP method + URL
        .contentType(MediaType.APPLICATION_JSON)  // Content-Type header
        .content(jsonBody)                        // request body
        .param("status", "DONE")                 // query param
        .header("Authorization", "Bearer token") // custom header
)
```

---

## 💻 Code Examples

### Setup

```java
@WebMvcTest({TaskController.class, GlobalExceptionHandler.class})
class TaskControllerTest {

    @Autowired MockMvc mockMvc;           // simulates HTTP — auto-injected
    @MockBean  TaskService taskService;   // replaces real service in Spring context
    @Autowired ObjectMapper objectMapper; // Java → JSON converter
}
```

### GET — 200 + JSON body assertions

```java
@Test
void getById_returns200_whenFound() throws Exception {
    when(taskService.getById(1L)).thenReturn(sampleTask);

    mockMvc.perform(get("/api/tasks/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.title").value("Fix login bug"))
        .andExpect(jsonPath("$.status").value("TODO"));
}
```

### POST — 201 Created + Location header

```java
@Test
void create_returns201_withValidBody() throws Exception {
    when(taskService.create(any())).thenReturn(sampleTask);

    mockMvc.perform(post("/api/tasks")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                { "title": "Fix login bug", "ownerEmail": "alice@example.com" }
            """))
        .andExpect(status().isCreated())        // 201
        .andExpect(header().exists("Location")) // Location: /api/tasks/1
        .andExpect(jsonPath("$.id").value(1));
}
```

### Validation Failure — 400 + fieldErrors

```java
@Test
void create_returns400_whenTitleBlank() throws Exception {
    mockMvc.perform(post("/api/tasks")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{}"))                     // empty body — all fields missing
        .andExpect(status().isBadRequest())     // 400
        .andExpect(jsonPath("$.fieldErrors.title").value("Title is required"))
        .andExpect(jsonPath("$.fieldErrors.ownerEmail").value("Owner email is required"));
    // Service was NEVER called — validation stopped the request
    verify(taskService, never()).create(any());
}
```

### DELETE — 204 No Content

```java
@Test
void delete_returns204() throws Exception {
    doNothing().when(taskService).delete(1L);  // void method stub

    mockMvc.perform(delete("/api/tasks/1"))
        .andExpect(status().isNoContent());    // 204 — no body

    verify(taskService, times(1)).delete(1L);
}
```

---

## 📋 Test Summary

| Test | Concept Demonstrated |
|------|---------------------|
| `getAll_returns200WithTaskList` | GET + `jsonPath` array + `hasSize()` |
| `getAll_returns200WithEmptyList` | Empty list response |
| `getById_returns200_whenFound` | GET single item + field assertions |
| `getById_returns404_whenNotFound` | Exception handler → 404 |
| `create_returns201_withValidBody` | POST + 201 + Location header |
| `create_returns400_whenTitleBlank` | `@Valid` → 400 + fieldErrors |
| `create_returns400_withAllFieldErrors` | Multiple validation errors at once |
| `create_returns400_whenEmailInvalid` | Specific field error message |
| `create_returns400_whenTitleTooShort` | @Size validation |
| `updateStatus_returns200_withUpdatedTask` | PATCH + query param |
| `delete_returns204` | DELETE + 204 + `doNothing()` |
| `delete_returns404_whenNotFound` | DELETE + exception handler |
| `getAll_callsServiceOnce` | `verify()` in MockMvc context |
| `create_callsServiceCreate` | `verify()` with `any(Class)` |

---

## ❌ Common Mistakes

| Mistake | Fix |
|---------|-----|
| Using `@Mock` instead of `@MockBean` | `@Mock` is not a Spring bean — controller can't get it injected |
| Forgetting `.contentType(APPLICATION_JSON)` on POST | Spring rejects the request — 415 Unsupported Media Type |
| `jsonPath("$.title")` on an array response | Arrays need `$[0].title` for the first element |
| Using `@SpringBootTest` for controller tests | Too slow and loads too much — use `@WebMvcTest` |
| Not including `GlobalExceptionHandler` in `@WebMvcTest` | Exception handler not loaded → exceptions return 500 instead of custom JSON |
| `doThrow()` vs `when().thenThrow()` | Use `doThrow()` for `void` methods, `when().thenThrow()` for non-void |

---

## ✅ Day 3 Checklist

### Setup
- [x] `@WebMvcTest(Controller.class, ExceptionHandler.class)`
- [x] `@MockBean` for the service layer
- [x] `@Autowired MockMvc mockMvc`
- [x] `@Autowired ObjectMapper objectMapper`

### GET Tests
- [x] 200 with list response + `hasSize()`
- [x] 200 with single item + all field assertions
- [x] 404 via exception handler

### POST Tests
- [x] 201 Created + Location header
- [x] 400 with blank required field
- [x] 400 with all fields missing
- [x] 400 with invalid email format

### PATCH / DELETE Tests
- [x] PATCH with `?status=DONE` query param
- [x] DELETE → 204 No Content
- [x] DELETE → 404 when not found

### Verification
- [x] `verify()` on service from MockMvc test
- [x] `doNothing()` for void method stubs
- [x] `doThrow()` for void method exception stubs

---

**Date**: August 9, 2026
**Status**: ✅ Week 7, Day 3 Complete!
**Next**: Day 4 — `@DataJpaTest` (test the repository layer against a real in-memory DB)

> *"@WebMvcTest proves the HTTP contract. The controller is the door — test that the door opens the right way."*
