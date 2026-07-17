package com.JavaBackEnd.spring_boot_journey_week4_day4.dto;

import java.math.BigDecimal;

public class BookSummaryDto {

    private final Long id;
    private final String title;
    private final String genre;
    private final int availableCopies;
    private final BigDecimal price;
    private final String authorFirstName;
    private final String authorLastName;

    // Constructor - JPQL calls this directly
    public BookSummaryDto(Long id,
                          String title,
                          String genre,
                          int availableCopies,
                          BigDecimal price,
                          String authorFirstName,
                          String authorLastName) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.availableCopies = availableCopies;
        this.price = price;
        this.authorFirstName = authorFirstName;
        this.authorLastName = authorLastName;
    }

    // Getters
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getGenre() { return genre; }
    public int getAvailableCopies() { return availableCopies; }
    public BigDecimal getPrice() { return price; }
    public String getAuthorFirstName() { return authorFirstName; }
    public String getAuthorLastName() { return authorLastName; }

    // Computed field - plain Java (no SpEL needed)
    public String getAuthorFullName() {
        return authorFirstName + " " + authorLastName;
    }

    public boolean isAvailable() {
        return availableCopies > 0;
    }

    @Override
    public String toString() {
        return "BookSummaryDto{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + getAuthorFullName() + '\'' +
                ", available=" + isAvailable() +
                '}';
    }
}