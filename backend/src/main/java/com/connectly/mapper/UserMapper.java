package com.connectly.mapper;

import com.connectly.dto.response.ProfileResponse;
import com.connectly.dto.response.RegistrationResponse;
import com.connectly.dto.response.UserResponse;
import com.connectly.entity.Profile;
import com.connectly.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public ProfileResponse toProfileResponse(Profile profile) {

        if (profile == null) {
            return null;
        }

        return new ProfileResponse(
                profile.getFirstName(),
                profile.getLastName(),
                profile.getBio(),
                profile.getProfilePictureUrl(),
                profile.getDateOfBirth()
        );
    }

    public UserResponse toUserResponse(User user) {

        if (user == null) {
            return null;
        }

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.isEnabled(),
                user.getCreatedAt(),
                toProfileResponse(user.getProfile())
        );
    }

    public RegistrationResponse toRegistrationResponse(User user) {

        if (user == null) {
            return null;
        }

        return new RegistrationResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                "Registration successful"
        );
    }
}
