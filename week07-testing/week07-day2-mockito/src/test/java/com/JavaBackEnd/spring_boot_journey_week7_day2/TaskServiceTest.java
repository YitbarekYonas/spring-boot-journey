package com.JavaBackEnd.spring_boot_journey_week7_day2;

import com.JavaBackEnd.spring_boot_journey_week7_day2.exception.TaskNotFoundException;
import com.JavaBackEnd.spring_boot_journey_week7_day2.model.Task;
import com.JavaBackEnd.spring_boot_journey_week7_day2.repository.TaskRepository;
import com.JavaBackEnd.spring_boot_journey_week7_day2.service.TaskService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

// ── @ExtendWith(MockitoExtension.class) ───────────────────────────────────
// Activates Mockito annotations (@Mock, @InjectMocks) in this test class.
// Without this, @Mock fields would be null — nothing would work.
// This is the ONLY annotation needed. No @SpringBootTest, no context.
@ExtendWith(MockitoExtension.class)
@DisplayName("TaskService (Mockito)")
class TaskServiceTest {

    // ── @Mock ─────────────────────────────────────────────────────────────
    // Creates a fake TaskRepository — NOT the real one.
    // This fake does NOTHING by default (returns null/empty/0).
    // You tell it what to return using when().thenReturn().
    @Mock
    private TaskRepository taskRepository;

    // ── @InjectMocks ──────────────────────────────────────────────────────
    // Creates a REAL TaskService and injects the @Mock above into it.
    // So TaskService thinks it has a real repository, but it's actually a mock.
    // This is the object we're testing.
    @InjectMocks
    private TaskService taskService;

    // ── Helper: a reusable task object ────────────────────────────────────
    private Task sampleTask;

    @BeforeEach
    void setUp() {
        sampleTask = Task.builder()
                .id(1L)
                .title("Fix login bug")
                .ownerEmail("alice@example.com")
                .status(Task.Status.TODO)
                .priority(Task.Priority.HIGH)
                .build();
    }

    // ════════════════════════════════════════════════════════════════════════
    // SECTION 1 — when().thenReturn()  (stubbing)
    // "When the mock is called with these arguments, return this value."
    // ════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("getById() returns task when found")
    void getById_returnsTask_whenFound() {

        // ARRANGE — tell the mock what to return
        // "When findById(1L) is called → return Optional.of(sampleTask)"
        when(taskRepository.findById(1L))
                .thenReturn(Optional.of(sampleTask));

        // ACT — call the real service method
        Task result = taskService.getById(1L);

        // ASSERT — verify the result is what we expected
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("Fix login bug");
        assertThat(result.getOwnerEmail()).isEqualTo("alice@example.com");
    }

    @Test
    @DisplayName("getByOwner() returns list of tasks for that owner")
    void getByOwner_returnsTasks() {
        Task task2 = Task.builder().id(2L).title("Write tests")
                .ownerEmail("alice@example.com").build();

        // Stub the repository to return a list
        when(taskRepository.findByOwnerEmail("alice@example.com"))
                .thenReturn(List.of(sampleTask, task2));

        List<Task> results = taskService.getByOwner("alice@example.com");

        assertThat(results).hasSize(2);
        assertThat(results).extracting(Task::getTitle)
                .containsExactly("Fix login bug", "Write tests");
    }

    @Test
    @DisplayName("getByOwner() returns empty list when owner has no tasks")
    void getByOwner_returnsEmptyList_whenNoTasks() {
        when(taskRepository.findByOwnerEmail("unknown@example.com"))
                .thenReturn(List.of());

        List<Task> results = taskService.getByOwner("unknown@example.com");

        assertThat(results).isEmpty();
    }

    @Test
    @DisplayName("countTasksForOwner() returns the correct count")
    void countTasksForOwner_returnsCount() {
        when(taskRepository.countByOwnerEmail("alice@example.com"))
                .thenReturn(5L);

        long count = taskService.countTasksForOwner("alice@example.com");

        assertThat(count).isEqualTo(5L);
    }

    // ════════════════════════════════════════════════════════════════════════
    // SECTION 2 — when().thenThrow()  (stubbing exceptions)
    // "When the mock is called → throw this exception instead of returning."
    // ════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("getById() throws TaskNotFoundException when not found")
    void getById_throwsException_whenNotFound() {

        // Stub findById to return empty — simulates "not in DB"
        when(taskRepository.findById(99L))
                .thenReturn(Optional.empty());

        // Service should throw TaskNotFoundException when repository returns empty
        TaskNotFoundException ex = assertThrows(
                TaskNotFoundException.class,
                () -> taskService.getById(99L)
        );

        assertThat(ex.getMessage()).contains("99");
    }

    @Test
    @DisplayName("create() throws IllegalArgumentException for duplicate title")
    void create_throwsException_forDuplicateTitle() {
        // Simulate: title already exists for this owner
        when(taskRepository.existsByTitleAndOwnerEmail("Fix login bug", "alice@example.com"))
                .thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> taskService.create(sampleTask));

        // CRITICAL: verify save() was NEVER called when duplicate detected
        verify(taskRepository, never()).save(any());
    }

    // ════════════════════════════════════════════════════════════════════════
    // SECTION 3 — verify()  (interaction verification)
    // "After the test, prove that the mock was called the right way."
    // ════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("create() calls save() exactly once with the correct task")
    void create_callsSave_once() {

        // Arrange: no duplicate exists
        when(taskRepository.existsByTitleAndOwnerEmail(anyString(), anyString()))
                .thenReturn(false);

        // Arrange: save() returns the task with an id assigned
        Task savedTask = Task.builder().id(1L).title("Fix login bug")
                .ownerEmail("alice@example.com").build();
        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        // Act
        taskService.create(sampleTask);

        // verify(mock, times(N)).method() — asserts the method was called N times
        verify(taskRepository, times(1)).save(sampleTask);
        // You can also write: verify(taskRepository).save(sampleTask);
        // (times(1) is the default when you don't specify)
    }

    @Test
    @DisplayName("delete() calls deleteById() when task exists")
    void delete_callsDeleteById_whenTaskExists() {
        when(taskRepository.existsById(1L)).thenReturn(true);

        taskService.delete(1L);

        // Verify deleteById was called with the correct id
        verify(taskRepository).deleteById(1L);
    }

    @Test
    @DisplayName("delete() throws exception and never calls deleteById when not found")
    void delete_throwsException_andNeverCallsDeleteById_whenNotFound() {
        when(taskRepository.existsById(99L)).thenReturn(false);

        assertThrows(TaskNotFoundException.class, () -> taskService.delete(99L));

        // never() — assert a method was NEVER called
        verify(taskRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("updateStatus() calls findById and save exactly once each")
    void updateStatus_callsFindByIdAndSave() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));
        when(taskRepository.save(any())).thenReturn(sampleTask);

        taskService.updateStatus(1L, Task.Status.IN_PROGRESS);

        verify(taskRepository, times(1)).findById(1L);
        verify(taskRepository, times(1)).save(sampleTask);
    }

    // ════════════════════════════════════════════════════════════════════════
    // SECTION 4 — ArgumentCaptor
    // "Capture what was actually passed to the mock, then assert on it."
    // Use when you need to inspect the exact object the service sent to the repo.
    // ════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("ArgumentCaptor: create() saves a task with status=TODO by default")
    void create_savesTaskWithDefaultStatus() {

        when(taskRepository.existsByTitleAndOwnerEmail(anyString(), anyString()))
                .thenReturn(false);
        when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);

        // Create the captor — it captures Task objects
        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);

        taskService.create(sampleTask);

        // Capture whatever was passed to save()
        verify(taskRepository).save(taskCaptor.capture());
        Task capturedTask = taskCaptor.getValue();

        // Now assert on the captured object
        assertThat(capturedTask.getTitle()).isEqualTo("Fix login bug");
        assertThat(capturedTask.getOwnerEmail()).isEqualTo("alice@example.com");
        assertThat(capturedTask.getStatus()).isEqualTo(Task.Status.TODO);
        assertThat(capturedTask.getPriority()).isEqualTo(Task.Priority.HIGH);
    }

    @Test
    @DisplayName("ArgumentCaptor: updateStatus() saves task with new status applied")
    void updateStatus_savesWithUpdatedStatus() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));
        when(taskRepository.save(any())).thenReturn(sampleTask);

        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);

        taskService.updateStatus(1L, Task.Status.DONE);

        verify(taskRepository).save(captor.capture());

        // The captured task should have the NEW status applied
        assertThat(captor.getValue().getStatus()).isEqualTo(Task.Status.DONE);
    }

    // ════════════════════════════════════════════════════════════════════════
    // SECTION 5 — verify() with atLeast(), atMost(), never()
    // ════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("verify: findById called at least once during updateStatus")
    void verifyAtLeast_findByIdCalled() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));
        when(taskRepository.save(any())).thenReturn(sampleTask);

        taskService.updateStatus(1L, Task.Status.IN_PROGRESS);

        verify(taskRepository, atLeast(1)).findById(1L);
        verify(taskRepository, atMost(1)).findById(1L);  // and at most once too
    }

    @Test
    @DisplayName("verify: no other repository methods called during getById")
    void verifyNoOtherInteractions_duringGetById() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));

        taskService.getById(1L);

        verify(taskRepository).findById(1L);

        // verifyNoMoreInteractions — asserts no OTHER methods were called on the mock
        // Useful to prove the service is not making unexpected extra DB calls
        verifyNoMoreInteractions(taskRepository);
    }

    // ════════════════════════════════════════════════════════════════════════
    // SECTION 6 — anyX() argument matchers
    // Use when you don't care about the exact value, only the type
    // ════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("any() matchers: stub without caring about exact argument value")
    void anyMatchers_stubbingWithoutExactValues() {
        // anyLong()   — any long value
        // anyString() — any String value
        // any(Class)  — any object of that class
        when(taskRepository.findById(anyLong()))
                .thenReturn(Optional.of(sampleTask));

        // Whether we call getById(1) or getById(99) — mock returns sampleTask
        Task result1 = taskService.getById(1L);
        Task result2 = taskService.getById(42L);

        assertThat(result1).isEqualTo(sampleTask);
        assertThat(result2).isEqualTo(sampleTask);

        // Verify findById was called twice total (once for each call above)
        verify(taskRepository, times(2)).findById(anyLong());
    }
}
