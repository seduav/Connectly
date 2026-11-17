package com.connectly.service.impl;

import com.connectly.dto.request.AuthenticationRequest;
import com.connectly.dto.response.AuthenticationResponse;
import com.connectly.entity.User;
import com.connectly.repository.UserRepository;
import com.connectly.security.JwtService;
import com.connectly.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public AuthenticationResponse authenticate(
            AuthenticationRequest request
    ) {

        String email = request.getEmail().trim().toLowerCase();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new BadCredentialsException(
                                "Invalid email or password"
                        )
                );

        if (!user.isEnabled()
                || !passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        )) {

            throw new BadCredentialsException(
                    "Invalid email or password"
            );
        }

        String token = jwtService.generateToken(user);

        return new AuthenticationResponse(
                token,
                "Bearer",
                jwtService.getExpirationTime()
        );
    }
}
