package com.JavaBackEnd.spring_boot_journey_week3_day6.repository;

import com.JavaBackEnd.spring_boot_journey_week3_day6.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByName(String name);
}