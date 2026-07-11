package com.JavaBackEnd.spring_boot_journey_week3_day6.seeder;

import com.JavaBackEnd.spring_boot_journey_week3_day6.entity.Tag;
import com.JavaBackEnd.spring_boot_journey_week3_day6.entity.Task;
import com.JavaBackEnd.spring_boot_journey_week3_day6.entity.TaskPriority;
import com.JavaBackEnd.spring_boot_journey_week3_day6.entity.TaskStatus;
import com.JavaBackEnd.spring_boot_journey_week3_day6.repository.TagRepository;
import com.JavaBackEnd.spring_boot_journey_week3_day6.repository.TaskRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Component
public class DataSeeder {

    private final TaskRepository taskRepository;
    private final TagRepository tagRepository;

    public DataSeeder(TaskRepository taskRepository, TagRepository tagRepository) {
        this.taskRepository = taskRepository;
        this.tagRepository = tagRepository;
    }

    @PostConstruct
    @Transactional
    public void seed() {
        if (taskRepository.count() == 0) {

            System.out.println("\n🌱 Seeding database with tasks and tags...");

            // Create and save tags
            Tag backend = tagRepository.save(new Tag("backend", "Backend"));
            Tag urgent = tagRepository.save(new Tag("urgent", "Urgent"));
            Tag bug = tagRepository.save(new Tag("bug", "Bug fix"));
            Tag doc = tagRepository.save(new Tag("documentation", "Documentation"));

            // Create tasks and add tags (no cascade - tags already saved)
            Task task1 = new Task(
                "Build REST API",
                "Implement CRUD operations",
                TaskStatus.IN_PROGRESS,
                TaskPriority.HIGH,
                LocalDate.now().plusDays(2)
            );
            task1.getTags().add(backend);
            task1.getTags().add(urgent);
            taskRepository.save(task1);

            Task task2 = new Task(
                "Fix login bug",
                "JWT token expiry issue",
                TaskStatus.TODO,
                TaskPriority.CRITICAL,
                LocalDate.now().plusDays(1)
            );
            task2.getTags().add(backend);
            task2.getTags().add(bug);
            task2.getTags().add(urgent);
            taskRepository.save(task2);

            Task task3 = new Task(
                "Write documentation",
                "API documentation",
                TaskStatus.TODO,
                TaskPriority.MEDIUM,
                LocalDate.now().plusDays(7)
            );
            task3.getTags().add(doc);
            taskRepository.save(task3);

            System.out.println("✅ Seeded " + tagRepository.count() + " tags and " + taskRepository.count() + " tasks.");
        }
    }
}