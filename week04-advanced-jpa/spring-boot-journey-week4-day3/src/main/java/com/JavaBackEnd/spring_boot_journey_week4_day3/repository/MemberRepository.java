package com.JavaBackEnd.spring_boot_journey_week4_day3.repository;

import com.JavaBackEnd.spring_boot_journey_week4_day3.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
}