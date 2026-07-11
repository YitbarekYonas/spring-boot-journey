package com.JavaBackEnd.spring_boot_journey_week3_day6.repository;

import com.JavaBackEnd.spring_boot_journey_week3_day6.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    // Fetch join to fix LazyInitializationException
    @Query("SELECT t FROM Task t LEFT JOIN FETCH t.tags WHERE t.id = :id")
    Optional<Task> findByIdWithTags(@Param("id") Long id);

    @Query("SELECT DISTINCT t FROM Task t LEFT JOIN FETCH t.tags")
    List<Task> findAllWithTags();
}