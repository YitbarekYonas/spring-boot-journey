package com.JavaBackEnd.spring_boot_journey_week3_day3.controller;

import com.JavaBackEnd.spring_boot_journey_week3_day3.entity.Task;
import com.JavaBackEnd.spring_boot_journey_week3_day3.entity.TaskPriority;
import com.JavaBackEnd.spring_boot_journey_week3_day3.entity.TaskStatus;
import com.JavaBackEnd.spring_boot_journey_week3_day3.repository.TaskRepository;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/debug/tasks")
public class TaskDebugController {

    private final TaskRepository taskRepository;

    public TaskDebugController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @GetMapping("/by-status/{status}")
    public List<Task> byStatus(@PathVariable TaskStatus status) {
        return taskRepository.findByStatus(status);
    }

    @GetMapping("/by-priority/{priority}")
    public List<Task> byPriority(@PathVariable TaskPriority priority) {
        return taskRepository.findByPriority(priority);
    }

    @GetMapping("/search")
    public List<Task> search(@RequestParam String keyword) {
        return taskRepository.findByTitleContainingIgnoreCase(keyword);
    }

    @GetMapping("/overdue")
    public List<Task> overdue() {
        return taskRepository.findOverdueTasks(
            LocalDate.now(),
            List.of(TaskStatus.DONE, TaskStatus.CANCELLED)
        );
    }

    @GetMapping("/count-by-status/{status}")
    public long countByStatus(@PathVariable TaskStatus status) {
        return taskRepository.countByStatus(status);
    }

    @GetMapping("/top5-recent")
    public List<Task> top5Recent() {
        return taskRepository.findTop5ByOrderByCreatedAtDesc();
    }

    @GetMapping("/all")
    public List<Task> all() {
        return taskRepository.findAll();
    }

    @GetMapping("/due-before")
    public List<Task> dueBefore(@RequestParam String date) {
        return taskRepository.findByDueDateBefore(LocalDate.parse(date));
    }

    @GetMapping("/due-after")
    public List<Task> dueAfter(@RequestParam String date) {
        return taskRepository.findByDueDateAfter(LocalDate.parse(date));
    }
}