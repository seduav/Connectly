package com.connectly.dto.response;

import com.connectly.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationResponse {

    private Long id;

    private String username;

    private String email;

    private Role role;

    private String message;
}
