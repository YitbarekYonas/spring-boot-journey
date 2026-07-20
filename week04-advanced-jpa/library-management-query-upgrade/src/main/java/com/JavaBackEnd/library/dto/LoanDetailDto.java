package com.JavaBackEnd.library.dto;

import com.JavaBackEnd.library.entity.LoanStatus;
import java.time.LocalDate;

public class LoanDetailDto {

    private final Long id;
    private final LoanStatus status;
    private final LocalDate loanDate;
    private final LocalDate dueDate;
    private final LocalDate returnDate;
    private final String bookTitle;
    private final String bookIsbn;
    private final String authorFullName;
    private final String memberName;
    private final String memberEmail;
    private final boolean overdue;

    public LoanDetailDto(Long id,
                         LoanStatus status,
                         LocalDate loanDate,
                         LocalDate dueDate,
                         LocalDate returnDate,
                         String bookTitle,
                         String bookIsbn,
                         String authorFirstName,
                         String authorLastName,
                         String memberName,
                         String memberEmail) {
        this.id = id;
        this.status = status;
        this.loanDate = loanDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.bookTitle = bookTitle;
        this.bookIsbn = bookIsbn;
        this.authorFullName = authorFirstName + " " + authorLastName;
        this.memberName = memberName;
        this.memberEmail = memberEmail;
        this.overdue = status == LoanStatus.ACTIVE
                       && LocalDate.now().isAfter(dueDate);
    }

    public Long getId() { return id; }
    public LoanStatus getStatus() { return status; }
    public LocalDate getLoanDate() { return loanDate; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public String getBookTitle() { return bookTitle; }
    public String getBookIsbn() { return bookIsbn; }
    public String getAuthorFullName() { return authorFullName; }
    public String getMemberName() { return memberName; }
    public String getMemberEmail() { return memberEmail; }
    public boolean isOverdue() { return overdue; }
}