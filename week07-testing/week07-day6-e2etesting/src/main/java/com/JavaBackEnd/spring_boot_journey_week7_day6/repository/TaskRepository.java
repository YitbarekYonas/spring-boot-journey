package com.JavaBackEnd.spring_boot_journey_week7_day6.repository;

import com.JavaBackEnd.spring_boot_journey_week7_day6.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByStatus(Task.Status status);

    List<Task> findByOwnerEmail(String ownerEmail);

    List<Task> findByStatusAndPriority(Task.Status status, Task.Priority priority);
}
