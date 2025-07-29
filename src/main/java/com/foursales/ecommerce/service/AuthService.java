package com.foursales.ecommerce.service;

import com.foursales.ecommerce.config.PropertiesConfig;
import com.foursales.ecommerce.entity.User;
import com.foursales.ecommerce.enums.Role;
import com.foursales.ecommerce.mapper.AuthMapper;
import com.foursales.ecommerce.mapper.UserMapper;
import com.foursales.ecommerce.repository.UserRepository;
import com.foursales.ecommerce.resource.request.RefreshTokenRequest;
import com.foursales.ecommerce.resource.request.SignInRequest;
import com.foursales.ecommerce.resource.request.SignOutRequest;
import com.foursales.ecommerce.resource.request.SignUpRequest;
import com.foursales.ecommerce.resource.response.AuthResponse;
import com.foursales.ecommerce.resource.response.UserResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final PropertiesConfig propertiesConfig;

    public UserResponse signUp(SignUpRequest request) {

        if (userRepository.existsByEmail(request.email()))
            throw new IllegalStateException("Email already registered");

        String encryptedPassword = passwordEncoder.encode(request.password());
        User user = UserMapper.toEntity(request.email(), encryptedPassword, Role.USER);

        userRepository.save(user);

        return UserMapper.toResponse(user);
    }

    public AuthResponse signIn(SignInRequest request) {

        return userRepository.findByEmail(request.email()).map(user -> {

            if (!passwordEncoder.matches(request.password(), user.getPassword())) {
                throw new BadCredentialsException("Invalid credentials");
            }

            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = refreshTokenService.rotateRefreshToken(user);
            long expiresIn = propertiesConfig.getJwt().getExpiresInSeconds();

            return AuthMapper.toResponse(accessToken, refreshToken, expiresIn);

        }).orElseThrow(() -> new BadCredentialsException("Invalid credentials"));
    }

    public AuthResponse refreshToken(RefreshTokenRequest request) {

        UUID userId = refreshTokenService.validateAndGetUserId(request.refreshToken());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));

        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = refreshTokenService.rotateRefreshToken(user);
        long expiresInSeconds = propertiesConfig.getJwt().getExpiresInSeconds();

        return AuthMapper.toResponse(newAccessToken, newRefreshToken, expiresInSeconds);
    }

    public void signOut(SignOutRequest request) {

        UUID userId = jwtService.getAuthenticatedUserId();

        if (!refreshTokenService.isValidForUser(request.refreshToken(), userId)) {
            throw new SecurityException("Invalid refresh token for this user");
        }

        refreshTokenService.revoke(request.refreshToken());
    }
}
