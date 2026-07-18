package com.JavaBackEnd.spring_boot_journey_week4_day5.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "book_branches")
public class BookBranch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String branchName;

    @Column(name = "book_title", nullable = false, length = 200)
    private String bookTitle;

    @Column(name = "copies", nullable = false)
    private int copies;

    protected BookBranch() {}

    public BookBranch(String branchName, String bookTitle, int copies) {
        this.branchName = branchName;
        this.bookTitle = bookTitle;
        this.copies = copies;
    }

    public Long getId() { return id; }
    public String getBranchName() { return branchName; }
    public void setBranchName(String branchName) { this.branchName = branchName; }
    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }
    public int getCopies() { return copies; }
    public void setCopies(int copies) { this.copies = copies; }

    @Override
    public String toString() {
        return "BookBranch{id=" + id + ", branch='" + branchName + "', book='" + bookTitle + "', copies=" + copies + "}";
    }
}