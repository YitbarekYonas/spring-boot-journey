package com.JavaBackEnd.spring_boot_journey_week6_day7.controller;

import com.JavaBackEnd.spring_boot_journey_week6_day7.dto.request.CreateTaskRequest;
import com.JavaBackEnd.spring_boot_journey_week6_day7.dto.request.UpdateTaskRequest;
import com.JavaBackEnd.spring_boot_journey_week6_day7.dto.response.TaskResponse;
import com.JavaBackEnd.spring_boot_journey_week6_day7.entity.Task;
import com.JavaBackEnd.spring_boot_journey_week6_day7.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Slf4j
public class TaskController {

    private final TaskService taskService;

    // GET /api/tasks?page=0&size=10&sort=createdAt,desc
    @GetMapping
    public ResponseEntity<Page<TaskResponse>> getAllTasks(
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(taskService.getAllTasks(pageable).map(TaskResponse::from));
    }

    // GET /api/tasks?owner=alice@example.com
    @GetMapping("/by-owner")
    public ResponseEntity<Page<TaskResponse>> getByOwner(
            @RequestParam String owner,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(
            taskService.getTasksByOwner(owner, pageable).map(TaskResponse::from));
    }

    // GET /api/tasks/by-status?status=TODO
    @GetMapping("/by-status")
    public ResponseEntity<Page<TaskResponse>> getByStatus(
            @RequestParam Task.Status status,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(
            taskService.getTasksByStatus(status, pageable).map(TaskResponse::from));
    }

    // GET /api/tasks/{id}
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(TaskResponse.from(taskService.getTaskById(id)));
        // If not found → service throws TaskNotFoundException
        //              → GlobalExceptionHandler returns 404 JSON
    }

    // POST /api/tasks
    @PostMapping
    public ResponseEntity<TaskResponse> create(
            @Valid @RequestBody CreateTaskRequest req,
            UriComponentsBuilder uriBuilder) {
        // @Valid triggers validation → if fails → GlobalExceptionHandler → 400 + fieldErrors
        Task created = taskService.createTask(req);
        URI location = uriBuilder.path("/api/tasks/{id}").buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(location).body(TaskResponse.from(created));
    }

    // PATCH /api/tasks/{id}?caller=alice@example.com
    @PatchMapping("/{id}")
    public ResponseEntity<TaskResponse> update(
            @PathVariable Long id,
            @RequestParam String caller,
            @Valid @RequestBody UpdateTaskRequest req) {
        Task updated = taskService.updateTask(id, caller, req);
        return ResponseEntity.ok(TaskResponse.from(updated));
    }

    // DELETE /api/tasks/{id}?caller=alice@example.com
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @RequestParam String caller) {
        taskService.deleteTask(id, caller);
        return ResponseEntity.noContent().build();
    }
}
