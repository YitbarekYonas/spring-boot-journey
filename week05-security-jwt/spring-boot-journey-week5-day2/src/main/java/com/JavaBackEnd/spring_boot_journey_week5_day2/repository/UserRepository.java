package com.JavaBackEnd.spring_boot_journey_week5_day2.repository;

import com.JavaBackEnd.spring_boot_journey_week5_day2.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}