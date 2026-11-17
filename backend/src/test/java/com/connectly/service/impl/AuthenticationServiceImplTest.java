package com.connectly.service.impl;

import com.connectly.dto.request.AuthenticationRequest;
import com.connectly.dto.response.AuthenticationResponse;
import com.connectly.entity.User;
import com.connectly.enums.Role;
import com.connectly.repository.UserRepository;
import com.connectly.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @Test
    void authenticate_shouldReturnTokenWhenCredentialsAreValid() {

        AuthenticationRequest request =
                new AuthenticationRequest(
                        "JOHN@example.com",
                        "Password123"
                );

        User user = new User();
        user.setId(1L);
        user.setEmail("john@example.com");
        user.setPassword("encodedPassword");
        user.setRole(Role.USER);
        user.setEnabled(true);

        when(userRepository.findByEmail("john@example.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "Password123",
                "encodedPassword"
        )).thenReturn(true);

        when(jwtService.generateToken(user))
                .thenReturn("test.jwt.token");

        when(jwtService.getExpirationTime())
                .thenReturn(900L);

        AuthenticationResponse response =
                authenticationService.authenticate(request);

        assertNotNull(response);
        assertEquals("test.jwt.token", response.getAccessToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals(900L, response.getExpiresIn());

        verify(userRepository).findByEmail("john@example.com");
        verify(passwordEncoder).matches(
                "Password123",
                "encodedPassword"
        );
        verify(jwtService).generateToken(user);
    }

    @Test
    void authenticate_shouldThrowExceptionWhenEmailDoesNotExist() {

        AuthenticationRequest request =
                new AuthenticationRequest(
                        "unknown@example.com",
                        "Password123"
                );

        when(userRepository.findByEmail("unknown@example.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                BadCredentialsException.class,
                () -> authenticationService.authenticate(request)
        );

        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(jwtService);
    }

    @Test
    void authenticate_shouldThrowExceptionWhenPasswordIsIncorrect() {

        AuthenticationRequest request =
                new AuthenticationRequest(
                        "john@example.com",
                        "WrongPassword"
                );

        User user = new User();
        user.setEmail("john@example.com");
        user.setPassword("encodedPassword");
        user.setEnabled(true);

        when(userRepository.findByEmail("john@example.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "WrongPassword",
                "encodedPassword"
        )).thenReturn(false);

        assertThrows(
                BadCredentialsException.class,
                () -> authenticationService.authenticate(request)
        );

        verifyNoInteractions(jwtService);
    }

    @Test
    void authenticate_shouldRejectDisabledAccount() {

        AuthenticationRequest request =
                new AuthenticationRequest(
                        "john@example.com",
                        "Password123"
                );

        User user = new User();
        user.setEmail("john@example.com");
        user.setPassword("encodedPassword");
        user.setEnabled(false);

        when(userRepository.findByEmail("john@example.com"))
                .thenReturn(Optional.of(user));

        assertThrows(
                BadCredentialsException.class,
                () -> authenticationService.authenticate(request)
        );

        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(jwtService);
    }
}
