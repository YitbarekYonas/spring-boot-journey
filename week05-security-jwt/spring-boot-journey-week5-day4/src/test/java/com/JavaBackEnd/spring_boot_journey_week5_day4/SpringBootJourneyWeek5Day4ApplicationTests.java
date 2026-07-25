package com.JavaBackEnd.spring_boot_journey_week5_day4;

import com.JavaBackEnd.spring_boot_journey_week5_day4.config.JwtProperties;
import com.JavaBackEnd.spring_boot_journey_week5_day4.entity.User;
import com.JavaBackEnd.spring_boot_journey_week5_day4.entity.UserRole;
import com.JavaBackEnd.spring_boot_journey_week5_day4.security.CustomUserDetails;
import com.JavaBackEnd.spring_boot_journey_week5_day4.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class SpringBootJourneyWeek5Day4ApplicationTests {

    private JwtService jwtService;
    private CustomUserDetails userDetails;

    @BeforeEach
    void setUp() throws Exception {
        JwtProperties props = new JwtProperties();
        props.setSecret("testSecretKeyForJwtServiceUnitTestMinimum256Bits!!");
        props.setExpirationMs(3600000L);
        props.setRefreshExpirationMs(86400000L);

        jwtService = new JwtService(props);

        User user = new User(
            "Alice Admin",
            "alice@library.com",
            "hashedPassword",
            UserRole.ADMIN
        );
        // Set ID via reflection since there's no setter
        Field idField = User.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(user, 1L);

        userDetails = new CustomUserDetails(user);
    }

    @Test
    void generateToken_returnsNonNullToken() {
        String token = jwtService.generateToken(userDetails);

        assertNotNull(token);
        assertEquals(3, token.split("\\.").length,
            "JWT should have exactly 3 segments (header.payload.signature)");
    }

    @Test
    void extractEmail_returnsCorrectEmail() {
        String token = jwtService.generateToken(userDetails);

        String extractedEmail = jwtService.extractEmail(token);

        assertEquals("alice@library.com", extractedEmail);
    }

    @Test
    void extractRole_returnsCorrectRole() {
        String token = jwtService.generateToken(userDetails);

        String extractedRole = jwtService.extractRole(token);

        assertEquals("ADMIN", extractedRole);
    }

    @Test
    void isTokenValid_withCorrectUserDetails_returnsTrue() {
        String token = jwtService.generateToken(userDetails);

        boolean valid = jwtService.isTokenValid(token, userDetails);

        assertTrue(valid);
    }

    @Test
    void isTokenValid_withTamperedToken_returnsFalse() {
        String token = jwtService.generateToken(userDetails);

        String[] parts = token.split("\\.");
        String tamperedToken = parts[0] + ".TAMPERED_PAYLOAD." + parts[2];

        assertFalse(jwtService.isTokenValid(tamperedToken, userDetails));
    }

    @Test
    void isTokenExpired_withFreshToken_returnsFalse() {
        String token = jwtService.generateToken(userDetails);

        assertFalse(jwtService.isTokenExpired(token));
    }

    @Test
    void extractExpiration_isInTheFuture() {
        String token = jwtService.generateToken(userDetails);

        Date expiration = jwtService.extractExpiration(token);

        assertTrue(expiration.after(new Date()),
            "Token expiration should be in the future");
    }
}