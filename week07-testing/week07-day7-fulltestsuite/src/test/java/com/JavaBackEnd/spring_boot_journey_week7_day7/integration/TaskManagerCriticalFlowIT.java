package com.JavaBackEnd.spring_boot_journey_week7_day7.integration;

import com.JavaBackEnd.spring_boot_journey_week7_day7.dto.TaskRequest;
import com.JavaBackEnd.spring_boot_journey_week7_day7.dto.TaskResponse;
import com.JavaBackEnd.spring_boot_journey_week7_day7.model.Task;
import com.JavaBackEnd.spring_boot_journey_week7_day7.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

// ── Layer 4 of 4 — the ONE Testcontainers-backed critical-flow test ────────
// The mini-project spec calls for exactly one of these: not to re-prove
// every endpoint again (Days 3–6 and the layers above already did that at
// their appropriate speed), but to prove the single most important user
// journey works with NOTHING mocked, against a real Postgres container:
//
//   create a task → move it through its real lifecycle (TODO -> IN_PROGRESS
//   -> DONE, exercising the actual business rule from TaskService) → confirm
//   the final state is correctly persisted.
//
// This is deliberately the most expensive test in the whole suite (real
// HTTP + real embedded server + real Docker container) — which is exactly
// why the suite has only one of it instead of dozens.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@DisplayName("Task Manager — Critical Flow (Testcontainers E2E)")
class TaskManagerCriticalFlowIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("taskdb_it")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureDatasource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TaskRepository taskRepository;

    private String baseUrl() {
        return "http://localhost:" + port + "/api/tasks";
    }

    @BeforeEach
    void cleanDatabase() {
        taskRepository.deleteAll();
    }

    @Test
    @DisplayName("Create a task, walk it through its full lifecycle to DONE, verify final persisted state")
    void fullTaskLifecycle_createThroughDone_persistsCorrectly() {
        // ── Step 1: create (TODO) ────────────────────────────────────────────
        TaskRequest createRequest = TaskRequest.builder()
                .title("Ship the Week 7 test suite")
                .description("Complete the capstone mini-project")
                .status(Task.Status.TODO)
                .priority(Task.Priority.HIGH)
                .dueDate(LocalDate.now().plusDays(1))
                .ownerEmail("student@example.com")
                .build();

        ResponseEntity<TaskResponse> createResponse = restTemplate.postForEntity(
                baseUrl(), createRequest, TaskResponse.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Long taskId = createResponse.getBody().getId();
        assertThat(taskId).isNotNull();

        // ── Step 2: attempting DONE straight from TODO must be rejected ──────
        // Real business rule from TaskService, exercised through the real
        // HTTP layer and the real GlobalExceptionHandler — proving the rule
        // holds end-to-end, not just in the Mockito unit test.
        ResponseEntity<String> rejectedResponse = restTemplate.exchange(
                baseUrl() + "/" + taskId + "/done",
                org.springframework.http.HttpMethod.PATCH,
                null,
                String.class);
        assertThat(rejectedResponse.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

        // ── Step 3: move to IN_PROGRESS via update ───────────────────────────
        TaskRequest progressRequest = TaskRequest.builder()
                .title(createRequest.getTitle())
                .description(createRequest.getDescription())
                .status(Task.Status.IN_PROGRESS)
                .priority(createRequest.getPriority())
                .dueDate(createRequest.getDueDate())
                .ownerEmail(createRequest.getOwnerEmail())
                .build();

        restTemplate.put(baseUrl() + "/" + taskId, progressRequest);

        ResponseEntity<TaskResponse> afterProgress = restTemplate.getForEntity(
                baseUrl() + "/" + taskId, TaskResponse.class);
        assertThat(afterProgress.getBody().getStatus()).isEqualTo(Task.Status.IN_PROGRESS);

        // ── Step 4: now DONE succeeds ─────────────────────────────────────────
        ResponseEntity<TaskResponse> doneResponse = restTemplate.exchange(
                baseUrl() + "/" + taskId + "/done",
                org.springframework.http.HttpMethod.PATCH,
                null,
                TaskResponse.class);

        assertThat(doneResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(doneResponse.getBody().getStatus()).isEqualTo(Task.Status.DONE);

        // ── Step 5: verify the FINAL state directly against real Postgres ────
        // Bypasses the API entirely — the ultimate proof that what the HTTP
        // layer reported actually landed in the database.
        Task persisted = taskRepository.findById(taskId).orElseThrow();
        assertThat(persisted.getStatus()).isEqualTo(Task.Status.DONE);
        assertThat(persisted.getTitle()).isEqualTo("Ship the Week 7 test suite");
    }
}
