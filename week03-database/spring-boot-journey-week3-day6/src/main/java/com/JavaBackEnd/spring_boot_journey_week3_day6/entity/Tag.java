package com.JavaBackEnd.spring_boot_journey_week3_day6.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "tags")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "tasks")  // Exclude bidirectional relationship
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

    public Tag(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // ✅ Correct equals/hashCode using business key
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tag)) return false;
        Tag tag = (Tag) o;
        return Objects.equals(name, tag.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    void addTaskInternal(Task task) {
        if (!tasks.contains(task)) {
            tasks.add(task);
        }
    }

    void removeTaskInternal(Task task) {
        tasks.remove(task);
    }
}