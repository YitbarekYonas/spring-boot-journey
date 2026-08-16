package com.JavaBackEnd.spring_boot_journey_week7_day6;

import com.JavaBackEnd.spring_boot_journey_week7_day6.dto.TaskRequest;
import com.JavaBackEnd.spring_boot_journey_week7_day6.dto.TaskResponse;
import com.JavaBackEnd.spring_boot_journey_week7_day6.exception.ErrorResponse;
import com.JavaBackEnd.spring_boot_journey_week7_day6.model.Task;
import com.JavaBackEnd.spring_boot_journey_week7_day6.repository.TaskRepository;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

// ── The top of the testing pyramid ──────────────────────────────────────────
//
// Day 2 (Mockito):       service logic, repository mocked            — fastest
// Day 3 (@WebMvcTest):   HTTP contract, service mocked                — fast
// Day 4 (@DataJpaTest):  repository queries, real H2                  — fast
// Day 5 (Testcontainers): repository queries, real Postgres           — slower
// Day 6 (this file):     REAL HTTP call → controller → service →
//                        repository → REAL Postgres, nothing mocked   — slowest
//
// This is the only layer in the whole suite that proves the actual wiring:
// that @Valid is actually applied, that GlobalExceptionHandler actually
// intercepts exceptions thrown from the real service, that ResponseEntity
// status codes actually reach the HTTP client, and that a JSON request
// body really does deserialize into the DTO we expect.
//
// webEnvironment = RANDOM_PORT starts the app on an actual embedded
// Tomcat/Netty instance bound to a random free port (avoids port
// conflicts when tests run in parallel or in CI). TestRestTemplate then
// makes REAL HTTP calls against it — not simulated ones like MockMvc.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@DisplayName("Task API — Full End-to-End Integration Tests")
class TaskApiEndToEndTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("taskdb_e2e")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureDatasource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    // ── LocalServerPort + TestRestTemplate ──────────────────────────────────
    // @LocalServerPort injects the actual random port the embedded server
    // bound to. TestRestTemplate is Spring's test-friendly wrapper around
    // RestTemplate — auto-configured by @SpringBootTest, handles relative
    // URLs against the running server for us.
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

    // ── Test data builder ────────────────────────────────────────────────────
    // A small fixture helper (Day 6 concept: "test data builders to avoid
    // duplicated setup code") — every test that needs a valid request body
    // starts from this and overrides only what it cares about.
    private TaskRequest validTaskRequest() {
        return TaskRequest.builder()
                .title("Fix login bug")
                .description("JWT token not refreshing correctly")
                .status(Task.Status.TODO)
                .priority(Task.Priority.HIGH)
                .dueDate(LocalDate.now().plusDays(5))
                .ownerEmail("alice@example.com")
                .build();
    }

    // ════════════════════════════════════════════════════════════════════════
    // FLOW 1 — create → fetch (the core "does the whole stack work" proof)
    // ════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("POST /api/tasks then GET /api/tasks/{id} — full create-then-fetch flow")
    void createThenFetchTask_worksEndToEnd() {
        // Step 1: create via real HTTP POST
        ResponseEntity<TaskResponse> createResponse = restTemplate.postForEntity(
                baseUrl(), validTaskRequest(), TaskResponse.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createResponse.getHeaders().getLocation()).isNotNull();

        TaskResponse created = createResponse.getBody();
        assertThat(created).isNotNull();
        assertThat(created.getId()).isNotNull();
        assertThat(created.getTitle()).isEqualTo("Fix login bug");

        // Step 2: fetch it back via real HTTP GET, proving it actually
        // persisted to (and can be read from) the real Postgres container —
        // not just held in memory somewhere.
        ResponseEntity<TaskResponse> fetchResponse = restTemplate.getForEntity(
                baseUrl() + "/" + created.getId(), TaskResponse.class);

        assertThat(fetchResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(fetchResponse.getBody()).isNotNull();
        assertThat(fetchResponse.getBody().getId()).isEqualTo(created.getId());
        assertThat(fetchResponse.getBody().getOwnerEmail()).isEqualTo("alice@example.com");

        // Cross-check directly against the DB, bypassing the API entirely —
        // proves the HTTP layer isn't lying to us about what got persisted.
        assertThat(taskRepository.findById(created.getId())).isPresent();
    }

    // ════════════════════════════════════════════════════════════════════════
    // FLOW 2 — create → update → verify change persisted
    // ════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("POST then PUT /api/tasks/{id} — update flow persists changes")
    void createThenUpdateTask_persistsChanges() {
        TaskResponse created = restTemplate.postForEntity(
                baseUrl(), validTaskRequest(), TaskResponse.class).getBody();

        TaskRequest updateRequest = TaskRequest.builder()
                .title("Fix login bug — RESOLVED")
                .description(created.getDescription())
                .status(Task.Status.DONE)
                .priority(Task.Priority.HIGH)
                .dueDate(created.getDueDate())
                .ownerEmail(created.getOwnerEmail())
                .build();

        restTemplate.put(baseUrl() + "/" + created.getId(), updateRequest);

        ResponseEntity<TaskResponse> fetchResponse = restTemplate.getForEntity(
                baseUrl() + "/" + created.getId(), TaskResponse.class);

        assertThat(fetchResponse.getBody().getTitle()).isEqualTo("Fix login bug — RESOLVED");
        assertThat(fetchResponse.getBody().getStatus()).isEqualTo(Task.Status.DONE);
    }

    // ════════════════════════════════════════════════════════════════════════
    // FLOW 3 — create multiple → filter by status → delete → verify gone
    // ════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Create several tasks, filter by status, delete one, verify removal — full flow")
    void createFilterAndDeleteTasks_worksEndToEnd() {
        TaskResponse todoTask = restTemplate.postForEntity(
                baseUrl(), validTaskRequest(), TaskResponse.class).getBody();

        TaskRequest doneRequest = validTaskRequest();
        doneRequest.setTitle("Already finished task");
        doneRequest.setStatus(Task.Status.DONE);
        restTemplate.postForEntity(baseUrl(), doneRequest, TaskResponse.class);

        // Filter by status via query param — real HTTP GET with ?status=TODO
        ResponseEntity<TaskResponse[]> filtered = restTemplate.getForEntity(
                baseUrl() + "?status=TODO", TaskResponse[].class);

        assertThat(filtered.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<TaskResponse> todoTasks = List.of(filtered.getBody());
        assertThat(todoTasks).hasSize(1);
        assertThat(todoTasks.get(0).getId()).isEqualTo(todoTask.getId());

        // Delete the TODO task via real HTTP DELETE
        restTemplate.delete(baseUrl() + "/" + todoTask.getId());

        ResponseEntity<ErrorResponse> afterDelete = restTemplate.getForEntity(
                baseUrl() + "/" + todoTask.getId(), ErrorResponse.class);

        assertThat(afterDelete.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(taskRepository.existsById(todoTask.getId())).isFalse();
    }

    // ════════════════════════════════════════════════════════════════════════
    // Edge cases — proves GlobalExceptionHandler is really wired into the
    // real HTTP pipeline, not just unit-tested in isolation.
    // ════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("GET /api/tasks/{id} for a non-existent id returns 404 with structured error body")
    void getNonExistentTask_returns404WithErrorBody() {
        ResponseEntity<ErrorResponse> response = restTemplate.getForEntity(
                baseUrl() + "/999999", ErrorResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(404);
        assertThat(response.getBody().getPath()).isEqualTo("/api/tasks/999999");
    }

    @Test
    @DisplayName("POST /api/tasks with a blank title returns 400 with field-level validation errors")
    void createTaskWithBlankTitle_returns400WithFieldErrors() {
        TaskRequest invalidRequest = validTaskRequest();
        invalidRequest.setTitle("");

        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity(
                baseUrl(), invalidRequest, ErrorResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getFieldErrors()).isNotEmpty();
    }

    @Test
    @DisplayName("POST /api/tasks with an invalid email returns 400")
    void createTaskWithInvalidEmail_returns400() {
        TaskRequest invalidRequest = validTaskRequest();
        invalidRequest.setOwnerEmail("not-an-email");

        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity(
                baseUrl(), invalidRequest, ErrorResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
