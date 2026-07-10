package com.JavaBackEnd.spring_boot_journey_week3_day5.repository;

import com.JavaBackEnd.spring_boot_journey_week3_day5.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    Optional<Tag> findByName(String name);

    boolean existsByName(String name);

    List<Tag> findByNameContainingIgnoreCase(String keyword);

    // Find tags that have at least one task
    @Query("SELECT DISTINCT t FROM Tag t WHERE SIZE(t.tasks) > 0")
    List<Tag> findTagsInUse();
}