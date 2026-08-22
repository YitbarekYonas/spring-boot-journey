package com.JavaBackEnd.spring_boot_journey_week8_day5.controller;

import com.JavaBackEnd.spring_boot_journey_week8_day5.dto.TaskRequest;
import com.JavaBackEnd.spring_boot_journey_week8_day5.dto.TaskResponse;
import com.JavaBackEnd.spring_boot_journey_week8_day5.exception.InvalidTaskStateException;
import com.JavaBackEnd.spring_boot_journey_week8_day5.exception.ResourceNotFoundException;
import com.JavaBackEnd.spring_boot_journey_week8_day5.model.Task;
import com.JavaBackEnd.spring_boot_journey_week8_day5.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// ── Layer 2 of 4 — @WebMvcTest ──────────────────────────────────────────────
// Loads ONLY the web layer: this controller, Jackson message converters,
// and the GlobalExceptionHandler. The service is @MockBean'd — its return
// values and exceptions are dictated by each test, so we're purely proving
// the HTTP CONTRACT: correct status codes, correct JSON shape, correct
// routing of path/query params, correct validation error format.
@WebMvcTest(TaskController.class)
@DisplayName("TaskController (@WebMvcTest)")
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService taskService;

    private TaskResponse sampleResponse() {
        return TaskResponse.builder()
                .id(1L)
                .title("Fix login bug")
                .status(Task.Status.TODO)
                .priority(Task.Priority.HIGH)
                .ownerEmail("alice@example.com")
                .dueDate(LocalDate.now().plusDays(5))
                .build();
    }

    private TaskRequest validRequest() {
        return TaskRequest.builder()
                .title("Fix login bug")
                .status(Task.Status.TODO)
                .priority(Task.Priority.HIGH)
                .ownerEmail("alice@example.com")
                .dueDate(LocalDate.now().plusDays(5))
                .build();
    }

    // ── POST /api/tasks — happy path ────────────────────────────────────────

    @Test
    @DisplayName("POST /api/tasks — 201 Created with Location header on valid input")
    void createTask_returns201_onValidInput() throws Exception {
        when(taskService.createTask(any(TaskRequest.class))).thenReturn(sampleResponse());

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/tasks/1"))
                .andExpect(jsonPath("$.title").value("Fix login bug"))
                .andExpect(jsonPath("$.status").value("TODO"));
    }

    // ── POST /api/tasks — validation failure ────────────────────────────────

    @Test
    @DisplayName("POST /api/tasks — 400 with field errors when title is blank")
    void createTask_returns400_whenTitleBlank() throws Exception {
        TaskRequest invalid = validRequest();
        invalid.setTitle("");

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors").isArray())
                .andExpect(jsonPath("$.fieldErrors[0]").value("Title is required"));
    }

    @Test
    @DisplayName("POST /api/tasks — 400 when ownerEmail is not a valid email")
    void createTask_returns400_whenEmailInvalid() throws Exception {
        TaskRequest invalid = validRequest();
        invalid.setOwnerEmail("not-an-email");

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    // ── GET /api/tasks/{id} ──────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/tasks/{id} — 200 with task body when found")
    void getTaskById_returns200_whenFound() throws Exception {
        when(taskService.getTaskById(1L)).thenReturn(sampleResponse());

        mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.ownerEmail").value("alice@example.com"));
    }

    @Test
    @DisplayName("GET /api/tasks/{id} — 404 with structured error body when not found")
    void getTaskById_returns404_whenMissing() throws Exception {
        when(taskService.getTaskById(999L))
                .thenThrow(new ResourceNotFoundException("Task not found with id: 999"));

        mockMvc.perform(get("/api/tasks/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Task not found with id: 999"))
                .andExpect(jsonPath("$.path").value("/api/tasks/999"));
    }

    // ── GET /api/tasks — with and without query param ───────────────────────

    @Test
    @DisplayName("GET /api/tasks — no status param calls getAllTasks()")
    void getAllTasks_withoutStatusParam_callsGetAllTasks() throws Exception {
        when(taskService.getAllTasks()).thenReturn(List.of(sampleResponse()));

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("GET /api/tasks?status=TODO — routes to getTasksByStatus(TODO)")
    void getAllTasks_withStatusParam_callsGetTasksByStatus() throws Exception {
        when(taskService.getTasksByStatus(eq(Task.Status.TODO)))
                .thenReturn(List.of(sampleResponse()));

        mockMvc.perform(get("/api/tasks").param("status", "TODO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("TODO"));
    }

    // ── PUT /api/tasks/{id} ──────────────────────────────────────────────────

    @Test
    @DisplayName("PUT /api/tasks/{id} — 200 with updated body")
    void updateTask_returns200_onValidInput() throws Exception {
        TaskResponse updated = sampleResponse();
        updated.setTitle("Fix login bug — updated");

        when(taskService.updateTask(eq(1L), any(TaskRequest.class))).thenReturn(updated);

        mockMvc.perform(put("/api/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Fix login bug — updated"));
    }

    // ── PATCH /api/tasks/{id}/done — happy path + business-rule conflict ────

    @Test
    @DisplayName("PATCH /api/tasks/{id}/done — 200 when transition is valid")
    void markAsDone_returns200_onValidTransition() throws Exception {
        TaskResponse done = sampleResponse();
        done.setStatus(Task.Status.DONE);

        when(taskService.markAsDone(1L)).thenReturn(done);

        mockMvc.perform(patch("/api/tasks/1/done"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DONE"));
    }

    @Test
    @DisplayName("PATCH /api/tasks/{id}/done — 409 Conflict when the business rule is violated")
    void markAsDone_returns409_onInvalidTransition() throws Exception {
        when(taskService.markAsDone(1L))
                .thenThrow(new InvalidTaskStateException(
                        "Task 1 must be IN_PROGRESS before it can be marked DONE"));

        mockMvc.perform(patch("/api/tasks/1/done"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value(
                        "Task 1 must be IN_PROGRESS before it can be marked DONE"));
    }

    // ── DELETE /api/tasks/{id} ───────────────────────────────────────────────

    @Test
    @DisplayName("DELETE /api/tasks/{id} — 204 No Content on success")
    void deleteTask_returns204() throws Exception {
        mockMvc.perform(delete("/api/tasks/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/tasks/{id} — 404 when the task doesn't exist")
    void deleteTask_returns404_whenMissing() throws Exception {
        org.mockito.Mockito.doThrow(new ResourceNotFoundException("Task not found with id: 999"))
                .when(taskService).deleteTask(999L);

        mockMvc.perform(delete("/api/tasks/999"))
                .andExpect(status().isNotFound());
    }
}
