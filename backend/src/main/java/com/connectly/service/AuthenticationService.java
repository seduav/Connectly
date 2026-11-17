package com.connectly.service;

import com.connectly.dto.request.AuthenticationRequest;
import com.connectly.dto.response.AuthenticationResponse;

public interface AuthenticationService {

    AuthenticationResponse authenticate(AuthenticationRequest request);
}
