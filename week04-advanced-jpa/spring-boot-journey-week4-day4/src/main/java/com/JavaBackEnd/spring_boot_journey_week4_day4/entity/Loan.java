package com.JavaBackEnd.spring_boot_journey_week4_day4.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "loans")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private LoanStatus status;

    @Column(name = "loan_date", nullable = false, updatable = false)
    private LocalDate loanDate;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "return_date")
    private LocalDate returnDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(length = 500)
    private String notes;

    protected Loan() {}

    public Loan(Book book, Member member, int loanDurationDays) {
        this.book = book;
        this.member = member;
        this.status = LoanStatus.ACTIVE;
        this.loanDate = LocalDate.now();
        this.dueDate = LocalDate.now().plusDays(loanDurationDays);
        this.createdAt = LocalDateTime.now();
    }

    public boolean isOverdue() {
        return status == LoanStatus.ACTIVE
               && LocalDate.now().isAfter(dueDate);
    }

    public void returnBook() {
        if (status != LoanStatus.ACTIVE && status != LoanStatus.OVERDUE) {
            throw new IllegalStateException(
                "Cannot return a loan with status: " + status);
        }
        this.status = LoanStatus.RETURNED;
        this.returnDate = LocalDate.now();
    }

    public void markOverdue() {
        if (status == LoanStatus.ACTIVE && isOverdue()) {
            this.status = LoanStatus.OVERDUE;
        }
    }

    public Long getId() { return id; }
    public Book getBook() { return book; }
    public Member getMember() { return member; }
    public LoanStatus getStatus() { return status; }
    public void setStatus(LoanStatus status) { this.status = status; }
    public LocalDate getLoanDate() { return loanDate; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Loan)) return false;
        Loan loan = (Loan) o;
        return id != null && Objects.equals(id, loan.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Loan{id=" + id
               + ", status=" + status
               + ", dueDate=" + dueDate + "}";
    }
}