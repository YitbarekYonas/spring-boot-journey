package com.JavaBackEnd.spring_boot_journey_week6_day7.repository;

import com.JavaBackEnd.spring_boot_journey_week6_day7.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    Page<Task> findByOwnerEmail(String ownerEmail, Pageable pageable);

    Page<Task> findByStatus(Task.Status status, Pageable pageable);

    Page<Task> findByOwnerEmailAndStatus(String ownerEmail, Task.Status status, Pageable pageable);

    boolean existsByTitleAndOwnerEmail(String title, String ownerEmail);
}
