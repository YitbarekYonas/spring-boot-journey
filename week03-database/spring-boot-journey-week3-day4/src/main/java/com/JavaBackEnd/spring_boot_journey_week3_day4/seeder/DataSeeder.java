package com.JavaBackEnd.spring_boot_journey_week3_day4.seeder;

import com.JavaBackEnd.spring_boot_journey_week3_day4.entity.*;
import com.JavaBackEnd.spring_boot_journey_week3_day4.repository.TaskRepository;
import com.JavaBackEnd.spring_boot_journey_week3_day4.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class DataSeeder {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    public DataSeeder(UserRepository userRepository, TaskRepository taskRepository) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
    }

    @PostConstruct
    @Transactional
    public void seed() {
        if (userRepository.count() == 0 && taskRepository.count() == 0) {
            System.out.println("\n🌱 Seeding database with users and tasks...");

            // Create users
            User alice = new User("Alice", "alice@email.com", UserRole.USER);
            User bob = new User("Bob", "bob@email.com", UserRole.MANAGER);
            User charlie = new User("Charlie", "charlie@email.com", UserRole.ADMIN);

            userRepository.save(alice);
            userRepository.save(bob);
            userRepository.save(charlie);

            // Create tasks using helper method to keep both sides in sync
            Task task1 = new Task("Set up Spring Boot project",
                    "Initialize project with dependencies",
                    TaskStatus.DONE, TaskPriority.HIGH,
                    LocalDate.now().minusDays(7));
            alice.addTask(task1);

            Task task2 = new Task("Build REST API endpoints",
                    "Implement CRUD operations",
                    TaskStatus.IN_PROGRESS, TaskPriority.HIGH,
                    LocalDate.now().plusDays(2));
            alice.addTask(task2);

            Task task3 = new Task("Write Postman collection",
                    "Document and test all endpoints",
                    TaskStatus.TODO, TaskPriority.MEDIUM,
                    LocalDate.now().plusDays(5));
            bob.addTask(task3);

            Task task4 = new Task("Implement JWT authentication",
                    "Secure the API with JWT tokens",
                    TaskStatus.TODO, TaskPriority.URGENT,
                    LocalDate.now().minusDays(1));
            alice.addTask(task4);

            Task task5 = new Task("Write unit tests",
                    "Test all service methods",
                    TaskStatus.TODO, TaskPriority.LOW,
                    LocalDate.now().plusDays(10));
            bob.addTask(task5);

            // Save all tasks (cascade from users)
            taskRepository.save(task1);
            taskRepository.save(task2);
            taskRepository.save(task3);
            taskRepository.save(task4);
            taskRepository.save(task5);

            System.out.println("✅ Seeded " + userRepository.count() + " users and " +
                               taskRepository.count() + " tasks.");
        }
    }
}