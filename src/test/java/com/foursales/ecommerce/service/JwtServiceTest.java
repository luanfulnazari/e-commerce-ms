package com.foursales.ecommerce.service;

import com.foursales.ecommerce.config.PropertiesConfig;
import com.foursales.ecommerce.entity.User;
import com.foursales.ecommerce.enums.Role;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {

    @Mock
    private PropertiesConfig propertiesConfig;

    @Mock
    private JwtEncoder jwtEncoder;

    @InjectMocks
    private JwtService jwtService;

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Nested
    class GetAuthenticationUserId {

        @Test
        @DisplayName("Should return authenticated user ID")
        void shouldReturnAuthenticatedUserId() {
            UUID expectedUserId = UUID.randomUUID();

            Authentication auth = mock(Authentication.class);
            when(auth.isAuthenticated()).thenReturn(true);
            when(auth.getName()).thenReturn(expectedUserId.toString());

            SecurityContext context = mock(SecurityContext.class);
            when(context.getAuthentication()).thenReturn(auth);
            SecurityContextHolder.setContext(context);

            UUID userId = jwtService.getAuthenticatedUserId();

            assertEquals(expectedUserId, userId);
        }

        @Test
        @DisplayName("Should return null when user is anonymous")
        void shouldReturnNullWhenAnonymousUser() {
            Authentication auth = mock(Authentication.class);
            when(auth.isAuthenticated()).thenReturn(true);
            when(auth.getName()).thenReturn("anonymousUser");

            SecurityContext context = mock(SecurityContext.class);
            when(context.getAuthentication()).thenReturn(auth);
            SecurityContextHolder.setContext(context);

            UUID userId = jwtService.getAuthenticatedUserId();

            assertNull(userId);
        }

        @Test
        @DisplayName("Should throw exception when not authenticated")
        void shouldThrowExceptionWhenNotAuthenticated() {
            Authentication auth = mock(Authentication.class);
            when(auth.isAuthenticated()).thenReturn(false);

            SecurityContext context = mock(SecurityContext.class);
            when(context.getAuthentication()).thenReturn(auth);
            SecurityContextHolder.setContext(context);

            assertThrows(BadCredentialsException.class, jwtService::getAuthenticatedUserId);
        }
    }

    @Nested
    class GenerateAccessToken {

        @Test
        @DisplayName("Should generate access token based on user data")
        void shouldGenerateAccessToken() {
            User user = mock(User.class);
            when(user.getId()).thenReturn(UUID.randomUUID());
            when(user.getEmail()).thenReturn("user@email.com");
            when(user.getRole()).thenReturn(Role.ADMIN);

            PropertiesConfig.Jwt jwtProps = mock(PropertiesConfig.Jwt.class);
            when(jwtProps.getIssuer()).thenReturn("auth-management");
            when(jwtProps.getExpiresInSeconds()).thenReturn(3600L);
            when(propertiesConfig.getJwt()).thenReturn(jwtProps);

            Jwt jwt = mock(Jwt.class);
            when(jwt.getTokenValue()).thenReturn("token-123");
            when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(jwt);

            String token = jwtService.generateAccessToken(user);

            assertEquals("token-123", token);
        }
    }
}
