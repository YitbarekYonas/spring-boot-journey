package com.JavaBackEnd.spring_boot_journey_week4_day1.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(
    name = "books",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_books_isbn", columnNames = {"isbn"})
    }
)
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 300)
    private String title;

    @Column(nullable = false, unique = true, length = 20)
    private String isbn;

    @Column(name = "publish_date")
    private LocalDate publishDate;

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "total_copies", nullable = false)
    private int totalCopies;

    @Column(name = "available_copies", nullable = false)
    private int availableCopies;

    @Column(length = 100)
    private String genre;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private Author author;

    @JsonIgnore
    @OneToMany(
        mappedBy = "book",
        fetch = FetchType.LAZY,
        cascade = {CascadeType.PERSIST, CascadeType.MERGE}
    )
    private List<Loan> loans = new ArrayList<>();

    protected Book() {}

    public Book(String title, String isbn, LocalDate publishDate,
                BigDecimal price, int totalCopies, String genre) {
        this.title = title;
        this.isbn = isbn;
        this.publishDate = publishDate;
        this.price = price;
        this.totalCopies = totalCopies;
        this.availableCopies = totalCopies;
        this.genre = genre;
        this.createdAt = LocalDateTime.now();
    }

    public boolean isAvailable() {
        return availableCopies > 0;
    }

    public void checkout() {
        if (availableCopies <= 0) {
            throw new IllegalStateException(
                "No copies available for: " + title);
        }
        availableCopies--;
    }

    public void returnCopy() {
        if (availableCopies >= totalCopies) {
            throw new IllegalStateException(
                "All copies already returned for: " + title);
        }
        availableCopies++;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public LocalDate getPublishDate() { return publishDate; }
    public void setPublishDate(LocalDate publishDate) {
        this.publishDate = publishDate;
    }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public int getTotalCopies() { return totalCopies; }
    public void setTotalCopies(int totalCopies) {
        this.totalCopies = totalCopies;
    }
    public int getAvailableCopies() { return availableCopies; }
    public void setAvailableCopies(int availableCopies) {
        this.availableCopies = availableCopies;
    }
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public Author getAuthor() { return author; }
    public void setAuthor(Author author) { this.author = author; }

    @JsonIgnore
    public List<Loan> getLoans() { return loans; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Book)) return false;
        Book book = (Book) o;
        return Objects.equals(isbn, book.isbn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isbn);
    }

    @Override
    public String toString() {
        return "Book{id=" + id
               + ", title='" + title + "'"
               + ", isbn='" + isbn + "'"
               + ", available=" + availableCopies + "/" + totalCopies + "}";
    }
}