package com.JavaBackEnd.spring_boot_journey_week7_day2.repository;

import com.JavaBackEnd.spring_boot_journey_week7_day2.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// ── Why we mock this ──────────────────────────────────────────────────────
// This interface normally talks to a real database.
// In unit tests we DON'T want a real database — we want to test
// TaskService logic in complete isolation.
// Mockito creates a fake implementation of this interface at runtime,
// letting us control exactly what it returns without touching any DB.
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByOwnerEmail(String ownerEmail);

    Page<Task> findByStatus(Task.Status status, Pageable pageable);

    boolean existsByTitleAndOwnerEmail(String title, String ownerEmail);

    long countByOwnerEmail(String ownerEmail);
}
