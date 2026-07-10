package com.JavaBackEnd.spring_boot_journey_week3_day4.controller;

import com.JavaBackEnd.spring_boot_journey_week3_day4.entity.Task;
import com.JavaBackEnd.spring_boot_journey_week3_day4.entity.User;
import com.JavaBackEnd.spring_boot_journey_week3_day4.repository.TaskRepository;
import com.JavaBackEnd.spring_boot_journey_week3_day4.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/debug")
public class TaskDebugController {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskDebugController(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/tasks")
    public List<Task> getAllTasks() {
        return taskRepository.findAllWithUsers();
    }

    @GetMapping("/tasks/{id}")
    public Task getTaskWithUser(@PathVariable Long id) {
        return taskRepository.findByIdWithUser(id).orElse(null);
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/users/{id}/tasks")
    public List<Task> getUserTasks(@PathVariable Long id) {
        return userRepository.findByIdWithTasks(id)
                .map(User::getTasks)
                .orElse(List.of());
    }
}