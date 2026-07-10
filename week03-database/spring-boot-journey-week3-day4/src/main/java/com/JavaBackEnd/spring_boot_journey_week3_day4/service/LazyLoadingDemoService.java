package com.JavaBackEnd.spring_boot_journey_week3_day4.service;

import com.JavaBackEnd.spring_boot_journey_week3_day4.entity.Task;
import com.JavaBackEnd.spring_boot_journey_week3_day4.entity.User;
import com.JavaBackEnd.spring_boot_journey_week3_day4.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LazyLoadingDemoService {

    private final TaskRepository taskRepository;

    public LazyLoadingDemoService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    // ✅ Inside transaction - LAZY loading works
    @Transactional
    public void demonstrateLazyLoadingWorks(Long taskId) {
        System.out.println("\n=== Demonstrating LAZY Loading (Inside Transaction) ===");

        Task task = taskRepository.findById(taskId).get();
        System.out.println("✅ Task loaded: " + task.getTitle());

        // Accessing user - triggers LAZY load (works because we're in @Transactional)
        User user = task.getAssignedUser();
        System.out.println("✅ User loaded: " + user.getName());
    }

    // ❌ No @Transactional - LAZY loading will fail
    public void demonstrateLazyLoadingException(Long taskId) {
        System.out.println("\n=== Demonstrating LazyInitializationException (No Transaction) ===");

        Task task = taskRepository.findById(taskId).get();
        System.out.println("✅ Task loaded: " + task.getTitle());

        try {
            // This will throw LazyInitializationException
            User user = task.getAssignedUser();
            System.out.println("❌ This line won't execute: " + user.getName());
        } catch (Exception e) {
            System.out.println("❌ Exception caught: " + e.getClass().getSimpleName());
            System.out.println("   Message: " + e.getMessage());
        }
    }

    // ✅ Fetch join - loads user in one query
    @Transactional
    public void demonstrateFetchJoin(Long taskId) {
        System.out.println("\n=== Demonstrating Fetch Join (One Query) ===");

        Task task = taskRepository.findByIdWithUser(taskId).get();
        System.out.println("✅ Task loaded: " + task.getTitle());

        // User already loaded in the same query - no extra SQL
        User user = task.getAssignedUser();
        System.out.println("✅ User loaded (already in memory): " + user.getName());
    }
}