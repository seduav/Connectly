package com.connectly.security;

import com.connectly.config.JwtConfig;
import com.connectly.entity.User;
import com.connectly.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;

import javax.crypto.SecretKey;
import java.security.SecureRandom;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private JwtDecoder jwtDecoder;

    @BeforeEach
    void setUp() {

        byte[] keyBytes = new byte[32];
        new SecureRandom().nextBytes(keyBytes);

        String secret = Base64.getEncoder()
                .encodeToString(keyBytes);

        JwtConfig jwtConfig = new JwtConfig();

        SecretKey secretKey = jwtConfig.jwtSecretKey(secret);

        JwtEncoder jwtEncoder = jwtConfig.jwtEncoder(secretKey);

        jwtDecoder = jwtConfig.jwtDecoder(secretKey);

        jwtService = new JwtService(jwtEncoder);

        org.springframework.test.util.ReflectionTestUtils
                .setField(jwtService, "jwtExpiration", 900L);
    }

    @Test
    void generateToken_shouldCreateValidJwt() {

        User user = new User();
        user.setId(1L);
        user.setEmail("john@example.com");
        user.setRole(Role.USER);

        String token = jwtService.generateToken(user);

        assertNotNull(token);
        assertFalse(token.isBlank());

        Jwt decodedToken = jwtDecoder.decode(token);

        assertEquals("john@example.com", decodedToken.getSubject());
        assertEquals("USER", decodedToken.getClaimAsString("role"));
        assertNotNull(decodedToken.getIssuer());
        assertEquals("https://connectly.example", decodedToken.getIssuer().toString());
    }

    @Test
    void generateToken_shouldSetCorrectExpiration() {

        User user = new User();
        user.setEmail("john@example.com");
        user.setRole(Role.USER);

        String token = jwtService.generateToken(user);

        Jwt decodedToken = jwtDecoder.decode(token);

        long lifetime = decodedToken.getExpiresAt().getEpochSecond()
                - decodedToken.getIssuedAt().getEpochSecond();

        assertEquals(900L, lifetime);
    }

    @Test
    void getExpirationTime_shouldReturnConfiguredExpiration() {

        assertEquals(900L, jwtService.getExpirationTime());
    }
}
