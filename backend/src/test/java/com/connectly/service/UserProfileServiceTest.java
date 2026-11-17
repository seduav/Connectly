package com.connectly.service.impl;

import com.connectly.dto.request.ProfileUpdateRequest;
import com.connectly.dto.response.ProfileResponse;
import com.connectly.entity.Profile;
import com.connectly.entity.User;
import com.connectly.exception.UserNotFoundException;
import com.connectly.mapper.UserMapper;
import com.connectly.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void getUserProfile_shouldReturnProfile() {

        User user = new User();

        Profile profile = new Profile();
        profile.setFirstName("John");
        profile.setLastName("Smith");

        user.setProfile(profile);

        ProfileResponse expectedResponse = new ProfileResponse(
                "John",
                "Smith",
                null,
                null,
                null
        );

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(userMapper.toProfileResponse(profile))
                .thenReturn(expectedResponse);

        ProfileResponse actualResponse =
                userService.getUserProfile(1L);

        assertNotNull(actualResponse);
        assertEquals("John", actualResponse.getFirstName());
        assertEquals("Smith", actualResponse.getLastName());

        verify(userRepository).findById(1L);
        verify(userMapper).toProfileResponse(profile);
    }

    @Test
    void updateUserProfile_shouldUpdateExistingProfile() {

        User user = new User();

        Profile profile = new Profile();
        user.setProfile(profile);

        ProfileUpdateRequest request = new ProfileUpdateRequest(
                "Jane",
                "Doe",
                "Hello Connectly!",
                "https://example.com/profile.jpg",
                LocalDate.of(2000, 5, 15)
        );

        ProfileResponse expectedResponse = new ProfileResponse(
                "Jane",
                "Doe",
                "Hello Connectly!",
                "https://example.com/profile.jpg",
                LocalDate.of(2000, 5, 15)
        );

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(userMapper.toProfileResponse(profile))
                .thenReturn(expectedResponse);

        ProfileResponse actualResponse =
                userService.updateUserProfile(1L, request);

        assertEquals("Jane", profile.getFirstName());
        assertEquals("Doe", profile.getLastName());
        assertEquals("Hello Connectly!", profile.getBio());
        assertEquals(
                "https://example.com/profile.jpg",
                profile.getProfilePictureUrl()
        );
        assertEquals(
                LocalDate.of(2000, 5, 15),
                profile.getDateOfBirth()
        );

        assertEquals(expectedResponse, actualResponse);

        verify(userRepository).findById(1L);
        verify(userMapper).toProfileResponse(profile);
    }

    @Test
    void getUserProfile_shouldThrowExceptionWhenUserDoesNotExist() {

        when(userRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> userService.getUserProfile(99L)
        );

        verify(userRepository).findById(99L);

        verifyNoInteractions(userMapper);
    }

    @Test
    void updateUserProfile_shouldCreateProfileWhenMissing() {

        User user = new User();

        ProfileUpdateRequest request = new ProfileUpdateRequest(
                "Alex",
                "Brown",
                "New profile",
                null,
                null
        );

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(userMapper.toProfileResponse(any(Profile.class)))
                .thenAnswer(invocation -> {
                    Profile updatedProfile = invocation.getArgument(0);

                    return new ProfileResponse(
                            updatedProfile.getFirstName(),
                            updatedProfile.getLastName(),
                            updatedProfile.getBio(),
                            updatedProfile.getProfilePictureUrl(),
                            updatedProfile.getDateOfBirth()
                    );
                });

        ProfileResponse response =
                userService.updateUserProfile(1L, request);

        assertNotNull(user.getProfile());
        assertSame(user, user.getProfile().getUser());

        assertEquals("Alex", response.getFirstName());
        assertEquals("Brown", response.getLastName());
        assertEquals("New profile", response.getBio());

        verify(userRepository).findById(1L);
        verify(userMapper).toProfileResponse(user.getProfile());
    }

    @Test
    void updateUserProfile_shouldThrowExceptionWhenUserDoesNotExist() {

        ProfileUpdateRequest request = new ProfileUpdateRequest(
                "John",
                "Smith",
                "Bio",
                null,
                null
        );

        when(userRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> userService.updateUserProfile(99L, request)
        );

        verify(userRepository).findById(99L);

        verifyNoInteractions(userMapper);

        verify(userRepository, never()).save(any(User.class));
    }
}
