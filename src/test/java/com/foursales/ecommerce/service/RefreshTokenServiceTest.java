package com.foursales.ecommerce.service;

import com.foursales.ecommerce.config.PropertiesConfig;
import com.foursales.ecommerce.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private PropertiesConfig propertiesConfig;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder().id(UUID.randomUUID()).build();
    }

    @Test
    @DisplayName("Should generate and store a refresh token")
    void shouldGenerateAndStoreRefreshToken() {
        whenPropertiesConfigGetJwtThenReturnJwtProperties();

        String token = refreshTokenService.generateAndStore(user);

        assertNotNull(token);
        UUID userId = refreshTokenService.validateAndGetUserId(token);
        assertEquals(user.getId(), userId);

        verify(propertiesConfig.getJwt()).getRefreshTokenExpiresInDays();
        verifyNoMoreInteractions(propertiesConfig);
    }

    @Test
    @DisplayName("Should rotate and return a new refresh token, removing old tokens for the user")
    void shouldRotateRefreshToken() {
        whenPropertiesConfigGetJwtThenReturnJwtProperties();

        String firstToken = refreshTokenService.generateAndStore(user);
        assertNotNull(firstToken);

        String newToken = refreshTokenService.rotateRefreshToken(user);
        assertNotNull(newToken);
        assertNotEquals(firstToken, newToken);

        UUID userId = refreshTokenService.validateAndGetUserId(newToken);
        assertEquals(user.getId(), userId);

        assertThrows(SecurityException.class, () -> refreshTokenService.validateAndGetUserId(firstToken));

        verify(propertiesConfig.getJwt(), times(2)).getRefreshTokenExpiresInDays();
        verifyNoMoreInteractions(propertiesConfig);
    }

    @Test
    @DisplayName("Should revoke the refresh token")
    void shouldRevokeRefreshToken() {
        whenPropertiesConfigGetJwtThenReturnJwtProperties();

        String token = refreshTokenService.generateAndStore(user);
        refreshTokenService.revoke(token);

        assertThrows(SecurityException.class, () -> refreshTokenService.validateAndGetUserId(token));

        verify(propertiesConfig.getJwt()).getRefreshTokenExpiresInDays();
        verifyNoMoreInteractions(propertiesConfig);
    }

    @Test
    @DisplayName("Should throw SecurityException when token is not found")
    void shouldThrowWhenTokenNotFound() {
        String invalidToken = "non-existent";

        assertThrows(SecurityException.class, () -> refreshTokenService.validateAndGetUserId(invalidToken));
        verifyNoInteractions(propertiesConfig);
    }

    @Test
    @DisplayName("Should throw SecurityException when token is expired")
    void shouldThrowWhenTokenIsExpired() {
        String token = UUID.randomUUID().toString();
        Instant expiredAt = Instant.now().minus(Duration.ofHours(1));
        RefreshTokenService.RefreshTokenData expiredData = new RefreshTokenService.RefreshTokenData(user.getId(), expiredAt);

        @SuppressWarnings("unchecked")
        Map<String, RefreshTokenService.RefreshTokenData> cache =
                (Map<String, RefreshTokenService.RefreshTokenData>)
                        ReflectionTestUtils.getField(refreshTokenService, "refreshTokenCache");

        cache.put(token, expiredData);

        assertThrows(SecurityException.class, () -> refreshTokenService.validateAndGetUserId(token));
        verifyNoInteractions(propertiesConfig);
    }

    @Test
    @DisplayName("Should validate token correctly for the given user")
    void shouldValidateTokenForUser() {
        whenPropertiesConfigGetJwtThenReturnJwtProperties();

        String token = refreshTokenService.generateAndStore(user);

        boolean valid = refreshTokenService.isValidForUser(token, user.getId());

        assertTrue(valid);

        verify(propertiesConfig.getJwt()).getRefreshTokenExpiresInDays();
        verifyNoMoreInteractions(propertiesConfig);
    }

    @Test
    @DisplayName("Should return false for invalid token-user match")
    void shouldReturnFalseForInvalidTokenUserMatch() {
        whenPropertiesConfigGetJwtThenReturnJwtProperties();

        String token = refreshTokenService.generateAndStore(user);

        boolean valid = refreshTokenService.isValidForUser(token, UUID.randomUUID());
        assertFalse(valid);

        verify(propertiesConfig.getJwt()).getRefreshTokenExpiresInDays();
        verifyNoMoreInteractions(propertiesConfig);
    }

    private void whenPropertiesConfigGetJwtThenReturnJwtProperties() {
        PropertiesConfig.Jwt jwtProps = mock(PropertiesConfig.Jwt.class);
        when(jwtProps.getRefreshTokenExpiresInDays()).thenReturn(1);
        when(propertiesConfig.getJwt()).thenReturn(jwtProps);
    }
}
