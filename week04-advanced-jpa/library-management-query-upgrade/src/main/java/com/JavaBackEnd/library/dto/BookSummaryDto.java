package com.JavaBackEnd.library.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class BookSummaryDto {

    private final Long id;
    private final String title;
    private final String isbn;
    private final String genre;
    private final int availableCopies;
    private final BigDecimal price;
    private final String authorFirstName;
    private final String authorLastName;
    private final LocalDate publishDate;

    public BookSummaryDto(Long id,
                          String title,
                          String isbn,
                          String genre,
                          int availableCopies,
                          BigDecimal price,
                          String authorFirstName,
                          String authorLastName,
                          LocalDate publishDate) {
        this.id = id;
        this.title = title;
        this.isbn = isbn;
        this.genre = genre;
        this.availableCopies = availableCopies;
        this.price = price;
        this.authorFirstName = authorFirstName;
        this.authorLastName = authorLastName;
        this.publishDate = publishDate;
    }

    public String getAuthorFullName() {
        return authorFirstName + " " + authorLastName;
    }

    public boolean isAvailable() {
        return availableCopies > 0;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getIsbn() { return isbn; }
    public String getGenre() { return genre; }
    public int getAvailableCopies() { return availableCopies; }
    public BigDecimal getPrice() { return price; }
    public String getAuthorFirstName() { return authorFirstName; }
    public String getAuthorLastName() { return authorLastName; }
    public LocalDate getPublishDate() { return publishDate; }
}