package com.JavaBackEnd.spring_boot_journey_week7_day3;

import com.JavaBackEnd.spring_boot_journey_week7_day3.controller.TaskController;
import com.JavaBackEnd.spring_boot_journey_week7_day3.dto.request.CreateTaskRequest;
import com.JavaBackEnd.spring_boot_journey_week7_day3.exception.GlobalExceptionHandler;
import com.JavaBackEnd.spring_boot_journey_week7_day3.model.Task;
import com.JavaBackEnd.spring_boot_journey_week7_day3.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// ── @WebMvcTest vs @SpringBootTest ────────────────────────────────────────
//
// @SpringBootTest loads the ENTIRE application context (web + service + repo + DB).
// That's slow (3-10 seconds) and tests too many things at once.
//
// @WebMvcTest loads ONLY the web layer:
//   ✅ Controllers         (TaskController)
//   ✅ Exception handlers  (GlobalExceptionHandler)
//   ✅ Filters / CORS
//   ✅ Jackson / ObjectMapper
//   ❌ Services            → replaced by @MockBean
//   ❌ Repositories        → not loaded at all
//   ❌ Database            → not loaded at all
//
// Result: tests start in ~1 second, test exactly one layer.
//
// @WebMvcTest(TaskController.class) — the argument limits to JUST this controller.
// Without it, all controllers in the project would be loaded.
@WebMvcTest({TaskController.class, GlobalExceptionHandler.class})
@DisplayName("TaskController (@WebMvcTest)")
class TaskControllerTest {

    // ── MockMvc ───────────────────────────────────────────────────────────
    // MockMvc simulates HTTP requests WITHOUT starting a real server.
    // It calls the controller directly through the Spring MVC infrastructure.
    // Auto-injected by @WebMvcTest.
    @Autowired
    private MockMvc mockMvc;

    // ── @MockBean ─────────────────────────────────────────────────────────
    // Creates a Mockito mock AND registers it as a Spring bean.
    // This is different from Day 2's @Mock:
    //   @Mock      → pure Mockito, no Spring context
    //   @MockBean  → Mockito mock inserted INTO the Spring context
    //
    // @WebMvcTest needs @MockBean here (not @Mock) because TaskController
    // is a real Spring bean that gets its TaskService injected by Spring.
    @MockBean
    private TaskService taskService;

    // ── ObjectMapper ─────────────────────────────────────────────────────
    // Converts Java objects to JSON strings for request bodies.
    // Auto-injected — uses the same Jackson config as the running application.
    @Autowired
    private ObjectMapper objectMapper;

    // ── Test data ─────────────────────────────────────────────────────────
    private Task sampleTask;

    @BeforeEach
    void setUp() {
        sampleTask = Task.builder()
                .id(1L)
                .title("Fix login bug")
                .description("JWT token not refreshing")
                .status(Task.Status.TODO)
                .priority(Task.Priority.HIGH)
                .ownerEmail("alice@example.com")
                .dueDate(LocalDate.now().plusDays(5))
                .createdAt(LocalDateTime.now())
                .build();
    }

    // ════════════════════════════════════════════════════════════════════════
    // SECTION 1 — GET requests: status codes and JSON body
    // ════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("GET /api/tasks → 200 with list of tasks")
    void getAll_returns200WithTaskList() throws Exception {
        Task task2 = Task.builder().id(2L).title("Write tests")
                .status(Task.Status.IN_PROGRESS).priority(Task.Priority.MEDIUM)
                .ownerEmail("bob@example.com").build();

        when(taskService.getAll()).thenReturn(List.of(sampleTask, task2));

        mockMvc.perform(get("/api/tasks")         // build the request
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())                   // prints full request/response to console
                .andExpect(status().isOk())        // assert HTTP 200
                .andExpect(jsonPath("$", hasSize(2)))              // array has 2 items
                .andExpect(jsonPath("$[0].id").value(1))           // first item id=1
                .andExpect(jsonPath("$[0].title").value("Fix login bug"))
                .andExpect(jsonPath("$[0].status").value("TODO"))
                .andExpect(jsonPath("$[1].title").value("Write tests"));
    }

    @Test
    @DisplayName("GET /api/tasks → 200 with empty list when no tasks exist")
    void getAll_returns200WithEmptyList() throws Exception {
        when(taskService.getAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)))
                .andExpect(content().json("[]"));
    }

    @Test
    @DisplayName("GET /api/tasks/1 → 200 with task detail")
    void getById_returns200_whenFound() throws Exception {
        when(taskService.getById(1L)).thenReturn(sampleTask);

        mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Fix login bug"))
                .andExpect(jsonPath("$.description").value("JWT token not refreshing"))
                .andExpect(jsonPath("$.status").value("TODO"))
                .andExpect(jsonPath("$.priority").value("HIGH"))
                .andExpect(jsonPath("$.ownerEmail").value("alice@example.com"));
    }

    // ════════════════════════════════════════════════════════════════════════
    // SECTION 2 — Exception handler responses tested through MockMvc
    // ════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("GET /api/tasks/99 → 404 when task not found")
    void getById_returns404_whenNotFound() throws Exception {

        // Service throws → GlobalExceptionHandler catches → returns 404 JSON
        when(taskService.getById(99L))
                .thenThrow(new com.JavaBackEnd.spring_boot_journey_week7_day3
                        .exception.GlobalExceptionHandler.ErrorResponse
                        .TaskNotFoundEx(99L));
        // Using the actual exception class from the exception package:
        doThrow(new RuntimeException("Task not found: id=99"))
                .when(taskService).getById(99L);

        mockMvc.perform(get("/api/tasks/99"))
                .andExpect(status().isNotFound());
    }

    // ════════════════════════════════════════════════════════════════════════
    // SECTION 3 — POST: valid body, invalid body, 201 Created
    // ════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("POST /api/tasks → 201 Created with valid body")
    void create_returns201_withValidBody() throws Exception {
        when(taskService.create(any())).thenReturn(sampleTask);

        // Build request body as JSON string using ObjectMapper
        String body = """
                {
                  "title": "Fix login bug",
                  "description": "JWT token not refreshing",
                  "priority": "HIGH",
                  "dueDate": "%s",
                  "ownerEmail": "alice@example.com"
                }
                """.formatted(LocalDate.now().plusDays(5));

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)  // Content-Type: application/json
                        .content(body))                           // request body
                .andDo(print())
                .andExpect(status().isCreated())                  // 201 Created
                .andExpect(header().exists("Location"))           // Location header set
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Fix login bug"));
    }

    @Test
    @DisplayName("POST /api/tasks → 400 when title is blank")
    void create_returns400_whenTitleBlank() throws Exception {
        String body = """
                {
                  "title": "",
                  "ownerEmail": "alice@example.com"
                }
                """;

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())               // 400 Bad Request
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.fieldErrors.title").exists()); // field error present
    }

    @Test
    @DisplayName("POST /api/tasks → 400 with all missing required fields")
    void create_returns400_withAllFieldErrors() throws Exception {
        // Empty JSON — all required fields missing
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.title").value("Title is required"))
                .andExpect(jsonPath("$.fieldErrors.ownerEmail").value("Owner email is required"));
    }

    @Test
    @DisplayName("POST /api/tasks → 400 when email format is invalid")
    void create_returns400_whenEmailInvalid() throws Exception {
        String body = """
                {
                  "title": "Fix login bug",
                  "ownerEmail": "not-an-email"
                }
                """;

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.ownerEmail").value("Owner email must be valid"));
    }

    @Test
    @DisplayName("POST /api/tasks → 400 when title is too short")
    void create_returns400_whenTitleTooShort() throws Exception {
        String body = """
                {
                  "title": "ab",
                  "ownerEmail": "alice@example.com"
                }
                """;

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.title")
                        .value("Title must be between 3 and 100 characters"));
    }

    // ════════════════════════════════════════════════════════════════════════
    // SECTION 4 — PATCH and DELETE
    // ════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("PATCH /api/tasks/1/status → 200 with updated status")
    void updateStatus_returns200_withUpdatedTask() throws Exception {
        Task updated = Task.builder().id(1L).title("Fix login bug")
                .status(Task.Status.DONE).priority(Task.Priority.HIGH)
                .ownerEmail("alice@example.com").build();

        when(taskService.updateStatus(1L, Task.Status.DONE)).thenReturn(updated);

        mockMvc.perform(patch("/api/tasks/1/status")
                        .param("status", "DONE"))          // ?status=DONE query param
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DONE"));
    }

    @Test
    @DisplayName("DELETE /api/tasks/1 → 204 No Content")
    void delete_returns204() throws Exception {
        doNothing().when(taskService).delete(1L);

        mockMvc.perform(delete("/api/tasks/1"))
                .andExpect(status().isNoContent());   // 204 — no body

        verify(taskService, times(1)).delete(1L);
    }

    @Test
    @DisplayName("DELETE /api/tasks/99 → 404 when task not found")
    void delete_returns404_whenNotFound() throws Exception {
        doThrow(new RuntimeException("Task not found: id=99"))
                .when(taskService).delete(99L);

        mockMvc.perform(delete("/api/tasks/99"))
                .andExpect(status().isNotFound());
    }

    // ════════════════════════════════════════════════════════════════════════
    // SECTION 5 — Verifying service interactions from controller tests
    // ════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("verify: getAll() calls taskService.getAll() exactly once")
    void getAll_callsServiceOnce() throws Exception {
        when(taskService.getAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk());

        // Same verify() from Day 2 — works with @MockBean too
        verify(taskService, times(1)).getAll();
    }

    @Test
    @DisplayName("verify: create() calls taskService.create() with correct data")
    void create_callsServiceCreate() throws Exception {
        when(taskService.create(any())).thenReturn(sampleTask);

        String body = """
                {
                  "title": "Fix login bug",
                  "ownerEmail": "alice@example.com",
                  "priority": "HIGH",
                  "dueDate": "%s"
                }
                """.formatted(LocalDate.now().plusDays(5));

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());

        // Service was called once with any CreateTaskRequest
        verify(taskService, times(1)).create(any(CreateTaskRequest.class));
    }
}
