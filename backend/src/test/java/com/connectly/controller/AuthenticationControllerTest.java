package com.connectly.controller;

import com.connectly.entity.User;
import com.connectly.enums.Role;
import com.connectly.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private void createTestUser() {

        User user = new User();
        user.setUsername("loginuser");
        user.setEmail("login@example.com");
        user.setPassword(passwordEncoder.encode("Password123"));
        user.setRole(Role.USER);
        user.setEnabled(true);

        userRepository.save(user);
    }

    @Test
    void login_shouldReturnTokenWhenCredentialsAreValid()
            throws Exception {

        createTestUser();

        String request = """
                {
                    "email": "login@example.com",
                    "password": "Password123"
                }
                """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").value(900));
    }

    @Test
    void login_shouldReturnUnauthorizedWhenPasswordIsIncorrect()
            throws Exception {

        createTestUser();

        String request = """
                {
                    "email": "login@example.com",
                    "password": "WrongPassword"
                }
                """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message")
                        .value("Invalid email or password"));
    }

    @Test
    void login_shouldReturnUnauthorizedWhenEmailDoesNotExist()
            throws Exception {

        String request = """
                {
                    "email": "unknown@example.com",
                    "password": "Password123"
                }
                """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void login_shouldReturnBadRequestWhenRequestIsInvalid()
            throws Exception {

        String request = """
                {
                    "email": "invalid-email",
                    "password": ""
                }
                """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.validationErrors").exists());
    }


    @Test
    void login_shouldReturnUnauthorizedWhenAccountIsDisabled()
            throws Exception {

        User user = new User();
        user.setUsername("disableduser");
        user.setEmail("disabled@example.com");
        user.setPassword(
                passwordEncoder.encode("Password123")
        );
        user.setRole(Role.USER);
        user.setEnabled(false);

        userRepository.save(user);

        String request = """
            {
                "email": "disabled@example.com",
                "password": "Password123"
            }
            """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message")
                        .value("Invalid email or password"));
    }

}
