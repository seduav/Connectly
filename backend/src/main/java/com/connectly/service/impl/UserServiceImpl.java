package com.connectly.service.impl;

import com.connectly.dto.request.ProfileUpdateRequest;
import com.connectly.dto.request.RegistrationRequest;
import com.connectly.dto.response.ProfileResponse;
import com.connectly.dto.response.RegistrationResponse;
import com.connectly.dto.response.UserResponse;
import com.connectly.entity.Profile;
import com.connectly.entity.User;
import com.connectly.enums.Role;
import com.connectly.exception.DuplicateEmailException;
import com.connectly.exception.DuplicateUsernameException;
import com.connectly.exception.UserNotFoundException;
import com.connectly.mapper.UserMapper;
import com.connectly.repository.UserRepository;
import com.connectly.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public RegistrationResponse registerUser(RegistrationRequest request) {

        String username = request.getUsername().trim();
        String email = request.getEmail()
                .trim()
                .toLowerCase(Locale.ROOT);

        if (userRepository.existsByEmail(email)) {
            throw new DuplicateEmailException(
                    "An account with this email already exists"
            );
        }

        if (userRepository.existsByUsername(username)) {
            throw new DuplicateUsernameException(
                    "An account with this username already exists"
            );
        }

        User user = new User();

        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );
        user.setRole(Role.USER);
        user.setEnabled(true);

        Profile profile = new Profile();
        profile.setFirstName(request.getFirstName().trim());
        profile.setLastName(request.getLastName().trim());

        user.setProfile(profile);

        User savedUser = userRepository.save(user);

        return userMapper.toRegistrationResponse(savedUser);
    }

    @Override
    public List<UserResponse> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(userMapper::toUserResponse)
                .toList();
    }

    @Override
    public UserResponse getUserById(Long userId) {

        User user = findUserById(userId);

        return userMapper.toUserResponse(user);
    }

    @Override
    public ProfileResponse getUserProfile(Long userId) {

        User user = findUserById(userId);

        return userMapper.toProfileResponse(user.getProfile());
    }

    @Override
    @Transactional
    public ProfileResponse updateUserProfile(
            Long userId,
            ProfileUpdateRequest request
    ) {

        User user = findUserById(userId);

        Profile profile = user.getProfile();

        if (profile == null) {
            profile = new Profile();
            user.setProfile(profile);
        }

        updateProfileFields(profile, request);

        return userMapper.toProfileResponse(profile);
    }

    @Override
    public ProfileResponse getMyProfile(String email) {

        User user = findUserByEmail(email);

        return userMapper.toProfileResponse(user.getProfile());
    }

    @Override
    @Transactional
    public ProfileResponse updateMyProfile(
            String email,
            ProfileUpdateRequest request
    ) {

        User user = findUserByEmail(email);

        Profile profile = user.getProfile();

        if (profile == null) {
            profile = new Profile();
            user.setProfile(profile);
        }

        updateProfileFields(profile, request);

        return userMapper.toProfileResponse(profile);
    }


    @Override
    @Transactional
    public void updateUserStatus(Long userId, boolean enabled) {

        User user = findUserById(userId);

        user.setEnabled(enabled);
    }

    @Override
    @Transactional
    public void updateUserRole(Long userId, Role role) {

        User user = findUserById(userId);

        user.setRole(role);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {

        User user = findUserById(userId);

        userRepository.delete(user);
    }

    private void updateProfileFields(
            Profile profile,
            ProfileUpdateRequest request
    ) {

        profile.setFirstName(request.getFirstName().trim());
        profile.setLastName(request.getLastName().trim());
        profile.setBio(request.getBio());
        profile.setProfilePictureUrl(request.getProfilePictureUrl());
        profile.setDateOfBirth(request.getDateOfBirth());
    }

    private User findUserById(Long userId) {

        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(
                        "User not found with ID: " + userId
                ));
    }

    private User findUserByEmail(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(
                        "User not found"
                ));
    }
}
