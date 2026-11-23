package com.connectly.dto.response;

import com.connectly.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CurrentUserResponse {

    private Long id;

    private String username;

    private String email;

    private Role role;

    private String firstName;

    private String lastName;

    private String bio;

    private String profilePictureUrl;

    private LocalDate dateOfBirth;
}
