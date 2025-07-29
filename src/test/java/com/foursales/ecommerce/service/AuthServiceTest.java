package com.foursales.ecommerce.service;

import com.foursales.ecommerce.config.PropertiesConfig;
import com.foursales.ecommerce.entity.User;
import com.foursales.ecommerce.enums.Role;
import com.foursales.ecommerce.repository.UserRepository;
import com.foursales.ecommerce.resource.request.RefreshTokenRequest;
import com.foursales.ecommerce.resource.request.SignInRequest;
import com.foursales.ecommerce.resource.request.SignOutRequest;
import com.foursales.ecommerce.resource.request.SignUpRequest;
import com.foursales.ecommerce.resource.response.AuthResponse;
import com.foursales.ecommerce.resource.response.UserResponse;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private PropertiesConfig propertiesConfig;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private final String refreshToken = "refresh-token";
    private final String accessToken = "access-token";
    private final long expiresIn = 300L;
    private final UUID userId = UUID.randomUUID();
    private final Role userRole = Role.USER;
    private final String email = "newuser@example.com";
    private final String password = "password123";
    private final String encryptedPassword = "$2a$10$encrypted";

    private SignUpRequest signUpRequest;
    private SignInRequest signInRequest;
    private RefreshTokenRequest refreshTokenRequest;
    private SignOutRequest signOutRequest;
    private User user;

    @BeforeEach
    void setUp() {
        signUpRequest = new SignUpRequest(email, password);
        signInRequest = new SignInRequest(email, password);
        refreshTokenRequest = new RefreshTokenRequest(refreshToken);
        signOutRequest = new SignOutRequest(refreshToken);
        user = User.builder().id(userId).email(email).password(encryptedPassword).role(userRole).build();
    }

    @Nested
    class SignUp {

        @Test
        @DisplayName("Should sign up user successfully when email is unique")
        void shouldSignUpSuccessfully() {
            when(userRepository.existsByEmail(email)).thenReturn(false);
            when(passwordEncoder.encode(password)).thenReturn(encryptedPassword);
            when(userRepository.save(any(User.class))).thenReturn(user);

            UserResponse response = authService.signUp(signUpRequest);

            assertEquals(email, response.getEmail());
            assertEquals(userRole, response.getRole());

            verify(userRepository).existsByEmail(email);
            verify(passwordEncoder).encode(password);
            verify(userRepository).save(any(User.class));
            verifyNoMoreInteractions(userRepository, passwordEncoder);
            verifyNoInteractions(jwtService, refreshTokenService, propertiesConfig);
        }

        @Test
        @DisplayName("Should throw IllegalStateException when email already exists")
        void shouldThrowWhenEmailAlreadyExists() {
            when(userRepository.existsByEmail(email)).thenReturn(true);

            assertThrows(IllegalStateException.class, () -> authService.signUp(signUpRequest));

            verify(userRepository).existsByEmail(email);
            verifyNoMoreInteractions(userRepository);
            verifyNoInteractions(passwordEncoder, jwtService, refreshTokenService, propertiesConfig);
        }
    }

    @Nested
    class SignIn {

        @Test
        @DisplayName("Should sign in successfully when credentials are valid")
        void shouldSignInSuccessfully() {
            mockJwtPropertiesExpiresIn();

            when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(password, user.getPassword())).thenReturn(true);
            when(jwtService.generateAccessToken(user)).thenReturn(accessToken);
            when(refreshTokenService.rotateRefreshToken(user)).thenReturn(refreshToken);

            AuthResponse response = authService.signIn(signInRequest);

            assertEquals(accessToken, response.getAccessToken());
            assertEquals(refreshToken, response.getRefreshToken());
            assertEquals(expiresIn, response.getExpiresIn());

            verify(userRepository).findByEmail(email);
            verify(passwordEncoder).matches(password, user.getPassword());
            verify(jwtService).generateAccessToken(user);
            verify(refreshTokenService).rotateRefreshToken(user);
            verify(propertiesConfig).getJwt();
            verify(propertiesConfig.getJwt()).getExpiresInSeconds();
            verifyNoMoreInteractions(userRepository, passwordEncoder, jwtService, refreshTokenService, propertiesConfig, propertiesConfig);
        }

        @Test
        @DisplayName("Should throw BadCredentialsException when user is not found")
        void shouldThrowWhenUserNotFound() {
            when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

            assertThrows(BadCredentialsException.class, () -> authService.signIn(signInRequest));

            verify(userRepository).findByEmail(email);
            verifyNoMoreInteractions(userRepository, passwordEncoder, jwtService, refreshTokenService, propertiesConfig, propertiesConfig);
        }

        @Test
        @DisplayName("Should throw BadCredentialsException when password is invalid")
        void shouldThrowWhenPasswordInvalid() {
            when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(password, user.getPassword())).thenReturn(false);

            assertThrows(BadCredentialsException.class, () -> authService.signIn(signInRequest));

            verify(userRepository).findByEmail(email);
            verify(passwordEncoder).matches(password, user.getPassword());
            verifyNoMoreInteractions(userRepository, passwordEncoder, jwtService, refreshTokenService, propertiesConfig, propertiesConfig);
        }
    }

    @Nested
    class RefreshToken {

        @Test
        @DisplayName("Should refresh token successfully when user is found")
        void shouldRefreshTokenSuccessfully() {
            mockJwtPropertiesExpiresIn();

            when(refreshTokenService.validateAndGetUserId(refreshToken)).thenReturn(userId);
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(jwtService.generateAccessToken(user)).thenReturn(accessToken);
            String newRefreshToken = "new-refresh-token";
            when(refreshTokenService.rotateRefreshToken(user)).thenReturn(newRefreshToken);

            AuthResponse response = authService.refreshToken(refreshTokenRequest);

            assertEquals(accessToken, response.getAccessToken());
            assertEquals(newRefreshToken, response.getRefreshToken());
            assertEquals(300L, response.getExpiresIn());

            verify(refreshTokenService).validateAndGetUserId(refreshToken);
            verify(userRepository).findById(userId);
            verify(jwtService).generateAccessToken(user);
            verify(refreshTokenService).rotateRefreshToken(user);
            verify(propertiesConfig.getJwt()).getExpiresInSeconds();
            verifyNoMoreInteractions(refreshTokenService, userRepository, jwtService, propertiesConfig);
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when user is not found")
        void shouldThrowWhenUserNotFound() {
            when(refreshTokenService.validateAndGetUserId(refreshToken)).thenReturn(userId);
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class,
                    () -> authService.refreshToken(refreshTokenRequest));

            verify(refreshTokenService).validateAndGetUserId(refreshToken);
            verify(userRepository).findById(userId);
            verifyNoMoreInteractions(refreshTokenService, userRepository, jwtService, propertiesConfig);
        }
    }

    @Nested
    class SignOut {

        @Test
        @DisplayName("Should sign out successfully when refresh token is valid for user")
        void shouldSignOutSuccessfully() {
            when(jwtService.getAuthenticatedUserId()).thenReturn(userId);
            when(refreshTokenService.isValidForUser(refreshToken, userId)).thenReturn(true);

            authService.signOut(signOutRequest);

            verify(jwtService).getAuthenticatedUserId();
            verify(refreshTokenService).isValidForUser(refreshToken, userId);
            verify(refreshTokenService).revoke(refreshToken);
            verifyNoMoreInteractions(jwtService, refreshTokenService);
        }

        @Test
        @DisplayName("Should throw SecurityException when refresh token is invalid for user")
        void shouldThrowWhenRefreshTokenIsInvalid() {
            when(jwtService.getAuthenticatedUserId()).thenReturn(userId);
            when(refreshTokenService.isValidForUser(refreshToken, userId)).thenReturn(false);

            assertThrows(SecurityException.class, () -> authService.signOut(signOutRequest));

            verify(jwtService).getAuthenticatedUserId();
            verify(refreshTokenService).isValidForUser(refreshToken, userId);
            verifyNoMoreInteractions(jwtService, refreshTokenService);
        }
    }

    private void mockJwtPropertiesExpiresIn() {
        PropertiesConfig.Jwt jwtProps = mock(PropertiesConfig.Jwt.class);
        when(jwtProps.getExpiresInSeconds()).thenReturn(expiresIn);
        when(propertiesConfig.getJwt()).thenReturn(jwtProps);
    }
}
