package com.JavaBackEnd.spring_boot_journey_week3_day5.controller;

import com.JavaBackEnd.spring_boot_journey_week3_day5.entity.Task;
import com.JavaBackEnd.spring_boot_journey_week3_day5.repository.TaskRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskRepository taskRepository;

    public TaskController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    // ⚠️ WARNING: This method causes N+1 problem!
    // Accessing tags in a loop will trigger lazy loading N times
    @GetMapping("/all")
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    // ✅ Fix: JOIN FETCH loads all tags in one query
    @GetMapping("/all-with-tags")
    public List<Task> getAllTasksWithTags() {
        return taskRepository.findAllWithTags();
    }

    @GetMapping("/by-tag/{tagName}")
    public List<Task> getTasksByTag(@PathVariable String tagName) {
        return taskRepository.findTasksByTagName(tagName);
    }
}