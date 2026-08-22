package com.JavaBackEnd.spring_boot_journey_week8_day5.repository;

import com.JavaBackEnd.spring_boot_journey_week8_day5.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

// ── Layer 3 of 4 — @DataJpaTest ─────────────────────────────────────────────
// Loads only entities + Spring Data repositories against an embedded H2
// instance. Fixtures are set up via TestEntityManager (not the repository
// under test) to keep setup independent of what's being verified. Each
// test runs in its own rolled-back transaction — no manual cleanup needed.
@DataJpaTest
@DisplayName("TaskRepository (@DataJpaTest)")
class TaskRepositoryTest {

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

        entityManager.clear();
    }

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
        assertThat(entityManager.find(Task.class, saved.getId()).getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("findByStatus() returns only tasks with matching status")
    void findByStatus_returnsMatchingTasks() {
        List<Task> todos = taskRepository.findByStatus(Task.Status.TODO);

        assertThat(todos).hasSize(3)
                .extracting(Task::getTitle)
                .containsExactlyInAnyOrder(
                        "Fix login bug", "Update README", "Renew SSL certificate");
    }

    @Test
    @DisplayName("findByPriority() returns only tasks with matching priority")
    void findByPriority_returnsMatchingTasks() {
        assertThat(taskRepository.findByPriority(Task.Priority.HIGH))
                .hasSize(3)
                .allMatch(t -> t.getPriority() == Task.Priority.HIGH);
    }

    @Test
    @DisplayName("findByOwnerEmail() returns only that owner's tasks")
    void findByOwnerEmail_returnsOnlyOwnersTasks() {
        assertThat(taskRepository.findByOwnerEmail("alice@example.com"))
                .hasSize(3)
                .allMatch(t -> t.getOwnerEmail().equals("alice@example.com"));
    }

    @Test
    @DisplayName("findByStatusAndPriority() combines both conditions (AND, not OR)")
    void findByStatusAndPriority_combinesConditions() {
        List<Task> result = taskRepository.findByStatusAndPriority(
                Task.Status.TODO, Task.Priority.HIGH);

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
    @DisplayName("findByDueDateBefore() returns only overdue tasks")
    void findByDueDateBefore_returnsOverdueTasks() {
        assertThat(taskRepository.findByDueDateBefore(LocalDate.now()))
                .hasSize(2)
                .extracting(Task::getTitle)
                .containsExactlyInAnyOrder("Set up CI pipeline", "Renew SSL certificate");
    }

    @Test
    @DisplayName("countByStatus() returns the correct count without loading entities")
    void countByStatus_returnsCorrectCount() {
        assertThat(taskRepository.countByStatus(Task.Status.DONE)).isEqualTo(1);
        assertThat(taskRepository.countByStatus(Task.Status.TODO)).isEqualTo(3);
    }

    @Test
    @DisplayName("findActiveTasksForOwner() excludes DONE and orders by dueDate ascending")
    void findActiveTasksForOwner_excludesDone_andOrdersByDueDate() {
        List<Task> active = taskRepository.findActiveTasksForOwner(
                "alice@example.com", Task.Status.DONE);

        assertThat(active).hasSize(2);
        assertThat(active.get(0).getTitle()).isEqualTo("Fix login bug");
        assertThat(active.get(1).getTitle()).isEqualTo("Write repository tests");
    }

    @Test
    @DisplayName("findActiveTasksForOwner() returns empty list for an owner with no tasks")
    void findActiveTasksForOwner_returnsEmptyList_forUnknownOwner() {
        assertThat(taskRepository.findActiveTasksForOwner("nobody@example.com", Task.Status.DONE))
                .isEmpty();
    }
}
