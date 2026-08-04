package com.JavaBackEnd.spring_boot_journey_week6_day4.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

// ── CreateBookRequest ──────────────────────────────────────────────────────
// REQUEST DTO — represents what the client sends to CREATE a book.
// Annotated with @Valid constraints so Spring validates before the
// controller method body even runs.
//
// Why separate from BookResponse?
// Request and response are different shapes:
//   Request:  client sends title, isbn, price → we validate and create
//   Response: server sends id, title, isbn, available, createdAt → we control
//
// The request has NO id (we generate it), NO createdAt (we set it),
// NO available flag (computed). The response has all of these.
// They are fundamentally different objects, so they get different classes.
@Getter
@NoArgsConstructor
public class CreateBookRequest {

    @NotBlank(message = "Title is required")
    @Size(min = 2, max = 200, message = "Title must be between 2 and 200 characters")
    private String title;

    @NotBlank(message = "ISBN is required")
    @Pattern(regexp = "^(?:\\d{10}|\\d{13}|978-\\d{10})$",
             message = "Invalid ISBN format")
    private String isbn;

    @NotBlank(message = "Genre is required")
    private String genre;

    @NotBlank(message = "Author name is required")
    private String authorName;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Price format invalid")
    private BigDecimal price;

    @Min(value = 1, message = "Must have at least 1 copy")
    private int totalCopies = 1;
}
