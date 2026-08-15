package com.JavaBackEnd.spring_boot_journey_week7_day5;

import com.JavaBackEnd.spring_boot_journey_week7_day5.model.Task;
import com.JavaBackEnd.spring_boot_journey_week7_day5.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

// ── Why this test exists (and what it proves that Day 4 can't) ─────────────
//
// Day 4's @DataJpaTest ran against H2 — fast, but H2 is not Postgres.
// Differences that H2 can silently paper over:
//   - SQL dialect quirks (date/time functions, string case sensitivity)
//   - How @Enumerated(STRING) columns are actually stored and compared
//   - Identity/sequence generation strategy behavior
//   - Constraint enforcement (NOT NULL, length limits) at the DB level
//
// Testcontainers solves this by spinning up a REAL, throwaway PostgreSQL
// Docker container just for this test class, run against the exact same
// repository code Day 4 tested. If it's green here, the query works against
// the actual database engine production will run — not just an emulation
// of it.
//
// @SpringBootTest loads the FULL application context (unlike @DataJpaTest,
// which loads only the JPA slice) — that's the right choice for genuine
// integration tests, where we want confidence in the real wiring.
@SpringBootTest
@Testcontainers
@DisplayName("TaskRepository — Testcontainers Integration Test (real PostgreSQL)")
class TaskRepositoryIntegrationTest {

    // ── The container ───────────────────────────────────────────────────────
    // @Container + @Testcontainers manages the container lifecycle for us:
    // started once before all tests in this class, stopped after.
    //
    // Using a pinned image tag (not "latest") keeps the test reproducible —
    // "works today, mysteriously breaks next month" is exactly what pinning
    // avoids.
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("taskdb_test")
            .withUsername("test")
            .withPassword("test");

    // ── @DynamicPropertySource ──────────────────────────────────────────────
    // The container picks a random host port at startup, so we can't know
    // spring.datasource.url ahead of time via application.yml. This hook
    // runs AFTER the container starts but BEFORE the Spring context boots,
    // injecting the real, resolved JDBC URL/credentials into the context —
    // overriding whatever is in application.yml for this test run only.
    @DynamicPropertySource
    static void configureDatasource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private TaskRepository taskRepository;

    @BeforeEach
    void setUp() {
        // Full integration test — no TestEntityManager shortcut here. We go
        // through the repository itself, the same way the real app would,
        // and clean up explicitly since @SpringBootTest is not automatically
        // transactional/rolled-back the way @DataJpaTest is.
        taskRepository.deleteAll();

        taskRepository.save(Task.builder()
                .title("Fix login bug")
                .status(Task.Status.TODO)
                .priority(Task.Priority.HIGH)
                .ownerEmail("alice@example.com")
                .dueDate(LocalDate.now().plusDays(5))
                .build());

        taskRepository.save(Task.builder()
                .title("Write integration tests")
                .status(Task.Status.IN_PROGRESS)
                .priority(Task.Priority.MEDIUM)
                .ownerEmail("alice@example.com")
                .dueDate(LocalDate.now().plusDays(10))
                .build());

        taskRepository.save(Task.builder()
                .title("Set up CI pipeline")
                .status(Task.Status.DONE)
                .priority(Task.Priority.HIGH)
                .ownerEmail("alice@example.com")
                .dueDate(LocalDate.now().minusDays(2))
                .build());

        taskRepository.save(Task.builder()
                .title("Renew SSL certificate")
                .status(Task.Status.TODO)
                .priority(Task.Priority.HIGH)
                .ownerEmail("bob@example.com")
                .dueDate(LocalDate.now().minusDays(3))
                .build());
    }

    @Test
    @DisplayName("container starts and reports itself as running")
    void container_isRunningAndReachable() {
        assertThat(postgres.isRunning()).isTrue();
    }

    @Test
    @DisplayName("save() + findById() round-trip against real Postgres")
    void saveAndFindById_roundTripsAgainstRealPostgres() {
        Task saved = taskRepository.save(Task.builder()
                .title("Deploy to production")
                .status(Task.Status.TODO)
                .priority(Task.Priority.HIGH)
                .ownerEmail("carol@example.com")
                .build());

        Task found = taskRepository.findById(saved.getId()).orElseThrow();

        assertThat(found.getTitle()).isEqualTo("Deploy to production");
        assertThat(found.getCreatedAt()).isNotNull(); // @PrePersist ran
    }

    @Test
    @DisplayName("findByStatusAndPriority() works against real Postgres dialect")
    void findByStatusAndPriority_worksAgainstRealPostgres() {
        List<Task> result = taskRepository.findByStatusAndPriority(
                Task.Status.TODO, Task.Priority.HIGH);

        assertThat(result).hasSize(2)
                .extracting(Task::getTitle)
                .containsExactlyInAnyOrder("Fix login bug", "Renew SSL certificate");
    }

    @Test
    @DisplayName("findByTitleContainingIgnoreCase() case-insensitive match against real Postgres " +
            "(Postgres LIKE is case-SENSITIVE by default — this proves Hibernate compensates)")
    void findByTitleContainingIgnoreCase_isTrulyCaseInsensitiveOnPostgres() {
        List<Task> result = taskRepository.findByTitleContainingIgnoreCase("SSL");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Renew SSL certificate");

        // Lowercase query, still matches — this is the exact scenario H2's
        // default case-insensitivity could mask a bug in, since Postgres'
        // plain LIKE would NOT match "ssl" against "SSL" without Hibernate
        // correctly emitting a LOWER()-wrapped comparison (or ILIKE).
        List<Task> lowercaseResult = taskRepository.findByTitleContainingIgnoreCase("ssl");
        assertThat(lowercaseResult).hasSize(1);
    }

    @Test
    @DisplayName("findActiveTasksForOwner() custom JPQL ordering holds against real Postgres")
    void findActiveTasksForOwner_ordersCorrectlyOnRealPostgres() {
        List<Task> active = taskRepository.findActiveTasksForOwner(
                "alice@example.com", Task.Status.DONE);

        assertThat(active).hasSize(2);
        assertThat(active.get(0).getTitle()).isEqualTo("Fix login bug");
        assertThat(active.get(1).getTitle()).isEqualTo("Write integration tests");
    }

    @Test
    @DisplayName("NOT NULL constraint on title is enforced at the real database level")
    void nullTitle_violatesNotNullConstraint_onRealPostgres() {
        Task invalid = Task.builder()
                .title(null) // violates @Column(nullable = false)
                .status(Task.Status.TODO)
                .priority(Task.Priority.LOW)
                .ownerEmail("dave@example.com")
                .build();

        org.junit.jupiter.api.Assertions.assertThrows(
                org.springframework.dao.DataIntegrityViolationException.class,
                () -> taskRepository.saveAndFlush(invalid));
    }
}
