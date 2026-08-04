package com.JavaBackEnd.spring_boot_journey_week6_day4.dto.response;

import com.JavaBackEnd.spring_boot_journey_week6_day4.entity.Book;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// ── BookResponse ───────────────────────────────────────────────────────────
// Full detail DTO — used for GET /api/books/{id}
// Includes all fields a client needs to display a book detail page.
// Compare with BookSummaryResponse which is the compact version for lists.
//
// Notice: isAvailable() is a COMPUTED field.
// It doesn't exist as a column in the DB — it's derived from availableCopies.
// DTOs are a great place to add computed/derived fields that make the API
// response more useful for clients without polluting the entity.
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookResponse {

    private final Long id;
    private final String title;
    private final String isbn;
    private final String genre;
    private final String authorName;
    private final BigDecimal price;
    private final int totalCopies;
    private final int availableCopies;
    private final boolean available;    // computed: availableCopies > 0
    private final LocalDateTime createdAt;

    public static BookResponse from(Book book) {
        return new BookResponse(book);
    }

    private BookResponse(Book book) {
        this.id = book.getId();
        this.title = book.getTitle();
        this.isbn = book.getIsbn();
        this.genre = book.getGenre();
        this.authorName = book.getAuthorName();
        this.price = book.getPrice();
        this.totalCopies = book.getTotalCopies();
        this.availableCopies = book.getAvailableCopies();
        this.available = book.isAvailable();  // uses domain method on entity
        this.createdAt = book.getCreatedAt();
    }

    public Long getId()                   { return id; }
    public String getTitle()              { return title; }
    public String getIsbn()               { return isbn; }
    public String getGenre()              { return genre; }
    public String getAuthorName()         { return authorName; }
    public BigDecimal getPrice()          { return price; }
    public int getTotalCopies()           { return totalCopies; }
    public int getAvailableCopies()       { return availableCopies; }
    public boolean isAvailable()          { return available; }
    public LocalDateTime getCreatedAt()   { return createdAt; }
}
