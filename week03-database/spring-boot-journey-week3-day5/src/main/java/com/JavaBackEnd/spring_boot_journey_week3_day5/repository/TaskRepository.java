package com.JavaBackEnd.spring_boot_journey_week3_day5.repository;

import com.JavaBackEnd.spring_boot_journey_week3_day5.entity.Task;
import com.JavaBackEnd.spring_boot_journey_week3_day5.entity.TaskPriority;
import com.JavaBackEnd.spring_boot_journey_week3_day5.entity.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByStatus(TaskStatus status);

    List<Task> findByPriority(TaskPriority priority);

    List<Task> findByStatusAndPriority(TaskStatus status, TaskPriority priority);

    List<Task> findByTitleContainingIgnoreCase(String keyword);

    long countByStatus(TaskStatus status);

    // JOIN FETCH to fix N+1
    @Query("SELECT DISTINCT t FROM Task t LEFT JOIN FETCH t.tags")
    List<Task> findAllWithTags();

    @Query("SELECT DISTINCT t FROM Task t LEFT JOIN FETCH t.tags WHERE t.status = :status")
    List<Task> findByStatusWithTags(@Param("status") TaskStatus status);

    @Query("SELECT DISTINCT t FROM Task t LEFT JOIN FETCH t.tags WHERE t.id = :id")
    Optional<Task> findByIdWithTags(@Param("id") Long id);

    // Get tasks by tag name (uses bidirectional navigation)
    @Query("SELECT t FROM Task t JOIN t.tags tag WHERE tag.name = :tagName")
    List<Task> findTasksByTagName(@Param("tagName") String tagName);

    // Overdue tasks with tags
    @Query("""
        SELECT DISTINCT t FROM Task t
        LEFT JOIN FETCH t.tags
        WHERE t.dueDate < :today
        AND t.status NOT IN (:excludedStatuses)
        ORDER BY t.priority DESC, t.dueDate ASC
        """)
    List<Task> findOverdueTasksWithTags(
        @Param("today") LocalDate today,
        @Param("excludedStatuses") List<TaskStatus> excludedStatuses
    );
}