package com.JavaBackEnd.spring_boot_journey_week6_day4.dto.response;

import com.JavaBackEnd.spring_boot_journey_week6_day4.entity.Book;

import java.math.BigDecimal;

// ── BookSummaryResponse ────────────────────────────────────────────────────
// Compact DTO — used for GET /api/books (list endpoint).
// Same entity, different DTO, different use case.
//
// Key lesson: One entity can have MULTIPLE response DTOs.
// The list view only needs title, author, price, availability.
// The detail view (BookResponse) shows everything.
// Sending full BookResponse in a list of 100 books wastes bandwidth.
// Sending only what the list UI needs = good API design.
public class BookSummaryResponse {

    private final Long id;
    private final String title;
    private final String authorName;
    private final BigDecimal price;
    private final boolean available;    // client can show "Available" badge

    public static BookSummaryResponse from(Book book) {
        return new BookSummaryResponse(book);
    }

    private BookSummaryResponse(Book book) {
        this.id = book.getId();
        this.title = book.getTitle();
        this.authorName = book.getAuthorName();
        this.price = book.getPrice();
        this.available = book.isAvailable();
        // NOT included: isbn, genre, totalCopies, availableCopies, createdAt
        // The list view doesn't need these.
    }

    public Long getId()           { return id; }
    public String getTitle()      { return title; }
    public String getAuthorName() { return authorName; }
    public BigDecimal getPrice()  { return price; }
    public boolean isAvailable()  { return available; }
}
