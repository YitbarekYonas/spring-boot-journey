package com.JavaBackEnd.spring_boot_journey_week7_day6.service;

import com.JavaBackEnd.spring_boot_journey_week7_day6.dto.TaskRequest;
import com.JavaBackEnd.spring_boot_journey_week7_day6.dto.TaskResponse;
import com.JavaBackEnd.spring_boot_journey_week7_day6.exception.ResourceNotFoundException;
import com.JavaBackEnd.spring_boot_journey_week7_day6.model.Task;
import com.JavaBackEnd.spring_boot_journey_week7_day6.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

// Constructor injection via Lombok's @RequiredArgsConstructor (General Best
// Practice #1) — no field injection, no @Autowired needed on a single
// final constructor param.
@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskResponse createTask(TaskRequest request) {
        Task saved = taskRepository.save(Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(request.getStatus())
                .priority(request.getPriority())
                .dueDate(request.getDueDate())
                .ownerEmail(request.getOwnerEmail())
                .build());

        return TaskResponse.fromEntity(saved);
    }

    public TaskResponse getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        return TaskResponse.fromEntity(task);
    }

    public List<TaskResponse> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(TaskResponse::fromEntity)
                .toList();
    }

    public List<TaskResponse> getTasksByStatus(Task.Status status) {
        return taskRepository.findByStatus(status).stream()
                .map(TaskResponse::fromEntity)
                .toList();
    }

    public TaskResponse updateTask(Long id, TaskRequest request) {
        Task existing = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        existing.setTitle(request.getTitle());
        existing.setDescription(request.getDescription());
        existing.setStatus(request.getStatus());
        existing.setPriority(request.getPriority());
        existing.setDueDate(request.getDueDate());
        existing.setOwnerEmail(request.getOwnerEmail());

        return TaskResponse.fromEntity(taskRepository.save(existing));
    }

    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new ResourceNotFoundException("Task not found with id: " + id);
        }
        taskRepository.deleteById(id);
    }
}
