package com.JavaBackEnd.spring_boot_journey_week6_day4.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// Keeping Book simple (no separate Author entity) so we can focus fully
// on the DTO mapping concept without extra relationship complexity.
// Week 3 already covered relationships — this week is about DTOs.
@Entity
@Table(name = "books")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, unique = true)
    private String isbn;

    private String genre;

    private String authorName;   // simplified — real app: @ManyToOne Author

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    @Builder.Default
    private int totalCopies = 1;

    @Column(nullable = false)
    @Builder.Default
    private int availableCopies = 1;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    // Domain method — business logic on the entity (not in service or DTO)
    public boolean isAvailable() {
        return availableCopies > 0;
    }
}
