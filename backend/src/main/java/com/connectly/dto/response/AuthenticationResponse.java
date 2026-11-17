package com.connectly.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthenticationResponse {

    private String accessToken;
    private String tokenType;
    private long expiresIn;
}
