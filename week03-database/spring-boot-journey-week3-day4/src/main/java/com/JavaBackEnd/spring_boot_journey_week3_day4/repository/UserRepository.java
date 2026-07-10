package com.JavaBackEnd.spring_boot_journey_week3_day4.repository;

import com.JavaBackEnd.spring_boot_journey_week3_day4.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u JOIN FETCH u.tasks WHERE u.id = :id")
    Optional<User> findByIdWithTasks(@Param("id") Long id);
}