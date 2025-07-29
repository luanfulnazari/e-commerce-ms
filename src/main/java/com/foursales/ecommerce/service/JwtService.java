package com.foursales.ecommerce.service;

import com.foursales.ecommerce.config.PropertiesConfig;
import com.foursales.ecommerce.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final PropertiesConfig propertiesConfig;
    private final JwtEncoder jwtEncoder;

    public UUID getAuthenticatedUserId() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BadCredentialsException("User is not authenticated");
        }

        String username = authentication.getName();
        if ("anonymousUser".equals(username)) {
            return null;
        }

        return UUID.fromString(username);
    }

    public String generateAccessToken(User user) {

        Instant now = Instant.now();
        String issuer = propertiesConfig.getJwt().getIssuer();
        long expiresInSeconds = propertiesConfig.getJwt().getExpiresInSeconds();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiresInSeconds))
                .subject(String.valueOf(user.getId()))
                .claim("email", user.getEmail())
                .claim("scopes", user.getRole())
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}



