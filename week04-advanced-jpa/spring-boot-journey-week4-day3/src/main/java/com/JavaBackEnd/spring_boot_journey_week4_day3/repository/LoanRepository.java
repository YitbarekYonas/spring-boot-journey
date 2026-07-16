package com.JavaBackEnd.spring_boot_journey_week4_day3.repository;

import com.JavaBackEnd.spring_boot_journey_week4_day3.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
}