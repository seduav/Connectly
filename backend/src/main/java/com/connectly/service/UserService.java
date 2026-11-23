package com.connectly.service;

import com.connectly.dto.request.ProfileUpdateRequest;
import com.connectly.dto.request.RegistrationRequest;
import com.connectly.dto.response.ProfileResponse;
import com.connectly.dto.response.RegistrationResponse;
import com.connectly.dto.response.UserResponse;
import com.connectly.dto.response.CurrentUserResponse;
import com.connectly.dto.response.UserSuggestionResponse;
import com.connectly.enums.Role;

import java.util.List;

public interface UserService {

    RegistrationResponse registerUser(RegistrationRequest request);

    List<UserResponse> getAllUsers();

    UserResponse getUserById(Long userId);

    ProfileResponse getUserProfile(Long userId);

    ProfileResponse updateUserProfile(
            Long userId,
            ProfileUpdateRequest request
    );

    ProfileResponse getMyProfile(String email);

    ProfileResponse updateMyProfile(
            String email,
            ProfileUpdateRequest request
    );

    void updateUserStatus(Long userId, boolean enabled);

    void updateUserRole(Long userId, Role role);

    void deleteUser(Long userId);

    CurrentUserResponse getCurrentUser(String email);

    List<UserSuggestionResponse> getRandomUsers(
            String currentUserEmail,
            int limit
    );
}

