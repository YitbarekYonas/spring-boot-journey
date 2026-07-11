package com.JavaBackEnd.spring_boot_journey_week3_day6.service.impl;

import com.JavaBackEnd.spring_boot_journey_week3_day6.entity.Task;
import com.JavaBackEnd.spring_boot_journey_week3_day6.entity.TaskPriority;
import com.JavaBackEnd.spring_boot_journey_week3_day6.entity.TaskStatus;
import com.JavaBackEnd.spring_boot_journey_week3_day6.repository.TaskRepository;
import com.JavaBackEnd.spring_boot_journey_week3_day6.service.TaskService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    @Transactional
    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    @Override
    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    @Override
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    @Override
    public List<Task> getAllTasksWithTags() {
        return taskRepository.findAllWithTags();
    }

    @Override
    @Transactional
    public Task updateTaskStatus(Long id, TaskStatus status) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));
        task.setStatus(status);  // Dirty checking will update automatically
        return task;
    }

    // Demonstrate dirty checking - no save() call needed
    @Override
    @Transactional
    public void demonstrateDirtyChecking(Long id) {
        System.out.println("\n=== DIRTY CHECKING DEMO ===");
        System.out.println("1. Loading task with ID: " + id);
        Task task = taskRepository.findById(id).orElseThrow();
        // SQL: SELECT * FROM tasks WHERE id = ?

        System.out.println("2. Modifying task WITHOUT calling save()");
        task.setStatus(TaskStatus.IN_PROGRESS);
        task.setPriority(TaskPriority.CRITICAL);

        System.out.println("3. No save() called. Transaction will commit.");
        System.out.println("4. Hibernate's dirty checking will generate UPDATE SQL.");
        // At transaction commit:
        // Hibernate compares current state vs snapshot
        // UPDATE tasks SET status = 'IN_PROGRESS', priority = 'CRITICAL' WHERE id = ?
    }

    // Demonstrate N+1 problem
    @Override
    public void demonstrateNPlusOne() {
        System.out.println("\n=== N+1 PROBLEM DEMO ===");
        System.out.println("Loading all tasks...");
        List<Task> tasks = taskRepository.findAll();
        // SQL: SELECT * FROM tasks  (1 query)

        System.out.println("Accessing tags for each task (N+1 problem):");
        for (Task task : tasks) {
            int tagCount = task.getTags().size();
            // EACH call triggers lazy loading:
            // SELECT * FROM task_tags JOIN tags WHERE task_id = ?
            // This is N queries (one per task)
            System.out.println("  Task: " + task.getTitle() + " has " + tagCount + " tags");
        }
        System.out.println("Total queries: 1 + " + tasks.size() + " = " + (1 + tasks.size()));
        System.out.println("Use getAllTasksWithTags() to see JOIN FETCH fix.");
    }
}