package com.JavaBackEnd.spring_boot_journey_week5_day1.repository;

import com.JavaBackEnd.spring_boot_journey_week5_day1.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
}