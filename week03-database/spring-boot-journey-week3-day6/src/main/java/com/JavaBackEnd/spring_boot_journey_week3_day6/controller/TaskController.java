package com.JavaBackEnd.spring_boot_journey_week3_day6.controller;

import com.JavaBackEnd.spring_boot_journey_week3_day6.entity.Task;
import com.JavaBackEnd.spring_boot_journey_week3_day6.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<Task>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
        // ⚠️ WARNING: This triggers N+1 if tags are accessed
    }

    @GetMapping("/all-with-tags")
    public ResponseEntity<List<Task>> getAllTasksWithTags() {
        return ResponseEntity.ok(taskService.getAllTasksWithTags());
        // ✅ JOIN FETCH - tags loaded in 1 query
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        return taskService.getTaskById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
        // ⚠️ Accessing tags will cause LazyInitializationException
    }

    @GetMapping("/{id}/demo/dirty-check")
    public ResponseEntity<String> demoDirtyChecking(@PathVariable Long id) {
        taskService.demonstrateDirtyChecking(id);
        return ResponseEntity.ok("Dirty checking demonstrated. Check SQL logs.");
    }

    @GetMapping("/demo/n-plus-one")
    public ResponseEntity<String> demoNPlusOne() {
        taskService.demonstrateNPlusOne();
        return ResponseEntity.ok("N+1 demonstrated. Check console logs.");
    }
}