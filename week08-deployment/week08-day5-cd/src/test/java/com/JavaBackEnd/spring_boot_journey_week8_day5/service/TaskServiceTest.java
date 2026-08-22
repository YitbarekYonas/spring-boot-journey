package com.JavaBackEnd.spring_boot_journey_week8_day5.service;

import com.JavaBackEnd.spring_boot_journey_week8_day5.dto.TaskRequest;
import com.JavaBackEnd.spring_boot_journey_week8_day5.dto.TaskResponse;
import com.JavaBackEnd.spring_boot_journey_week8_day5.exception.InvalidTaskStateException;
import com.JavaBackEnd.spring_boot_journey_week8_day5.exception.ResourceNotFoundException;
import com.JavaBackEnd.spring_boot_journey_week8_day5.model.Task;
import com.JavaBackEnd.spring_boot_journey_week8_day5.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

// ── Layer 1 of 4 — Mockito unit tests ───────────────────────────────────────
// No Spring context, no database, no HTTP. The repository is a pure mock:
// @Mock creates a fake TaskRepository, @InjectMocks wires it into a real
// TaskService instance. This is the fastest layer in the suite and the
// right place to test BUSINESS LOGIC in isolation — especially markAsDone,
// which has actual branching rules worth pinning down precisely.
@ExtendWith(MockitoExtension.class)
@DisplayName("TaskService (Mockito unit tests)")
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private Task existingTask;

    @BeforeEach
    void setUp() {
        existingTask = Task.builder()
                .id(1L)
                .title("Fix login bug")
                .status(Task.Status.TODO)
                .priority(Task.Priority.HIGH)
                .ownerEmail("alice@example.com")
                .dueDate(LocalDate.now().plusDays(5))
                .build();
    }

    // ── createTask ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("createTask() saves via repository and returns a mapped response")
    void createTask_savesAndReturnsResponse() {
        TaskRequest request = TaskRequest.builder()
                .title("New task")
                .status(Task.Status.TODO)
                .priority(Task.Priority.MEDIUM)
                .ownerEmail("bob@example.com")
                .build();

        Task savedEntity = Task.builder()
                .id(42L)
                .title("New task")
                .status(Task.Status.TODO)
                .priority(Task.Priority.MEDIUM)
                .ownerEmail("bob@example.com")
                .build();

        when(taskRepository.save(any(Task.class))).thenReturn(savedEntity);

        TaskResponse result = taskService.createTask(request);

        assertThat(result.getId()).isEqualTo(42L);
        assertThat(result.getTitle()).isEqualTo("New task");

        // Argument captor — verify EXACTLY what was passed to save(), not
        // just that save() was called with "something."
        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(captor.capture());
        assertThat(captor.getValue().getOwnerEmail()).isEqualTo("bob@example.com");
        assertThat(captor.getValue().getId()).isNull(); // never set an id ourselves
    }

    // ── getTaskById ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("getTaskById() returns a mapped response when found")
    void getTaskById_returnsResponse_whenFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));

        TaskResponse result = taskService.getTaskById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("Fix login bug");
    }

    @Test
    @DisplayName("getTaskById() throws ResourceNotFoundException when not found")
    void getTaskById_throwsNotFound_whenMissing() {
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.getTaskById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }

    // ── getAllTasks / getTasksByStatus ──────────────────────────────────────

    @Test
    @DisplayName("getAllTasks() maps every entity returned by the repository")
    void getAllTasks_mapsAllEntities() {
        Task second = Task.builder().id(2L).title("Second task")
                .status(Task.Status.DONE).priority(Task.Priority.LOW)
                .ownerEmail("carol@example.com").build();

        when(taskRepository.findAll()).thenReturn(List.of(existingTask, second));

        List<TaskResponse> result = taskService.getAllTasks();

        assertThat(result).hasSize(2)
                .extracting(TaskResponse::getTitle)
                .containsExactly("Fix login bug", "Second task");
    }

    @Test
    @DisplayName("getTasksByStatus() delegates to the correct repository method")
    void getTasksByStatus_delegatesToRepository() {
        when(taskRepository.findByStatus(Task.Status.TODO)).thenReturn(List.of(existingTask));

        List<TaskResponse> result = taskService.getTasksByStatus(Task.Status.TODO);

        assertThat(result).hasSize(1);
        verify(taskRepository).findByStatus(Task.Status.TODO);
        verify(taskRepository, never()).findAll(); // proves it filtered, didn't fetch everything
    }

    // ── updateTask ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("updateTask() overwrites fields and saves")
    void updateTask_overwritesFieldsAndSaves() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        TaskRequest update = TaskRequest.builder()
                .title("Fix login bug — updated")
                .status(Task.Status.IN_PROGRESS)
                .priority(Task.Priority.HIGH)
                .ownerEmail("alice@example.com")
                .build();

        TaskResponse result = taskService.updateTask(1L, update);

        assertThat(result.getTitle()).isEqualTo("Fix login bug — updated");
        assertThat(result.getStatus()).isEqualTo(Task.Status.IN_PROGRESS);
    }

    @Test
    @DisplayName("updateTask() throws ResourceNotFoundException for a missing id")
    void updateTask_throwsNotFound_whenMissing() {
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        TaskRequest update = TaskRequest.builder()
                .title("x").status(Task.Status.TODO).priority(Task.Priority.LOW)
                .ownerEmail("x@example.com").build();

        assertThatThrownBy(() -> taskService.updateTask(999L, update))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(taskRepository, never()).save(any());
    }

    // ── deleteTask ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("deleteTask() calls deleteById when the task exists")
    void deleteTask_deletes_whenExists() {
        when(taskRepository.existsById(1L)).thenReturn(true);

        taskService.deleteTask(1L);

        verify(taskRepository).deleteById(1L);
    }

    @Test
    @DisplayName("deleteTask() throws ResourceNotFoundException and never calls deleteById when missing")
    void deleteTask_throwsNotFound_whenMissing() {
        when(taskRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> taskService.deleteTask(999L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(taskRepository, never()).deleteById(anyLong());
    }

    // ── markAsDone — the real business rule ─────────────────────────────────

    @Test
    @DisplayName("markAsDone() transitions IN_PROGRESS -> DONE successfully")
    void markAsDone_transitionsFromInProgress() {
        existingTask.setStatus(Task.Status.IN_PROGRESS);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        TaskResponse result = taskService.markAsDone(1L);

        assertThat(result.getStatus()).isEqualTo(Task.Status.DONE);
    }

    @Test
    @DisplayName("markAsDone() rejects a task still in TODO — must pass through IN_PROGRESS")
    void markAsDone_rejectsFromTodo() {
        existingTask.setStatus(Task.Status.TODO);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));

        assertThatThrownBy(() -> taskService.markAsDone(1L))
                .isInstanceOf(InvalidTaskStateException.class)
                .hasMessageContaining("IN_PROGRESS");

        verify(taskRepository, never()).save(any());
    }

    @Test
    @DisplayName("markAsDone() rejects a task that is already DONE")
    void markAsDone_rejectsAlreadyDone() {
        existingTask.setStatus(Task.Status.DONE);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));

        assertThatThrownBy(() -> taskService.markAsDone(1L))
                .isInstanceOf(InvalidTaskStateException.class)
                .hasMessageContaining("already DONE");

        verify(taskRepository, never()).save(any());
    }
}
