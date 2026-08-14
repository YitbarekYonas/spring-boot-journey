package com.JavaBackEnd.spring_boot_journey_week7_day4;

import com.JavaBackEnd.spring_boot_journey_week7_day4.model.Task;
import com.JavaBackEnd.spring_boot_journey_week7_day4.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

// ── @DataJpaTest — what it loads ────────────────────────────────────────────
//
// @SpringBootTest would load EVERYTHING: controllers, services, security,
// the works. Slow, and testing far more than "does this query work".
//
// @DataJpaTest loads ONLY the JPA-related slice:
//   ✅ @Entity classes
//   ✅ Spring Data JPA repositories       (TaskRepository)
//   ✅ An embedded, in-memory database    (H2 by default, auto-configured —
//      it REPLACES whatever datasource is in application.yml unless you
//      disable that with @AutoConfigureTestDatabase(replace = NONE))
//   ✅ TestEntityManager                  (a test-only helper around the JPA
//      EntityManager, made for setting up fixtures without a repository)
//   ❌ Controllers / web layer
//   ❌ Services
//   ❌ Security
//
// Each @Test method also runs inside a transaction that's ROLLED BACK at
// the end — so tests never interfere with each other's data, and you never
// have to manually clean up.
@DataJpaTest
@DisplayName("TaskRepository (@DataJpaTest)")
class TaskRepositoryTest {

    // ── TestEntityManager ────────────────────────────────────────────────────
    // A thin wrapper around JPA's EntityManager, built for tests:
    //   - persistAndFlush(entity)  → save + force SQL to run immediately
    //   - find(Class, id)          → read straight from the persistence context
    //   - clear()                  → detach everything (useful for lazy-loading checks)
    //
    // We use this to set up fixtures directly, WITHOUT going through the
    // repository under test — so a bug in TaskRepository.save() can't also
    // hide a bug in TaskRepository.findByX().
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TaskRepository taskRepository;

    private Task todoHighAlice;
    private Task inProgressMediumAlice;
    private Task doneHighAlice;
    private Task todoLowBob;
    private Task overdueTodoBob;

    @BeforeEach
    void setUp() {
        todoHighAlice = entityManager.persistAndFlush(Task.builder()
                .title("Fix login bug")
                .description("JWT token not refreshing")
                .status(Task.Status.TODO)
                .priority(Task.Priority.HIGH)
                .ownerEmail("alice@example.com")
                .dueDate(LocalDate.now().plusDays(5))
                .build());

        inProgressMediumAlice = entityManager.persistAndFlush(Task.builder()
                .title("Write repository tests")
                .status(Task.Status.IN_PROGRESS)
                .priority(Task.Priority.MEDIUM)
                .ownerEmail("alice@example.com")
                .dueDate(LocalDate.now().plusDays(10))
                .build());

        doneHighAlice = entityManager.persistAndFlush(Task.builder()
                .title("Set up CI pipeline")
                .status(Task.Status.DONE)
                .priority(Task.Priority.HIGH)
                .ownerEmail("alice@example.com")
                .dueDate(LocalDate.now().minusDays(2))
                .build());

        todoLowBob = entityManager.persistAndFlush(Task.builder()
                .title("Update README")
                .status(Task.Status.TODO)
                .priority(Task.Priority.LOW)
                .ownerEmail("bob@example.com")
                .dueDate(LocalDate.now().plusDays(20))
                .build());

        overdueTodoBob = entityManager.persistAndFlush(Task.builder()
                .title("Renew SSL certificate")
                .status(Task.Status.TODO)
                .priority(Task.Priority.HIGH)
                .ownerEmail("bob@example.com")
                .dueDate(LocalDate.now().minusDays(3))
                .build());

        entityManager.clear(); // detach everything — force repository reads to hit the DB
    }

    // ════════════════════════════════════════════════════════════════════════
    // SECTION 1 — Built-in JpaRepository methods (save / findById / count)
    // ════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("save() persists a task and generates an id")
    void save_persistsTask_andGeneratesId() {
        Task saved = taskRepository.save(Task.builder()
                .title("Brand new task")
                .status(Task.Status.TODO)
                .priority(Task.Priority.MEDIUM)
                .ownerEmail("carol@example.com")
                .build());

        assertThat(saved.getId()).isNotNull();

        Task found = entityManager.find(Task.class, saved.getId());
        assertThat(found.getTitle()).isEqualTo("Brand new task");
        // @PrePersist should have defaulted createdAt
        assertThat(found.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("findById() returns empty Optional when id doesn't exist")
    void findById_returnsEmpty_whenNotFound() {
        Optional<Task> result = taskRepository.findById(999L);
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("count() reflects all seeded rows")
    void count_reflectsSeededRows() {
        assertThat(taskRepository.count()).isEqualTo(5);
    }

    // ════════════════════════════════════════════════════════════════════════
    // SECTION 2 — Derived query methods
    // ════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("findByStatus() returns only tasks with matching status")
    void findByStatus_returnsMatchingTasks() {
        List<Task> todos = taskRepository.findByStatus(Task.Status.TODO);

        assertThat(todos)
                .hasSize(3)
                .extracting(Task::getTitle)
                .containsExactlyInAnyOrder(
                        "Fix login bug", "Update README", "Renew SSL certificate");
    }

    @Test
    @DisplayName("findByPriority() returns only tasks with matching priority")
    void findByPriority_returnsMatchingTasks() {
        List<Task> highPriority = taskRepository.findByPriority(Task.Priority.HIGH);

        assertThat(highPriority).hasSize(3)
                .allMatch(t -> t.getPriority() == Task.Priority.HIGH);
    }

    @Test
    @DisplayName("findByOwnerEmail() returns only that owner's tasks")
    void findByOwnerEmail_returnsOnlyOwnersTasks() {
        List<Task> aliceTasks = taskRepository.findByOwnerEmail("alice@example.com");

        assertThat(aliceTasks).hasSize(3)
                .allMatch(t -> t.getOwnerEmail().equals("alice@example.com"));
    }

    @Test
    @DisplayName("findByStatusAndPriority() combines both conditions (AND, not OR)")
    void findByStatusAndPriority_combinesConditions() {
        List<Task> result = taskRepository.findByStatusAndPriority(
                Task.Status.TODO, Task.Priority.HIGH);

        // TODO+HIGH matches: "Fix login bug" (alice) and "Renew SSL certificate" (bob)
        // "Update README" is TODO but LOW — must be excluded
        assertThat(result).hasSize(2)
                .extracting(Task::getTitle)
                .containsExactlyInAnyOrder("Fix login bug", "Renew SSL certificate");
    }

    @Test
    @DisplayName("findByTitleContainingIgnoreCase() matches case-insensitively and partially")
    void findByTitleContainingIgnoreCase_matchesPartialAndIgnoresCase() {
        List<Task> result = taskRepository.findByTitleContainingIgnoreCase("readme");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Update README");
    }

    @Test
    @DisplayName("findByTitleContainingIgnoreCase() returns empty list when nothing matches")
    void findByTitleContainingIgnoreCase_returnsEmptyList_whenNoMatch() {
        List<Task> result = taskRepository.findByTitleContainingIgnoreCase("nonexistent");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findByDueDateBefore() returns only overdue tasks")
    void findByDueDateBefore_returnsOverdueTasks() {
        List<Task> overdue = taskRepository.findByDueDateBefore(LocalDate.now());

        assertThat(overdue).hasSize(2)
                .extracting(Task::getTitle)
                .containsExactlyInAnyOrder("Set up CI pipeline", "Renew SSL certificate");
    }

    @Test
    @DisplayName("countByStatus() returns the correct count without loading entities")
    void countByStatus_returnsCorrectCount() {
        long doneCount = taskRepository.countByStatus(Task.Status.DONE);
        long todoCount = taskRepository.countByStatus(Task.Status.TODO);

        assertThat(doneCount).isEqualTo(1);
        assertThat(todoCount).isEqualTo(3);
    }

    // ════════════════════════════════════════════════════════════════════════
    // SECTION 3 — Custom JPQL query (@Query)
    // ════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("findActiveTasksForOwner() excludes DONE tasks and orders by dueDate ascending")
    void findActiveTasksForOwner_excludesDone_andOrdersByDueDate() {
        List<Task> active = taskRepository.findActiveTasksForOwner(
                "alice@example.com", Task.Status.DONE);

        // Alice has 3 tasks total, 1 is DONE ("Set up CI pipeline") → excluded.
        // Remaining 2, ordered by dueDate ascending:
        //   "Fix login bug"          (due in 5 days)
        //   "Write repository tests" (due in 10 days)
        assertThat(active).hasSize(2);
        assertThat(active.get(0).getTitle()).isEqualTo("Fix login bug");
        assertThat(active.get(1).getTitle()).isEqualTo("Write repository tests");
        assertThat(active).noneMatch(t -> t.getStatus() == Task.Status.DONE);
    }

    @Test
    @DisplayName("findActiveTasksForOwner() returns empty list for an owner with no tasks")
    void findActiveTasksForOwner_returnsEmptyList_forUnknownOwner() {
        List<Task> result = taskRepository.findActiveTasksForOwner(
                "nobody@example.com", Task.Status.DONE);

        assertThat(result).isEmpty();
    }
}
