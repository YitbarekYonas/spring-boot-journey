package com.JavaBackEnd.spring_boot_journey_week3_day6.service;

import com.JavaBackEnd.spring_boot_journey_week3_day6.entity.Task;
import com.JavaBackEnd.spring_boot_journey_week3_day6.entity.TaskStatus;

import java.util.List;
import java.util.Optional;

public interface TaskService {
    Task createTask(Task task);
    Optional<Task> getTaskById(Long id);
    List<Task> getAllTasks();
    List<Task> getAllTasksWithTags();
    Task updateTaskStatus(Long id, TaskStatus status);
    void demonstrateDirtyChecking(Long id);
    void demonstrateNPlusOne();
}