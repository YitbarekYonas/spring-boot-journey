package com.JavaBackEnd.spring_boot_journey_week7_day3.controller;

import com.JavaBackEnd.spring_boot_journey_week7_day3.dto.request.CreateTaskRequest;
import com.JavaBackEnd.spring_boot_journey_week7_day3.dto.response.TaskResponse;
import com.JavaBackEnd.spring_boot_journey_week7_day3.model.Task;
import com.JavaBackEnd.spring_boot_journey_week7_day3.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

// ── What @WebMvcTest tests about this controller ───────────────────────────
// 1. Does the correct HTTP method + URL reach this method?
// 2. Does @Valid trigger validation and return 400 on failure?
// 3. Does the service result get mapped to the correct DTO and status code?
// 4. Do exception handler responses reach the client correctly?
// 5. Is the JSON response body shaped correctly?
//
// What @WebMvcTest does NOT test:
//   - Business logic (that's TaskService — tested separately with Mockito)
//   - Database behavior (that's @DataJpaTest — Day 4)
@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    public ResponseEntity<List<TaskResponse>> getAll() {
        List<TaskResponse> response = taskService.getAll()
                .stream().map(TaskResponse::from).toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(TaskResponse.from(taskService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<TaskResponse> create(
            @Valid @RequestBody CreateTaskRequest request,
            UriComponentsBuilder uriBuilder) {

        Task created = taskService.create(request);
        URI location = uriBuilder.path("/api/tasks/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(location).body(TaskResponse.from(created));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam Task.Status status) {

        return ResponseEntity.ok(TaskResponse.from(taskService.updateStatus(id, status)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
