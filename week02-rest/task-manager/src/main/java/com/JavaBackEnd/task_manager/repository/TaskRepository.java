package com.JavaBackEnd.task_manager.repository;

import com.JavaBackEnd.task_manager.entity.Task;
import com.JavaBackEnd.task_manager.entity.TaskPriority;
import com.JavaBackEnd.task_manager.entity.TaskStatus;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class TaskRepository {

    private final AtomicLong idCounter = new AtomicLong(1);

    // Thread-safe list for singleton bean with concurrent requests
    private final List<Task> tasks = new CopyOnWriteArrayList<>(List.of(
        new Task(idCounter.getAndIncrement(),
                 "Set up Spring Boot project",
                 "Initialize project with required dependencies",
                 TaskStatus.DONE,
                 TaskPriority.HIGH,
                 LocalDate.now().minusDays(7)),

        new Task(idCounter.getAndIncrement(),
                 "Build REST API endpoints",
                 "Implement CRUD operations for task management",
                 TaskStatus.IN_PROGRESS,
                 TaskPriority.HIGH,
                 LocalDate.now().plusDays(2)),

        new Task(idCounter.getAndIncrement(),
                 "Write Postman collection",
                 "Document and test all API endpoints",
                 TaskStatus.TODO,
                 TaskPriority.MEDIUM,
                 LocalDate.now().plusDays(5)),

        new Task(idCounter.getAndIncrement(),
                 "Deploy to Railway",
                 "Dockerize and deploy the application",
                 TaskStatus.TODO,
                 TaskPriority.LOW,
                 LocalDate.now().plusDays(14))
    ));

    // ===== Basic CRUD =====
    public List<Task> findAll() {
        return new ArrayList<>(tasks);
    }

    public Optional<Task> findById(Long id) {
        return tasks.stream()
                .filter(t -> t.getId().equals(id))
                .findFirst();
    }

    public Task save(Task task) {
        if (task.getId() == null) {
            task.setId(idCounter.getAndIncrement());
            task.setCreatedAt(LocalDateTime.now());
            task.setUpdatedAt(LocalDateTime.now());
            tasks.add(task);
        } else {
            task.setUpdatedAt(LocalDateTime.now());
            tasks.replaceAll(t -> t.getId().equals(task.getId()) ? task : t);
        }
        return task;
    }

    public boolean deleteById(Long id) {
        return tasks.removeIf(t -> t.getId().equals(id));
    }

    // ===== Query Methods =====
    public List<Task> findByStatus(TaskStatus status) {
        return tasks.stream()
                .filter(t -> t.getStatus() == status)
                .collect(Collectors.toList());
    }

    public List<Task> findByPriority(TaskPriority priority) {
        return tasks.stream()
                .filter(t -> t.getPriority() == priority)
                .collect(Collectors.toList());
    }

    public List<Task> findByStatusAndPriority(TaskStatus status, TaskPriority priority) {
        return tasks.stream()
                .filter(t -> t.getStatus() == status && t.getPriority() == priority)
                .collect(Collectors.toList());
    }

    public List<Task> findOverdue() {
        return tasks.stream()
                .filter(t -> t.getDueDate() != null
                          && t.getDueDate().isBefore(LocalDate.now())
                          && t.getStatus() != TaskStatus.DONE
                          && t.getStatus() != TaskStatus.CANCELLED)
                .collect(Collectors.toList());
    }
}