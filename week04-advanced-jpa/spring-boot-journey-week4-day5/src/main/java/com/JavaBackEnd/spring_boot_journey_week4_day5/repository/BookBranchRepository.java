package com.JavaBackEnd.spring_boot_journey_week4_day5.repository;

import com.JavaBackEnd.spring_boot_journey_week4_day5.entity.BookBranch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookBranchRepository extends JpaRepository<BookBranch, Long> {

    List<BookBranch> findByBookTitle(String bookTitle);

    Optional<BookBranch> findByBranchNameAndBookTitle(String branchName, String bookTitle);
}