package com.JavaBackEnd.spring_boot_journey_week6_day7.service;

import com.JavaBackEnd.spring_boot_journey_week6_day7.dto.request.CreateTaskRequest;
import com.JavaBackEnd.spring_boot_journey_week6_day7.dto.request.UpdateTaskRequest;
import com.JavaBackEnd.spring_boot_journey_week6_day7.entity.Task;
import com.JavaBackEnd.spring_boot_journey_week6_day7.exception.GlobalExceptionHandler.*;
import com.JavaBackEnd.spring_boot_journey_week6_day7.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// ── What this file demonstrates ────────────────────────────────────────────
// Week 6 Day 2 — throws custom exceptions (GlobalExceptionHandler catches them)
// Week 6 Day 5 — @Slf4j, @RequiredArgsConstructor (Lombok)
// Week 6 Day 6 — correct log levels (debug/info/warn/error)
// Service returns entities — controller maps to DTOs (Day 4 pattern)
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TaskService {

    private final TaskRepository taskRepository;

    public Page<Task> getAllTasks(Pageable pageable) {
        log.debug("getAllTasks() — page={}", pageable.getPageNumber());
        return taskRepository.findAll(pageable);
    }

    public Page<Task> getTasksByOwner(String ownerEmail, Pageable pageable) {
        log.debug("getTasksByOwner() — owner='{}'", ownerEmail);
        return taskRepository.findByOwnerEmail(ownerEmail, pageable);
    }

    public Page<Task> getTasksByStatus(Task.Status status, Pageable pageable) {
        log.debug("getTasksByStatus() — status={}", status);
        return taskRepository.findByStatus(status, pageable);
    }

    public Task getTaskById(Long id) {
        log.debug("getTaskById() — id={}", id);
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        // TaskNotFoundException → GlobalExceptionHandler → 404 JSON response
    }

    @Transactional
    public Task createTask(CreateTaskRequest req) {
        log.debug("createTask() — title='{}', owner='{}'", req.getTitle(), req.getOwnerEmail());

        // Week 6 Day 2 — business rule: no duplicate title per owner
        if (taskRepository.existsByTitleAndOwnerEmail(req.getTitle(), req.getOwnerEmail())) {
            log.warn("Duplicate task rejected — title='{}', owner='{}'",
                    req.getTitle(), req.getOwnerEmail());
            throw new DuplicateTaskException(
                "Task '" + req.getTitle() + "' already exists for " + req.getOwnerEmail());
        }

        Task task = Task.builder()
                .title(req.getTitle())
                .description(req.getDescription())
                .priority(req.getPriority() != null ? req.getPriority() : Task.Priority.MEDIUM)
                .dueDate(req.getDueDate())
                .ownerEmail(req.getOwnerEmail())
                .build();

        Task saved = taskRepository.save(task);
        log.info("Task created — id={}, title='{}', owner='{}'",
                saved.getId(), saved.getTitle(), saved.getOwnerEmail());
        return saved;
    }

    @Transactional
    public Task updateTask(Long id, String callerEmail, UpdateTaskRequest req) {
        log.debug("updateTask() — id={}, caller='{}'", id, callerEmail);

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));

        // Week 6 Day 2 — authorization check: only owner can update
        if (!task.getOwnerEmail().equals(callerEmail)) {
            log.warn("Unauthorized update attempt — id={}, caller='{}', owner='{}'",
                    id, callerEmail, task.getOwnerEmail());
            throw new UnauthorizedActionException(
                "Only the task owner can update this task");
        }

        // Apply only non-null fields (partial update)
        if (req.getTitle()       != null) task.setTitle(req.getTitle());
        if (req.getDescription() != null) task.setDescription(req.getDescription());
        if (req.getStatus()      != null) task.setStatus(req.getStatus());
        if (req.getPriority()    != null) task.setPriority(req.getPriority());
        if (req.getDueDate()     != null) task.setDueDate(req.getDueDate());

        Task updated = taskRepository.save(task);
        log.info("Task updated — id={}, status={}", updated.getId(), updated.getStatus());
        return updated;
    }

    @Transactional
    public void deleteTask(Long id, String callerEmail) {
        log.debug("deleteTask() — id={}, caller='{}'", id, callerEmail);

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));

        if (!task.getOwnerEmail().equals(callerEmail)) {
            log.warn("Unauthorized delete attempt — id={}, caller='{}'", id, callerEmail);
            throw new UnauthorizedActionException(
                "Only the task owner can delete this task");
        }

        taskRepository.delete(task);
        log.info("Task deleted — id={}", id);
    }
}
