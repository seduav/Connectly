package com.connectly.controller;

import com.connectly.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    private String readJson(String filename) throws Exception {
        ClassPathResource resource = new ClassPathResource(
                "testdata/users/" + filename
        );

        return resource.getContentAsString(StandardCharsets.UTF_8);
    }

    @Test
    void registerUser_shouldReturnCreated() throws Exception {

        String request = readJson("register-user.json");

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.username")
                        .value("controlleruser"))
                .andExpect(jsonPath("$.email")
                        .value("controller@example.com"))
                .andExpect(jsonPath("$.role")
                        .value("USER"))
                .andExpect(jsonPath("$.message")
                        .value("Registration successful"));
    }

    @Test
    void registerUser_shouldReturnBadRequestWhenInputIsInvalid()
            throws Exception {

        String request = readJson("register-user-invalid.json");

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.validationErrors").exists());
    }

    @Test
    void registerUser_shouldReturnConflictWhenEmailExists()
            throws Exception {

        String request = readJson("register-user.json");

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated());

        String secondRequest = request.replace(
                "controlleruser",
                "anotheruser"
        ).replace(
                "controller@example.com",
                "controller@example.com"
        ).replace(
                "John",
                "Jane"
        );

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(secondRequest))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }
}
