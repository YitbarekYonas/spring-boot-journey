package com.JavaBackEnd.spring_boot_journey_week7_day4.repository;

import com.JavaBackEnd.spring_boot_journey_week7_day4.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

// ── What @DataJpaTest tests about this repository ──────────────────────────
// 1. Does JpaRepository give us save/findById/findAll/delete for free?
// 2. Do the DERIVED query methods (findByX) generate the correct SQL and
//    return the correct rows?
// 3. Does the custom JPQL query (@Query) return the correct projection?
//
// What @DataJpaTest does NOT test:
//   - HTTP layer (that's @WebMvcTest — Day 3)
//   - Business logic in the service layer (that's Mockito unit tests — Day 2)
public interface TaskRepository extends JpaRepository<Task, Long> {

    // ── Derived query methods ───────────────────────────────────────────────
    // Spring Data parses the method name and builds the query automatically.

    List<Task> findByStatus(Task.Status status);

    List<Task> findByPriority(Task.Priority priority);

    List<Task> findByOwnerEmail(String ownerEmail);

    List<Task> findByStatusAndPriority(Task.Status status, Task.Priority priority);

    List<Task> findByTitleContainingIgnoreCase(String keyword);

    List<Task> findByDueDateBefore(LocalDate date);

    long countByStatus(Task.Status status);

    // ── Custom JPQL query ────────────────────────────────────────────────────
    // Object-oriented query against the Task entity/fields, not raw table/columns.
    // Named parameters (:status) are clearer than positional (?1) for anything
    // with more than one bind variable.
    @Query("SELECT t FROM Task t " +
            "WHERE t.ownerEmail = :ownerEmail AND t.status <> :status " +
            "ORDER BY t.dueDate ASC")
    List<Task> findActiveTasksForOwner(@Param("ownerEmail") String ownerEmail,
                                        @Param("status") Task.Status excludedStatus);
}
