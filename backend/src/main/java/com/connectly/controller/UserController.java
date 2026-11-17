package com.connectly.controller;

import com.connectly.dto.request.ProfileUpdateRequest;
import com.connectly.dto.request.RegistrationRequest;
import com.connectly.dto.response.ProfileResponse;
import com.connectly.dto.response.RegistrationResponse;
import com.connectly.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<RegistrationResponse> registerUser(
            @Valid @RequestBody RegistrationRequest request
    ) {

        RegistrationResponse response =
                userService.registerUser(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/me/profile")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ProfileResponse> getMyProfile(
            @AuthenticationPrincipal Jwt jwt
    ) {

        ProfileResponse response =
                userService.getMyProfile(jwt.getSubject());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/me/profile")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ProfileResponse> updateMyProfile(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody ProfileUpdateRequest request
    ) {

        ProfileResponse response =
                userService.updateMyProfile(
                        jwt.getSubject(),
                        request
                );

        return ResponseEntity.ok(response);
    }
}
