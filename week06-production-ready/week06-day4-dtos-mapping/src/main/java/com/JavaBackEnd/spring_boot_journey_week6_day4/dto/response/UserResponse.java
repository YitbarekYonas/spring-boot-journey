package com.JavaBackEnd.spring_boot_journey_week6_day4.dto.response;

import com.JavaBackEnd.spring_boot_journey_week6_day4.entity.User;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

// ── What is UserResponse? ──────────────────────────────────────────────────
// This DTO is the SAFE version of the User entity.
// The client receives exactly these 5 fields — nothing else.
//
// What is EXCLUDED (and why):
//   password           → BCrypt hash — never expose even as a hash
//   accountNonExpired  → internal Spring Security flag
//   accountNonLocked   → internal Spring Security flag
//   credentialsNon...  → internal Spring Security flag
//   updatedAt          → internal change tracking
//   createdBy          → internal audit metadata
//   lastModifiedBy     → internal audit metadata
//
// This is the ENTIRE point of the DTO layer — you decide what the client sees.
@JsonInclude(JsonInclude.Include.NON_NULL)   // null fields are omitted from JSON
public class UserResponse {

    private final Long id;
    private final String name;
    private final String email;
    private final String role;
    private final boolean enabled;
    private final LocalDateTime createdAt;

    // ── Static Factory Method ─────────────────────────────────────────────
    // Called as: UserResponse.from(userEntity)
    // Centralizes entity → DTO mapping.
    // If you add a new safe field: update from() here ONCE.
    // Every controller that calls from() automatically benefits.
    public static UserResponse from(User user) {
        return new UserResponse(user);
    }

    // Private constructor — forces use of from() factory method
    private UserResponse(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.enabled = user.isEnabled();
        this.createdAt = user.getCreatedAt();
        // ← password is simply NOT mapped here
        // ← security flags are NOT mapped here
        // ← audit fields are NOT mapped here
    }

    // Getters — Jackson needs these to serialize the object to JSON
    public Long getId()                { return id; }
    public String getName()            { return name; }
    public String getEmail()           { return email; }
    public String getRole()            { return role; }
    public boolean isEnabled()         { return enabled; }
    public LocalDateTime getCreatedAt(){ return createdAt; }
}
