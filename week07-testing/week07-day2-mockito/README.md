# Week 7, Day 2: Mockito — Mock the Repository, Test the Service

[![Status](https://img.shields.io/badge/Status-Completed-brightgreen.svg)]()
[![Day](https://img.shields.io/badge/Day-2-blue.svg)]()
[![Topic](https://img.shields.io/badge/Topic-Mockito-orange.svg)]()

> **"Mock the dependency, test the logic. No database, no problem."**

---

## 🎯 Learning Objectives

- ✅ Understand `@Mock` vs `@InjectMocks` — what each creates
- ✅ Stub return values with `when().thenReturn()`
- ✅ Stub exceptions with `when().thenThrow()`
- ✅ Verify interactions with `verify()`, `times()`, `never()`
- ✅ Capture arguments with `ArgumentCaptor`
- ✅ Use `any()`, `anyLong()`, `anyString()` argument matchers
- ✅ Test the full service layer with zero database involvement

---

## 💡 What I Learned Today

### 1. The Three Mockito Building Blocks

| Annotation | What It Does |
|------------|--------------|
| `@ExtendWith(MockitoExtension.class)` | Activates Mockito annotations in the test class |
| `@Mock` | Creates a fake object — does nothing by default |
| `@InjectMocks` | Creates the REAL class and injects all `@Mock` fields into it |

### 2. The Arrange-Act-Assert Pattern with Mockito

```
ARRANGE: when(mock.method(args)).thenReturn(value)   ← set up the fake
ACT:     realService.doSomething()                   ← call what you're testing
ASSERT:  assertThat(result)...                       ← check the output
VERIFY:  verify(mock).method(args)                   ← check the interaction
```

### 3. Mockito Cheat Sheet

| Method | Purpose | Example |
|--------|---------|---------|
| `when().thenReturn()` | Stub a return value | `when(repo.findById(1L)).thenReturn(Optional.of(task))` |
| `when().thenThrow()` | Stub an exception | `when(repo.findById(99L)).thenThrow(new RuntimeException())` |
| `verify(mock).method()` | Assert called once | `verify(repo).save(task)` |
| `verify(mock, times(2))` | Assert called N times | `verify(repo, times(2)).findById(anyLong())` |
| `verify(mock, never())` | Assert never called | `verify(repo, never()).deleteById(anyLong())` |
| `verify(mock, atLeast(1))` | Assert called at least N times | `verify(repo, atLeast(1)).findById(1L)` |
| `ArgumentCaptor.capture()` | Capture argument passed to mock | See example below |
| `any(Task.class)` | Match any Task object | `when(repo.save(any(Task.class))).thenReturn(task)` |
| `verifyNoMoreInteractions()` | No unexpected extra calls | `verifyNoMoreInteractions(repo)` |

---

## 💻 Code Examples

### Setup — @Mock and @InjectMocks

```java
@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;  // fake — Mockito creates this

    @InjectMocks
    private TaskService taskService;        // real — Mockito injects the mock above

    // No @SpringBootTest, no context, no DB — pure unit test
}
```

### when().thenReturn() — Stubbing a Return Value

```java
@Test
void getById_returnsTask_whenFound() {
    // ARRANGE — tell the fake what to return
    when(taskRepository.findById(1L))
        .thenReturn(Optional.of(sampleTask));

    // ACT — call the real service
    Task result = taskService.getById(1L);

    // ASSERT — the service processed the mocked return correctly
    assertThat(result.getTitle()).isEqualTo("Fix login bug");
}
```

### when().thenReturn(false) — Triggering the Not-Found Branch

```java
@Test
void getById_throwsException_whenNotFound() {
    when(taskRepository.findById(99L))
        .thenReturn(Optional.empty());  // simulate "not in DB"

    assertThrows(TaskNotFoundException.class,
        () -> taskService.getById(99L));
}
```

### verify() + never() — Proving What DID and DIDN'T Happen

```java
@Test
void create_throwsException_forDuplicateTitle() {
    when(taskRepository.existsByTitleAndOwnerEmail("Fix login bug", "alice@example.com"))
        .thenReturn(true);  // duplicate found

    assertThrows(IllegalArgumentException.class,
        () -> taskService.create(sampleTask));

    // Prove the service stopped before calling save()
    verify(taskRepository, never()).save(any());
}
```

### ArgumentCaptor — Inspect What the Service Passed to the Repo

```java
@Test
void create_savesTaskWithCorrectStatus() {
    when(taskRepository.existsByTitleAndOwnerEmail(anyString(), anyString()))
        .thenReturn(false);
    when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);

    ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);

    taskService.create(sampleTask);

    verify(taskRepository).save(captor.capture());
    Task captured = captor.getValue();  // the actual object passed to save()

    assertThat(captured.getTitle()).isEqualTo("Fix login bug");
    assertThat(captured.getStatus()).isEqualTo(Task.Status.TODO);
}
```

---

## 📋 Test Summary

| Test | Mockito Concept |
|------|----------------|
| `getById_returnsTask_whenFound` | `when().thenReturn()` |
| `getByOwner_returnsTasks` | stubbing list return |
| `getByOwner_returnsEmptyList` | stubbing empty result |
| `countTasksForOwner_returnsCount` | stubbing primitive return |
| `getById_throwsException_whenNotFound` | `Optional.empty()` stub |
| `create_throwsException_forDuplicateTitle` | `thenReturn(true)` + `verify(never())` |
| `create_callsSave_once` | `verify(times(1))` |
| `delete_callsDeleteById_whenExists` | `verify()` on void method |
| `delete_throwsException_andNeverCallsDelete` | `thenReturn(false)` + `never()` |
| `updateStatus_callsFindByIdAndSave` | multiple `verify()` calls |
| `create_savesTaskWithDefaultStatus` | `ArgumentCaptor` |
| `updateStatus_savesWithUpdatedStatus` | `ArgumentCaptor` on mutation |
| `verifyAtLeast_findByIdCalled` | `atLeast()` / `atMost()` |
| `verifyNoOtherInteractions_duringGetById` | `verifyNoMoreInteractions()` |
| `anyMatchers_stubbingWithoutExactValues` | `anyLong()` / `any()` |

---

## ❌ Common Mistakes

| Mistake | Fix |
|---------|-----|
| Forgetting `@ExtendWith(MockitoExtension.class)` | `@Mock` fields stay null without it |
| Using `@SpringBootTest` with Mockito | Unnecessary — loads full context, slow |
| Verifying before acting | `verify()` always comes AFTER the method call |
| `when(void method).thenReturn()` | Void methods use `doNothing().when(mock).method()` |
| Stubbing a method but not asserting the result | Stub + assert + verify = complete test |
| Over-verifying everything | Only verify interactions that matter to the behavior being tested |

---

## ✅ Day 2 Checklist

### Setup
- [x] `@ExtendWith(MockitoExtension.class)` on test class
- [x] `@Mock` for the dependency (repository)
- [x] `@InjectMocks` for the class under test (service)
- [x] `@BeforeEach` for reusable test data

### Stubbing
- [x] `when().thenReturn()` — stub found case
- [x] `when().thenReturn(Optional.empty())` — stub not-found case
- [x] `when().thenReturn(true/false)` — stub boolean results
- [x] `any()`, `anyLong()`, `anyString()` matchers

### Verification
- [x] `verify(mock).method()` — called exactly once
- [x] `verify(mock, times(N))` — called N times
- [x] `verify(mock, never())` — never called
- [x] `verify(mock, atLeast(N))` / `atMost(N)`
- [x] `verifyNoMoreInteractions(mock)`

### ArgumentCaptor
- [x] `ArgumentCaptor.forClass()` declared
- [x] `captor.capture()` inside `verify()`
- [x] `captor.getValue()` to get the captured object
- [x] AssertJ assertions on the captured value

---

**Date**: August 9, 2026
**Status**: ✅ Week 7, Day 2 Complete!
**Next**: Day 3 — `@WebMvcTest` (test the controller layer with MockMvc)

> *"Mockito lets you test one thing at a time. Mock the dependencies, isolate the logic."*
