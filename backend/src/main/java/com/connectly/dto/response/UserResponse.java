package com.connectly.dto.response;

import com.connectly.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long id;

    private String username;

    private String email;

    private Role role;

    private boolean enabled;

    private LocalDateTime createdAt;

    private ProfileResponse profile;
}
