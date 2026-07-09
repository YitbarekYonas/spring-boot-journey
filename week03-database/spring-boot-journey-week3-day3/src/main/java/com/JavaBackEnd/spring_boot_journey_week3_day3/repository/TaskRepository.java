package com.JavaBackEnd.spring_boot_journey_week3_day3.repository;

import com.JavaBackEnd.spring_boot_journey_week3_day3.entity.Task;
import com.JavaBackEnd.spring_boot_journey_week3_day3.entity.TaskPriority;
import com.JavaBackEnd.spring_boot_journey_week3_day3.entity.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    // ── Single-condition queries ───────────────────────────────────

    List<Task> findByStatus(TaskStatus status);

    List<Task> findByPriority(TaskPriority priority);

    Optional<Task> findByTitle(String title);

    // ── Multi-condition queries ────────────────────────────────────

    List<Task> findByStatusAndPriority(TaskStatus status, TaskPriority priority);

    List<Task> findByStatusOrPriority(TaskStatus status, TaskPriority priority);

    // ── String matching ───────────────────────────────────────────

    List<Task> findByTitleContaining(String keyword);

    List<Task> findByTitleContainingIgnoreCase(String keyword);

    List<Task> findByTitleStartingWith(String prefix);

    // ── Date comparisons ──────────────────────────────────────────

    List<Task> findByDueDateBefore(LocalDate date);

    List<Task> findByDueDateAfter(LocalDate date);

    List<Task> findByDueDateBetween(LocalDate start, LocalDate end);

    // ── Null checks ───────────────────────────────────────────────

    List<Task> findByDueDateIsNull();

    List<Task> findByDueDateIsNotNull();

    // ── Count and existence ───────────────────────────────────────

    long countByStatus(TaskStatus status);

    boolean existsByIdAndStatus(Long id, TaskStatus status);

    // ── Sorting in method name ────────────────────────────────────

    List<Task> findByStatusOrderByPriorityDesc(TaskStatus status);

    List<Task> findAllByOrderByDueDateAscCreatedAtDesc();

    // ── Limiting results ──────────────────────────────────────────

    Optional<Task> findFirstByStatus(TaskStatus status);

    List<Task> findTop5ByOrderByCreatedAtDesc();

    // ── Custom queries with @Query ─────────────────────────────────

    @Query("""
        SELECT t FROM Task t
        WHERE t.dueDate < :today
        AND t.status NOT IN (:excludedStatuses)
        ORDER BY t.priority DESC, t.dueDate ASC
        """)
    List<Task> findOverdueTasks(
        @Param("today") LocalDate today,
        @Param("excludedStatuses") List<TaskStatus> excludedStatuses
    );

    @Modifying
    @Transactional
    @Query("UPDATE Task t SET t.status = :newStatus WHERE t.status = :oldStatus")
    int updateStatusBulk(
        @Param("oldStatus") TaskStatus oldStatus,
        @Param("newStatus") TaskStatus newStatus
    );

    @Modifying
    @Transactional
    @Query("DELETE FROM Task t WHERE t.status = :status AND t.createdAt < :before")
    int deleteByStatusBefore(
        @Param("status") TaskStatus status,
        @Param("before") LocalDateTime before
    );
}