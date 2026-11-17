package com.connectly.controller;

import com.connectly.entity.Profile;
import com.connectly.entity.User;
import com.connectly.enums.Role;
import com.connectly.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProtectedEndpointTest {

    private static final String TEST_EMAIL =
            "protected@example.com";

    private static final String ISSUER =
            "https://connectly.example";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtEncoder jwtEncoder;

    @BeforeEach
    void setUp() {
        createTestUser(
                "protecteduser",
                TEST_EMAIL,
                Role.USER
        );
    }

    private void createTestUser(
            String username,
            String email,
            Role role
    ) {

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword("encodedPassword");
        user.setRole(role);
        user.setEnabled(true);

        Profile profile = new Profile();
        profile.setFirstName("John");
        profile.setLastName("Smith");
        profile.setBio("Test profile");

        user.setProfile(profile);

        userRepository.saveAndFlush(user);
    }

    private String generateToken(
            String email,
            Role role,
            Instant issuedAt,
            Instant expiresAt,
            String issuer
    ) {

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
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

    private String generateValidToken() {
        return generateValidToken(TEST_EMAIL, Role.USER);
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
                now.plusSeconds(900),
                ISSUER
        );
    }

    @Test
    void getMyProfile_shouldReturnUnauthorizedWithoutToken()
            throws Exception {

        mockMvc.perform(get("/api/users/me/profile"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateMyProfile_shouldReturnUnauthorizedWithoutToken()
            throws Exception {

        String request = """
                {
                    "firstName": "Jane",
                    "lastName": "Doe",
                    "bio": "Updated profile"
                }
                """;

        mockMvc.perform(put("/api/users/me/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getMyProfile_shouldReturnProfileWithValidUserToken()
            throws Exception {

        String token = generateValidToken();

        mockMvc.perform(get("/api/users/me/profile")
                        .header(
                                "Authorization",
                                "Bearer " + token
                        ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Smith"))
                .andExpect(jsonPath("$.bio").value("Test profile"));
    }

    @Test
    void getMyProfile_shouldAllowAdminAccessToOwnProfile()
            throws Exception {

        createTestUser(
                "adminuser",
                "admin@example.com",
                Role.ADMIN
        );

        String token = generateValidToken(
                "admin@example.com",
                Role.ADMIN
        );

        mockMvc.perform(get("/api/users/me/profile")
                        .header(
                                "Authorization",
                                "Bearer " + token
                        ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    void updateMyProfile_shouldUpdateProfileWithValidToken()
            throws Exception {

        String token = generateValidToken();

        String request = """
                {
                    "firstName": "Jane",
                    "lastName": "Doe",
                    "bio": "Updated profile",
                    "profilePictureUrl": "https://example.com/photo.jpg",
                    "dateOfBirth": "2000-05-15"
                }
                """;

        mockMvc.perform(put("/api/users/me/profile")
                        .header(
                                "Authorization",
                                "Bearer " + token
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.bio").value("Updated profile"))
                .andExpect(jsonPath("$.profilePictureUrl")
                        .value("https://example.com/photo.jpg"))
                .andExpect(jsonPath("$.dateOfBirth")
                        .value("2000-05-15"));
    }

    @Test
    void getMyProfile_shouldReturnUnauthorizedWithMalformedToken()
            throws Exception {

        mockMvc.perform(get("/api/users/me/profile")
                        .header(
                                "Authorization",
                                "Bearer invalid.jwt.token"
                        ))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getMyProfile_shouldReturnUnauthorizedWithExpiredToken()
            throws Exception {

        Instant now = Instant.now();

        String expiredToken = generateToken(
                TEST_EMAIL,
                Role.USER,
                now.minusSeconds(1200),
                now.minusSeconds(300),
                ISSUER
        );

        mockMvc.perform(get("/api/users/me/profile")
                        .header(
                                "Authorization",
                                "Bearer " + expiredToken
                        ))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getMyProfile_shouldReturnUnauthorizedWithIncorrectIssuer()
            throws Exception {

        Instant now = Instant.now();

        String token = generateToken(
                TEST_EMAIL,
                Role.USER,
                now,
                now.plusSeconds(900),
                "https://incorrect-issuer.example"
        );

        mockMvc.perform(get("/api/users/me/profile")
                        .header(
                                "Authorization",
                                "Bearer " + token
                        ))
                .andExpect(status().isUnauthorized());
    }
}

