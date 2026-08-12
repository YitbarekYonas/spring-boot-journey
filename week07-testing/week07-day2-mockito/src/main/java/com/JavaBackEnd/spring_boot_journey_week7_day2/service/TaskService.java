package com.JavaBackEnd.spring_boot_journey_week7_day2.service;

import com.JavaBackEnd.spring_boot_journey_week7_day2.exception.TaskNotFoundException;
import com.JavaBackEnd.spring_boot_journey_week7_day2.model.Task;
import com.JavaBackEnd.spring_boot_journey_week7_day2.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// ── What Mockito tests here ───────────────────────────────────────────────
// TaskService depends on TaskRepository.
// In tests, we REPLACE the real TaskRepository with a Mockito mock.
// We then control exactly what the mock returns → test service logic in isolation.
//
// This means:
//   - No database required
//   - No Spring context needed
//   - Tests run in milliseconds
//   - If the test fails, the bug is in TaskService (not the DB or network)
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TaskService {

    private final TaskRepository taskRepository;

    public Task getById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    public List<Task> getByOwner(String ownerEmail) {
        log.debug("Fetching tasks for owner='{}'", ownerEmail);
        return taskRepository.findByOwnerEmail(ownerEmail);
    }

    @Transactional
    public Task create(Task task) {
        // Business rule: no duplicate title per owner
        if (taskRepository.existsByTitleAndOwnerEmail(task.getTitle(), task.getOwnerEmail())) {
            throw new IllegalArgumentException(
                "Task '" + task.getTitle() + "' already exists for " + task.getOwnerEmail());
        }
        Task saved = taskRepository.save(task);
        log.info("Task created — id={}, title='{}'", saved.getId(), saved.getTitle());
        return saved;
    }

    @Transactional
    public Task updateStatus(Long id, Task.Status newStatus) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        task.setStatus(newStatus);
        Task updated = taskRepository.save(task);
        log.info("Task {} status updated to {}", id, newStatus);
        return updated;
    }

    @Transactional
    public void delete(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new TaskNotFoundException(id);
        }
        taskRepository.deleteById(id);
        log.info("Task deleted — id={}", id);
    }

    public long countTasksForOwner(String ownerEmail) {
        return taskRepository.countByOwnerEmail(ownerEmail);
    }
}
