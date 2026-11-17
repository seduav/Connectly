package com.connectly.config;

import com.connectly.entity.User;
import com.connectly.enums.Role;
import com.connectly.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(SecurityIntegrationTest.ProtectedEndpointTestConfiguration.class)
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Test
    void protectedEndpoint_shouldReturnUnauthorizedWithoutToken()
            throws Exception {

        mockMvc.perform(get("/api/test/protected"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedEndpoint_shouldReturnUnauthorizedWithInvalidToken()
            throws Exception {

        mockMvc.perform(get("/api/test/protected")
                        .header(
                                "Authorization",
                                "Bearer invalid.jwt.token"
                        ))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedEndpoint_shouldAllowAccessWithValidToken()
            throws Exception {

        User user = new User();
        user.setEmail("john@example.com");
        user.setRole(Role.USER);
        user.setEnabled(true);

        String token = jwtService.generateToken(user);

        mockMvc.perform(get("/api/test/protected")
                        .header(
                                "Authorization",
                                "Bearer " + token
                        )
                        .accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(content().string("JWT authentication successful"));
    }

    @TestConfiguration
    static class ProtectedEndpointTestConfiguration {

        @Bean
        ProtectedEndpointController protectedEndpointController() {
            return new ProtectedEndpointController();
        }
    }

    @RestController
    static class ProtectedEndpointController {

        @GetMapping("/api/test/protected")
        public String protectedEndpoint() {
            return "JWT authentication successful";
        }
    }
}
