package com.connectly.security;

import com.connectly.entity.Profile;
import com.connectly.entity.User;
import com.connectly.enums.Role;
import com.connectly.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class JwtAuthenticationIntegrationTest {

    private static final String ISSUER =
            "https://connectly.example";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtEncoder jwtEncoder;

    private User createTestUser(Role role) {

        User user = new User();
        user.setUsername("test" + role.name().toLowerCase());
        user.setEmail(role.name().toLowerCase() + "@example.com");
        user.setPassword("encodedPassword");
        user.setRole(role);
        user.setEnabled(true);

        Profile profile = new Profile();
        profile.setFirstName("Test");
        profile.setLastName("User");

        user.setProfile(profile);

        return userRepository.saveAndFlush(user);
    }

    private String generateToken(
            String email,
            Role role,
            Instant issuedAt,
            Instant expiresAt
    ) {

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(ISSUER)
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .subject(email)
                .claim("role", role.name())
                .build();

        return jwtEncoder.encode(
                JwtEncoderParameters.from(
                        JwsHeader.with(MacAlgorithm.HS256).build(),
                        claims
                )
        ).getTokenValue();
    }

    private String generateValidToken(
            String email,
            Role role
    ) {

        Instant now = Instant.now();

        return generateToken(
                email,
                role,
                now,
                now.plusSeconds(900)
        );
    }

    @Test
    void profile_shouldReturnUnauthorizedWithoutToken()
            throws Exception {

        mockMvc.perform(get("/api/users/me/profile"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void profile_shouldReturnProfileWithValidUserToken()
            throws Exception {

        User user = createTestUser(Role.USER);

        String token = generateValidToken(
                user.getEmail(),
                Role.USER
        );

        mockMvc.perform(get("/api/users/me/profile")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Test"))
                .andExpect(jsonPath("$.lastName").value("User"));
    }

    @Test
    void profile_shouldReturnProfileWithValidAdminToken()
            throws Exception {

        User user = createTestUser(Role.ADMIN);

        String token = generateValidToken(
                user.getEmail(),
                Role.ADMIN
        );

        mockMvc.perform(get("/api/users/me/profile")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void adminEndpoint_shouldReturnUnauthorizedWithoutToken()
            throws Exception {

        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void adminEndpoint_shouldReturnForbiddenWithUserToken()
            throws Exception {

        User user = createTestUser(Role.USER);

        String token = generateValidToken(
                user.getEmail(),
                Role.USER
        );

        mockMvc.perform(get("/api/admin/users")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminEndpoint_shouldReturnUsersWithAdminToken()
            throws Exception {

        User user = createTestUser(Role.ADMIN);

        String token = generateValidToken(
                user.getEmail(),
                Role.ADMIN
        );

        mockMvc.perform(get("/api/admin/users")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void protectedEndpoint_shouldReturnUnauthorizedWithInvalidToken()
            throws Exception {

        mockMvc.perform(get("/api/users/me/profile")
                        .header(
                                "Authorization",
                                "Bearer invalid.jwt.token"
                        ))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedEndpoint_shouldReturnUnauthorizedWithExpiredToken()
            throws Exception {

        Instant now = Instant.now();

        String token = generateToken(
                "expired@example.com",
                Role.USER,
                now.minusSeconds(120),
                now.minusSeconds(60)
        );

        mockMvc.perform(get("/api/users/me/profile")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }
}
