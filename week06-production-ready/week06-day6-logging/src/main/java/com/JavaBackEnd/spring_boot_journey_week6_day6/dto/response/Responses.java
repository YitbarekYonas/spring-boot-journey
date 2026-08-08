package com.JavaBackEnd.spring_boot_journey_week6_day6.dto.response;

import com.JavaBackEnd.spring_boot_journey_week6_day6.entity.Book;
import com.JavaBackEnd.spring_boot_journey_week6_day6.entity.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// All three response DTOs in one file to minimize file count.
// In a real project you'd split these into separate files.

// ── UserResponse ───────────────────────────────────────────────────────────
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
class UserResponse {
    private final Long id;
    private final String name;
    private final String email;
    private final String role;
    private final boolean enabled;
    private final LocalDateTime createdAt;

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .enabled(user.isEnabled())
                .createdAt(user.getCreatedAt())
                .build();
    }
}

// ── BookResponse ───────────────────────────────────────────────────────────
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
class BookResponse {
    private final Long id;
    private final String title;
    private final String isbn;
    private final String genre;
    private final String authorName;
    private final BigDecimal price;
    private final int totalCopies;
    private final int availableCopies;
    private final boolean available;
    private final LocalDateTime createdAt;

    public static BookResponse from(Book book) {
        return BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .isbn(book.getIsbn())
                .genre(book.getGenre())
                .authorName(book.getAuthorName())
                .price(book.getPrice())
                .totalCopies(book.getTotalCopies())
                .availableCopies(book.getAvailableCopies())
                .available(book.isAvailable())
                .createdAt(book.getCreatedAt())
                .build();
    }
}

// ── BookSummaryResponse ────────────────────────────────────────────────────
@Getter
@Builder
class BookSummaryResponse {
    private final Long id;
    private final String title;
    private final String authorName;
    private final BigDecimal price;
    private final boolean available;

    public static BookSummaryResponse from(Book book) {
        return BookSummaryResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .authorName(book.getAuthorName())
                .price(book.getPrice())
                .available(book.isAvailable())
                .build();
    }
}
