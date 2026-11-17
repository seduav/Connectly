package com.connectly.controller;

import com.connectly.dto.request.UpdateUserRoleRequest;
import com.connectly.dto.request.UpdateUserStatusRequest;
import com.connectly.dto.response.UserResponse;
import com.connectly.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {

        List<UserResponse> users = userService.getAllUsers();

        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable Long userId
    ) {

        UserResponse user = userService.getUserById(userId);

        return ResponseEntity.ok(user);
    }

    @PutMapping("/users/{userId}/status")
    public ResponseEntity<Void> updateUserStatus(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserStatusRequest request
    ) {

        userService.updateUserStatus(
                userId,
                request.getEnabled()
        );

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/users/{userId}/role")
    public ResponseEntity<Void> updateUserRole(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserRoleRequest request
    ) {

        userService.updateUserRole(
                userId,
                request.getRole()
        );

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable Long userId
    ) {

        userService.deleteUser(userId);

        return ResponseEntity.noContent().build();
    }
}
