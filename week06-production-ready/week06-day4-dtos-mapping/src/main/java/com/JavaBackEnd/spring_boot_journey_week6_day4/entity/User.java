package com.JavaBackEnd.spring_boot_journey_week6_day4.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

// ── Why NOT @Data on a JPA entity ─────────────────────────────────────────
// @Data generates equals/hashCode using ALL fields including id.
// Before an entity is saved, id = null → equals/hashCode behave unexpectedly
// in Sets and Maps. It also generates toString() which can trigger lazy
// loading on relationships and cause LazyInitializationException or N+1.
// Instead: use @Getter @Setter @Builder individually.
@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;       // BCrypt hash — NEVER expose in API response

    @Column(nullable = false)
    private String role;           // "ADMIN" or "USER"

    @Column(nullable = false)
    @Builder.Default
    private boolean enabled = true;

    // Spring Security internal flags — internal model, not for clients
    @Builder.Default
    private boolean accountNonExpired = true;

    @Builder.Default
    private boolean accountNonLocked = true;

    @Builder.Default
    private boolean credentialsNonExpired = true;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Audit — who created/last modified (simplified without Spring Data Auditing)
    private String createdBy;
    private String lastModifiedBy;
}
