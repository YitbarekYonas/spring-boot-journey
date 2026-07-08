package com.JavaBackEnd.task_manager.service;

import com.JavaBackEnd.task_manager.entity.Task;
import com.JavaBackEnd.task_manager.entity.TaskPriority;
import com.JavaBackEnd.task_manager.entity.TaskStatus;

import java.util.List;
import java.util.Optional;

public interface TaskService {

    List<Task> getAllTasks();

    List<Task> getFilteredTasks(TaskStatus status, TaskPriority priority);

    Optional<Task> getTaskById(Long id);

    Task createTask(Task task);

    Optional<Task> updateTask(Long id, Task updatedTask);

    Optional<Task> updateTaskStatus(Long id, TaskStatus newStatus);

    boolean deleteTask(Long id);

    List<Task> getOverdueTasks();
}