package com.JavaBackEnd.task_manager.service.impl;

import com.JavaBackEnd.task_manager.entity.Task;
import com.JavaBackEnd.task_manager.entity.TaskPriority;
import com.JavaBackEnd.task_manager.entity.TaskStatus;
import com.JavaBackEnd.task_manager.repository.TaskRepository;
import com.JavaBackEnd.task_manager.service.TaskService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    @Override
    public List<Task> getFilteredTasks(TaskStatus status, TaskPriority priority) {
        if (status != null && priority != null) {
            return taskRepository.findByStatusAndPriority(status, priority);
        }
        if (status != null) {
            return taskRepository.findByStatus(status);
        }
        if (priority != null) {
            return taskRepository.findByPriority(priority);
        }
        return taskRepository.findAll();
    }

    @Override
    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    @Override
    public Task createTask(Task task) {
        // Business Rule 1: Title is required
        if (task.getTitle() == null || task.getTitle().isBlank()) {
            throw new IllegalArgumentException("Task title cannot be blank");
        }

        // Business Rule 2: Title length limit
        if (task.getTitle().length() > 200) {
            throw new IllegalArgumentException("Task title cannot exceed 200 characters");
        }

        // Business Rule 3: Default status to TODO
        if (task.getStatus() == null) {
            task.setStatus(TaskStatus.TODO);
        }

        // Business Rule 4: Default priority to MEDIUM
        if (task.getPriority() == null) {
            task.setPriority(TaskPriority.MEDIUM);
        }

        // Business Rule 5: Cannot create a task that's already DONE
        if (task.getStatus() == TaskStatus.DONE) {
            throw new IllegalArgumentException(
                "Cannot create a task with status DONE. Create it as TODO first.");
        }

        return taskRepository.save(task);
    }

    @Override
    public Optional<Task> updateTask(Long id, Task updatedTask) {
        return taskRepository.findById(id)
                .map(existing -> {
                    // Business Rule: Cannot edit CANCELLED tasks
                    if (existing.getStatus() == TaskStatus.CANCELLED) {
                        throw new IllegalStateException(
                            "Cannot update a cancelled task. Cancelled tasks are read-only.");
                    }

                    // Preserve system-managed fields
                    updatedTask.setId(id);
                    updatedTask.setCreatedAt(existing.getCreatedAt());

                    // Default missing fields to existing values
                    if (updatedTask.getStatus() == null) {
                        updatedTask.setStatus(existing.getStatus());
                    }
                    if (updatedTask.getPriority() == null) {
                        updatedTask.setPriority(existing.getPriority());
                    }

                    return taskRepository.save(updatedTask);
                });
    }

    @Override
    public Optional<Task> updateTaskStatus(Long id, TaskStatus newStatus) {
        return taskRepository.findById(id)
                .map(existing -> {
                    // Business Rule: Enforce valid status transitions (state machine)
                    validateStatusTransition(existing.getStatus(), newStatus);
                    existing.setStatus(newStatus);
                    return taskRepository.save(existing);
                });
    }

    @Override
    public boolean deleteTask(Long id) {
        // Business Rule: Cannot delete IN_PROGRESS tasks
        return taskRepository.findById(id)
                .map(task -> {
                    if (task.getStatus() == TaskStatus.IN_PROGRESS) {
                        throw new IllegalStateException(
                            "Cannot delete a task that is IN_PROGRESS. Cancel it first.");
                    }
                    return taskRepository.deleteById(id);
                })
                .orElse(false);
    }

    @Override
    public List<Task> getOverdueTasks() {
        return taskRepository.findOverdue();
    }

    // ===== Private Helper Methods =====

    private void validateStatusTransition(TaskStatus current, TaskStatus next) {
        boolean valid = switch (current) {
            case TODO        -> next == TaskStatus.IN_PROGRESS || next == TaskStatus.CANCELLED;
            case IN_PROGRESS -> next == TaskStatus.DONE || next == TaskStatus.CANCELLED || next == TaskStatus.TODO;
            case DONE        -> false;  // DONE is terminal
            case CANCELLED   -> false;  // CANCELLED is terminal
        };

        if (!valid) {
            throw new IllegalStateException(
                "Invalid status transition: " + current + " → " + next
                + ". Allowed transitions from " + current + ": "
                + getAllowedTransitions(current));
        }
    }

    private String getAllowedTransitions(TaskStatus status) {
        return switch (status) {
            case TODO        -> "IN_PROGRESS, CANCELLED";
            case IN_PROGRESS -> "DONE, CANCELLED, TODO";
            case DONE        -> "none (terminal state)";
            case CANCELLED   -> "none (terminal state)";
        };
    }
}