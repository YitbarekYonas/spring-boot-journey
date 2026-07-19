package com.JavaBackEnd.spring_boot_journey_week4_day6.repository;

import com.JavaBackEnd.spring_boot_journey_week4_day6.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
}