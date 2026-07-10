package com.JavaBackEnd.spring_boot_journey_week3_day5.seeder;

import com.JavaBackEnd.spring_boot_journey_week3_day5.entity.Tag;
import com.JavaBackEnd.spring_boot_journey_week3_day5.entity.Task;
import com.JavaBackEnd.spring_boot_journey_week3_day5.entity.TaskPriority;
import com.JavaBackEnd.spring_boot_journey_week3_day5.entity.TaskStatus;
import com.JavaBackEnd.spring_boot_journey_week3_day5.repository.TagRepository;
import com.JavaBackEnd.spring_boot_journey_week3_day5.repository.TaskRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

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
        if (tagRepository.count() == 0 && taskRepository.count() == 0) {

            System.out.println("\n🌱 Seeding database with tasks and tags...");

            // Create tags
            Tag backend = tagRepository.save(new Tag("backend", "Backend development"));
            Tag urgent = tagRepository.save(new Tag("urgent", "Urgent tasks"));
            Tag bug = tagRepository.save(new Tag("bug", "Bug fixes"));
            Tag doc = tagRepository.save(new Tag("documentation", "Documentation"));

            // Create tasks with tags
            Task task1 = new Task(
                "Build REST API",
                "Implement CRUD operations",
                TaskStatus.IN_PROGRESS,
                TaskPriority.HIGH,
                LocalDate.now().plusDays(2)
            );
            task1.addTag(backend);
            task1.addTag(urgent);
            taskRepository.save(task1);

            Task task2 = new Task(
                "Fix login bug",
                "JWT token expiry issue",
                TaskStatus.TODO,
                TaskPriority.CRITICAL,
                LocalDate.now().plusDays(1)
            );
            task2.addTag(backend);
            task2.addTag(bug);
            task2.addTag(urgent);
            taskRepository.save(task2);

            Task task3 = new Task(
                "Write documentation",
                "API documentation with examples",
                TaskStatus.TODO,
                TaskPriority.MEDIUM,
                LocalDate.now().plusDays(7)
            );
            task3.addTag(doc);
            taskRepository.save(task3);

            System.out.println("✅ Seeded " + tagRepository.count() + " tags and " + taskRepository.count() + " tasks.");
            System.out.println("📊 Join table rows: task_tags should have entries.");
        }
    }
}