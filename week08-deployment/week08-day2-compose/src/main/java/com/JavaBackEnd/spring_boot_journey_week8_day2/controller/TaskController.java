package com.JavaBackEnd.spring_boot_journey_week8_day2.controller;

import com.JavaBackEnd.spring_boot_journey_week8_day2.dto.TaskRequest;
import com.JavaBackEnd.spring_boot_journey_week8_day2.dto.TaskResponse;
import com.JavaBackEnd.spring_boot_journey_week8_day2.model.Task;
import com.JavaBackEnd.spring_boot_journey_week8_day2.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskRequest request) {
        TaskResponse created = taskService.createTask(request);
        return ResponseEntity
                .created(URI.create("/api/tasks/" + created.getId()))
                .body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> getAllTasks(
            @RequestParam(required = false) Task.Status status) {
        List<TaskResponse> tasks = (status != null)
                ? taskService.getTasksByStatus(status)
                : taskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable Long id,
                                                     @Valid @RequestBody TaskRequest request) {
        return ResponseEntity.ok(taskService.updateTask(id, request));
    }

    @PatchMapping("/{id}/done")
    public ResponseEntity<TaskResponse> markAsDone(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.markAsDone(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
