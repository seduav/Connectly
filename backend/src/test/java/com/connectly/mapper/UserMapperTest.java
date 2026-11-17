package com.connectly.mapper;

import com.connectly.dto.response.ProfileResponse;
import com.connectly.dto.response.RegistrationResponse;
import com.connectly.dto.response.UserResponse;
import com.connectly.entity.Profile;
import com.connectly.entity.User;
import com.connectly.enums.Role;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private final UserMapper userMapper = new UserMapper();

    @Test
    void toProfileResponse_shouldMapProfileSuccessfully() {

        Profile profile = new Profile();
        profile.setFirstName("John");
        profile.setLastName("Smith");
        profile.setBio("Hello Connectly!");
        profile.setProfilePictureUrl(
                "https://example.com/profile.jpg"
        );
        profile.setDateOfBirth(LocalDate.of(2000, 5, 15));

        ProfileResponse response =
                userMapper.toProfileResponse(profile);

        assertNotNull(response);
        assertEquals("John", response.getFirstName());
        assertEquals("Smith", response.getLastName());
        assertEquals("Hello Connectly!", response.getBio());
        assertEquals(
                "https://example.com/profile.jpg",
                response.getProfilePictureUrl()
        );
        assertEquals(
                LocalDate.of(2000, 5, 15),
                response.getDateOfBirth()
        );
    }

    @Test
    void toProfileResponse_shouldReturnNullWhenProfileIsNull() {

        ProfileResponse response =
                userMapper.toProfileResponse(null);

        assertNull(response);
    }

    @Test
    void toUserResponse_shouldMapUserSuccessfully() {

        User user = new User();
        user.setId(1L);
        user.setUsername("john123");
        user.setEmail("john@example.com");
        user.setRole(Role.USER);
        user.setEnabled(true);

        Profile profile = new Profile();
        profile.setFirstName("John");
        profile.setLastName("Smith");
        profile.setBio("Hello Connectly!");

        user.setProfile(profile);

        UserResponse response =
                userMapper.toUserResponse(user);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("john123", response.getUsername());
        assertEquals("john@example.com", response.getEmail());
        assertEquals(Role.USER, response.getRole());
        assertTrue(response.isEnabled());

        assertNotNull(response.getProfile());
        assertEquals("John", response.getProfile().getFirstName());
        assertEquals("Smith", response.getProfile().getLastName());
        assertEquals(
                "Hello Connectly!",
                response.getProfile().getBio()
        );
    }

    @Test
    void toUserResponse_shouldReturnNullWhenUserIsNull() {

        UserResponse response =
                userMapper.toUserResponse(null);

        assertNull(response);
    }

    @Test
    void toRegistrationResponse_shouldMapUserSuccessfully() {

        User user = new User();
        user.setId(1L);
        user.setUsername("john123");
        user.setEmail("john@example.com");
        user.setRole(Role.USER);
        user.setPassword("encodedPassword");

        RegistrationResponse response =
                userMapper.toRegistrationResponse(user);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("john123", response.getUsername());
        assertEquals("john@example.com", response.getEmail());
        assertEquals(Role.USER, response.getRole());
        assertEquals(
                "Registration successful",
                response.getMessage()
        );
    }

    @Test
    void toRegistrationResponse_shouldReturnNullWhenUserIsNull() {

        RegistrationResponse response =
                userMapper.toRegistrationResponse(null);

        assertNull(response);
    }
}
