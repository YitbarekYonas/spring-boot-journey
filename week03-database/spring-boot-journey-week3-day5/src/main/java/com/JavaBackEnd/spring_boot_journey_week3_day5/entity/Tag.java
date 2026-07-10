package com.JavaBackEnd.spring_boot_journey_week3_day5.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tags")
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(length = 200)
    private String description;

    @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
    private List<Task> tasks = new ArrayList<>();

    protected Tag() {}

    public Tag(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Long getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<Task> getTasks() { return tasks; }

    // Internal sync helpers - package-private
    void addTaskInternal(Task task) {
        if (!tasks.contains(task)) {
            tasks.add(task);
        }
    }

    void removeTaskInternal(Task task) {
        tasks.remove(task);
    }
}