package com.connectly.service.impl;

import com.connectly.dto.request.RegistrationRequest;
import com.connectly.dto.response.RegistrationResponse;
import com.connectly.entity.User;
import com.connectly.enums.Role;
import com.connectly.exception.DuplicateEmailException;
import com.connectly.exception.DuplicateUsernameException;
import com.connectly.mapper.UserMapper;
import com.connectly.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void registerUser_shouldRegisterUserSuccessfully() {

        RegistrationRequest request = new RegistrationRequest(
                "john123",
                "JOHN@example.com",
                "Password123",
                "John",
                "Smith"
        );

        RegistrationResponse expectedResponse =
                new RegistrationResponse(
                        1L,
                        "john123",
                        "john@example.com",
                        Role.USER,
                        "Registration successful"
                );

        when(userRepository.existsByEmail("john@example.com"))
                .thenReturn(false);

        when(userRepository.existsByUsername("john123"))
                .thenReturn(false);

        when(passwordEncoder.encode("Password123"))
                .thenReturn("encodedPassword");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> {
                    User user = invocation.getArgument(0);
                    user.setId(1L);
                    return user;
                });

        when(userMapper.toRegistrationResponse(any(User.class)))
                .thenReturn(expectedResponse);

        RegistrationResponse actualResponse =
                userService.registerUser(request);

        assertNotNull(actualResponse);
        assertEquals(1L, actualResponse.getId());
        assertEquals("john123", actualResponse.getUsername());
        assertEquals("john@example.com", actualResponse.getEmail());
        assertEquals(Role.USER, actualResponse.getRole());

        ArgumentCaptor<User> userCaptor =
                ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();

        assertEquals("john123", savedUser.getUsername());
        assertEquals("john@example.com", savedUser.getEmail());
        assertEquals("encodedPassword", savedUser.getPassword());
        assertEquals(Role.USER, savedUser.getRole());
        assertTrue(savedUser.isEnabled());

        assertNotNull(savedUser.getProfile());
        assertEquals("John", savedUser.getProfile().getFirstName());
        assertEquals("Smith", savedUser.getProfile().getLastName());
        assertSame(savedUser, savedUser.getProfile().getUser());

        verify(passwordEncoder).encode("Password123");
        verify(userMapper).toRegistrationResponse(savedUser);
    }

    @Test
    void registerUser_shouldThrowExceptionWhenEmailExists() {

        RegistrationRequest request = new RegistrationRequest(
                "john123",
                "john@example.com",
                "Password123",
                "John",
                "Smith"
        );

        when(userRepository.existsByEmail("john@example.com"))
                .thenReturn(true);

        assertThrows(
                DuplicateEmailException.class,
                () -> userService.registerUser(request)
        );

        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
        verifyNoInteractions(userMapper);
    }

    @Test
    void registerUser_shouldThrowExceptionWhenUsernameExists() {

        RegistrationRequest request = new RegistrationRequest(
                "john123",
                "john@example.com",
                "Password123",
                "John",
                "Smith"
        );

        when(userRepository.existsByEmail("john@example.com"))
                .thenReturn(false);

        when(userRepository.existsByUsername("john123"))
                .thenReturn(true);

        assertThrows(
                DuplicateUsernameException.class,
                () -> userService.registerUser(request)
        );

        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
        verifyNoInteractions(userMapper);
    }
}
