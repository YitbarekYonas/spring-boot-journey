package com.JavaBackEnd.spring_boot_journey_week8_day3.repository;

import com.JavaBackEnd.spring_boot_journey_week8_day3.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByStatus(Task.Status status);

    List<Task> findByOwnerEmail(String ownerEmail);

    @Query("SELECT t FROM Task t " +
            "WHERE t.ownerEmail = :ownerEmail AND t.status <> :status " +
            "ORDER BY t.dueDate ASC")
    List<Task> findActiveTasksForOwner(@Param("ownerEmail") String ownerEmail,
                                        @Param("status") Task.Status excludedStatus);
}
