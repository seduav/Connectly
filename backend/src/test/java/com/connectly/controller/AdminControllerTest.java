package com.connectly.controller;

import com.connectly.entity.User;
import com.connectly.enums.Role;
import com.connectly.repository.UserRepository;
import com.connectly.util.TestJsonLoader;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtEncoder jwtEncoder;

    private Long userId;

    @BeforeEach
    void setUp() {
        userId = createTestUser();
    }

    private Long createTestUser() {

        User user = new User();
        user.setUsername("adminTestUser");
        user.setEmail("admintest@example.com");
        user.setPassword("encodedPassword");
        user.setRole(Role.USER);
        user.setEnabled(true);

        return userRepository.saveAndFlush(user).getId();
    }

    private String generateToken(Role role) {

        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("https://connectly.example")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(900))
                .subject("admin@example.com")
                .claim("role", role.name())
                .build();

        return jwtEncoder.encode(
                JwtEncoderParameters.from(
                        JwsHeader.with(MacAlgorithm.HS256).build(),
                        claims
                )
        ).getTokenValue();
    }

    private String adminToken() {
        return generateToken(Role.ADMIN);
    }

    private String userToken() {
        return generateToken(Role.USER);
    }

    @Test
    void getAllUsers_shouldReturnUsersForAdmin()
            throws Exception {

        mockMvc.perform(get("/api/admin/users")
                        .header(
                                "Authorization",
                                "Bearer " + adminToken()
                        ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getUserById_shouldReturnUserForAdmin()
            throws Exception {

        mockMvc.perform(get("/api/admin/users/" + userId)
                        .header(
                                "Authorization",
                                "Bearer " + adminToken()
                        ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.email")
                        .value("admintest@example.com"));
    }

    @Test
    void updateUserStatus_shouldDisableUserForAdmin()
            throws Exception {

        String request = TestJsonLoader.load(
                "testdata/admin/update-user-status.json"
        );

        mockMvc.perform(put(
                        "/api/admin/users/" + userId + "/status")
                        .header(
                                "Authorization",
                                "Bearer " + adminToken()
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isNoContent());

        User updatedUser = userRepository.findById(userId)
                .orElseThrow();

        org.junit.jupiter.api.Assertions.assertFalse(
                updatedUser.isEnabled()
        );
    }

    @Test
    void updateUserRole_shouldChangeRoleForAdmin()
            throws Exception {

        String request = TestJsonLoader.load(
                "testdata/admin/update-user-role.json"
        );

        mockMvc.perform(put(
                        "/api/admin/users/" + userId + "/role")
                        .header(
                                "Authorization",
                                "Bearer " + adminToken()
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isNoContent());

        User updatedUser = userRepository.findById(userId)
                .orElseThrow();

        org.junit.jupiter.api.Assertions.assertEquals(
                Role.ADMIN,
                updatedUser.getRole()
        );
    }

    @Test
    void deleteUser_shouldDeleteUserForAdmin()
            throws Exception {

        mockMvc.perform(delete(
                        "/api/admin/users/" + userId)
                        .header(
                                "Authorization",
                                "Bearer " + adminToken()
                        ))
                .andExpect(status().isNoContent());

        org.junit.jupiter.api.Assertions.assertFalse(
                userRepository.existsById(userId)
        );
    }

    @Test
    void adminEndpoints_shouldReturnForbiddenForRegularUser()
            throws Exception {

        mockMvc.perform(get("/api/admin/users")
                        .header(
                                "Authorization",
                                "Bearer " + userToken()
                        ))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminEndpoints_shouldReturnUnauthorizedWithoutToken()
            throws Exception {

        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getUserById_shouldReturnNotFoundForMissingUser()
            throws Exception {

        mockMvc.perform(get("/api/admin/users/999999")
                        .header(
                                "Authorization",
                                "Bearer " + adminToken()
                        ))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateUserStatus_shouldReturnBadRequestForInvalidRequest()
            throws Exception {

        String request = """
                {
                    "enabled": null
                }
                """;

        mockMvc.perform(put(
                        "/api/admin/users/" + userId + "/status")
                        .header(
                                "Authorization",
                                "Bearer " + adminToken()
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateUserRole_shouldReturnBadRequestForInvalidRequest()
            throws Exception {

        String request = """
                {
                    "role": null
                }
                """;

        mockMvc.perform(put(
                        "/api/admin/users/" + userId + "/role")
                        .header(
                                "Authorization",
                                "Bearer " + adminToken()
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
    }
}
