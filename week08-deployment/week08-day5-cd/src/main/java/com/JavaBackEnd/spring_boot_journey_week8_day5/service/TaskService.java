package com.JavaBackEnd.spring_boot_journey_week8_day5.service;

import com.JavaBackEnd.spring_boot_journey_week8_day5.dto.TaskRequest;
import com.JavaBackEnd.spring_boot_journey_week8_day5.dto.TaskResponse;
import com.JavaBackEnd.spring_boot_journey_week8_day5.exception.InvalidTaskStateException;
import com.JavaBackEnd.spring_boot_journey_week8_day5.exception.ResourceNotFoundException;
import com.JavaBackEnd.spring_boot_journey_week8_day5.model.Task;
import com.JavaBackEnd.spring_boot_journey_week8_day5.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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
        return TaskResponse.fromEntity(findTaskOrThrow(id));
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
        Task existing = findTaskOrThrow(id);

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

    public TaskResponse markAsDone(Long id) {
        Task task = findTaskOrThrow(id);

        if (task.getStatus() == Task.Status.DONE) {
            throw new InvalidTaskStateException("Task " + id + " is already DONE");
        }
        if (task.getStatus() == Task.Status.TODO) {
            throw new InvalidTaskStateException(
                    "Task " + id + " must be IN_PROGRESS before it can be marked DONE");
        }

        task.setStatus(Task.Status.DONE);
        return TaskResponse.fromEntity(taskRepository.save(task));
    }

    private Task findTaskOrThrow(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
    }
}
