package com.JavaBackEnd.spring_boot_journey_week6_day7;

import com.JavaBackEnd.spring_boot_journey_week6_day7.entity.Task;
import com.JavaBackEnd.spring_boot_journey_week6_day7.repository.TaskRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;

@SpringBootApplication
@Slf4j
public class Week6Day7Application {

    public static void main(String[] args) {
        SpringApplication.run(Week6Day7Application.class, args);
    }

    @Bean
    CommandLineRunner seed(TaskRepository repo) {
        return args -> {
            repo.save(Task.builder().title("Setup CI/CD pipeline")
                    .description("Configure GitHub Actions for the project")
                    .priority(Task.Priority.HIGH).status(Task.Status.IN_PROGRESS)
                    .dueDate(LocalDate.now().plusDays(5))
                    .ownerEmail("alice@example.com").build());

            repo.save(Task.builder().title("Write unit tests")
                    .description("Cover service layer with JUnit 5 + Mockito")
                    .priority(Task.Priority.HIGH).status(Task.Status.TODO)
                    .dueDate(LocalDate.now().plusDays(3))
                    .ownerEmail("alice@example.com").build());

            repo.save(Task.builder().title("Update API documentation")
                    .priority(Task.Priority.LOW).status(Task.Status.TODO)
                    .dueDate(LocalDate.now().plusDays(10))
                    .ownerEmail("bob@example.com").build());

            repo.save(Task.builder().title("Fix login bug")
                    .description("JWT token not refreshing correctly")
                    .priority(Task.Priority.HIGH).status(Task.Status.DONE)
                    .ownerEmail("alice@example.com").build());

            log.info("✅ Seed complete — 4 tasks inserted");
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            log.info("Test the full Week 6 combination:");
            log.info("  GET    /api/tasks                              → all tasks (paginated)");
            log.info("  GET    /api/tasks/by-owner?owner=alice@...     → filter by owner");
            log.info("  GET    /api/tasks/by-status?status=TODO        → filter by status");
            log.info("  GET    /api/tasks/99                           → 404 error response");
            log.info("  POST   /api/tasks  (missing title)             → 400 + fieldErrors");
            log.info("  POST   /api/tasks  (duplicate title+owner)     → 409 conflict");
            log.info("  PATCH  /api/tasks/1?caller=bob@...             → 403 forbidden");
            log.info("  DELETE /api/tasks/1?caller=alice@...           → 204 success");
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        };
    }
}
