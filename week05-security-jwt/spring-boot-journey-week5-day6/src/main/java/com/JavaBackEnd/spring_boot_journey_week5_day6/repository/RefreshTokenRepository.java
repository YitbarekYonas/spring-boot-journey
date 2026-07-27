package com.JavaBackEnd.spring_boot_journey_week5_day6.repository;

import com.JavaBackEnd.spring_boot_journey_week5_day6.entity.RefreshToken;
import com.JavaBackEnd.spring_boot_journey_week5_day6.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    List<RefreshToken> findByUser(User user);

    @Query("""
        SELECT rt FROM RefreshToken rt
        WHERE rt.user = :user
        AND rt.revoked = false
        AND rt.expiresAt > :now
        """)
    List<RefreshToken> findValidTokensByUser(
        @Param("user") User user,
        @Param("now") LocalDateTime now
    );

    @Modifying
    @Query("""
        UPDATE RefreshToken rt
        SET rt.revoked = true
        WHERE rt.user = :user
        AND rt.revoked = false
        """)
    int revokeAllUserTokens(@Param("user") User user);

    @Modifying
    @Query("""
        DELETE FROM RefreshToken rt
        WHERE rt.expiresAt < :cutoff
        """)
    int deleteExpiredTokens(@Param("cutoff") LocalDateTime cutoff);

    @Query("""
        SELECT COUNT(rt) FROM RefreshToken rt
        WHERE rt.user = :user
        AND rt.revoked = false
        AND rt.expiresAt > :now
        """)
    long countValidTokensByUser(
        @Param("user") User user,
        @Param("now") LocalDateTime now
    );
}