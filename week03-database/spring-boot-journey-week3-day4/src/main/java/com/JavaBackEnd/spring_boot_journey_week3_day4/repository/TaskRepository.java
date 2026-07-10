package com.JavaBackEnd.spring_boot_journey_week3_day4.repository;

import com.JavaBackEnd.spring_boot_journey_week3_day4.entity.Task;
import com.JavaBackEnd.spring_boot_journey_week3_day4.entity.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByStatus(TaskStatus status);

    @Query("SELECT t FROM Task t JOIN FETCH t.assignedUser WHERE t.id = :id")
    Optional<Task> findByIdWithUser(@Param("id") Long id);

    @Query("SELECT t FROM Task t JOIN FETCH t.assignedUser")
    List<Task> findAllWithUsers();
}