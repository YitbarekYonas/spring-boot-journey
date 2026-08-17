package com.JavaBackEnd.spring_boot_journey_week7_day7.repository;

import com.JavaBackEnd.spring_boot_journey_week7_day7.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByStatus(Task.Status status);

    List<Task> findByPriority(Task.Priority priority);

    List<Task> findByOwnerEmail(String ownerEmail);

    List<Task> findByStatusAndPriority(Task.Status status, Task.Priority priority);

    List<Task> findByTitleContainingIgnoreCase(String keyword);

    List<Task> findByDueDateBefore(LocalDate date);

    long countByStatus(Task.Status status);

    @Query("SELECT t FROM Task t " +
            "WHERE t.ownerEmail = :ownerEmail AND t.status <> :status " +
            "ORDER BY t.dueDate ASC")
    List<Task> findActiveTasksForOwner(@Param("ownerEmail") String ownerEmail,
                                        @Param("status") Task.Status excludedStatus);
}
