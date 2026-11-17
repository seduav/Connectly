package com.connectly.security;

import com.connectly.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtEncoder jwtEncoder;

    @Value("${app.jwt.expiration}")
    private long jwtExpiration;

    public String generateToken(User user) {

        if (jwtExpiration <= 0) {
            throw new IllegalStateException(
                    "JWT expiration must be greater than zero"
            );
        }

        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plusSeconds(jwtExpiration);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("https://connectly.example")
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .subject(user.getEmail())
                .claim("role", user.getRole().name())
                .build();

        return jwtEncoder.encode(
                JwtEncoderParameters.from(
                        org.springframework.security.oauth2.jwt
                                .JwsHeader.with(MacAlgorithm.HS256)
                                .build(),
                        claims
                )
        ).getTokenValue();
    }

    public long getExpirationTime() {
        return jwtExpiration;
    }
}
