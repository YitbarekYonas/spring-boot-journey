package com.JavaBackEnd.library.repository;

import com.JavaBackEnd.library.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Member> findByActive(boolean active);

    List<Member> findByNameContainingIgnoreCase(String name);

    long countByActive(boolean active);
}