package com.foursales.ecommerce.service;

import com.foursales.ecommerce.config.PropertiesConfig;
import com.foursales.ecommerce.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final PropertiesConfig propertiesConfig;

    private final Map<String, RefreshTokenData> refreshTokenCache = new ConcurrentHashMap<>();

    public String generateAndStore(User user) {
        String refreshToken = UUID.randomUUID().toString();
        int refreshTokenExpiresInDays = propertiesConfig.getJwt().getRefreshTokenExpiresInDays();
        Instant expiresAt = Instant.now().plus(Duration.ofDays(refreshTokenExpiresInDays));
        refreshTokenCache.put(refreshToken, new RefreshTokenData(user.getId(), expiresAt));
        return refreshToken;
    }

    public UUID validateAndGetUserId(String refreshToken) {
        RefreshTokenData data = refreshTokenCache.get(refreshToken);
        if (data == null || data.expiresAt().isBefore(Instant.now())) {
            refreshTokenCache.remove(refreshToken);
            throw new SecurityException("Invalid or expired refresh token");
        }
        return data.userId();
    }

    public String rotateRefreshToken(User user) {
        refreshTokenCache.entrySet().removeIf(entry ->
                Objects.equals(entry.getValue().userId(), user.getId()));
        return generateAndStore(user);
    }

    public void revoke(String refreshToken) {
        refreshTokenCache.remove(refreshToken);
    }

    public boolean isValidForUser(String refreshToken, UUID userId) {
        RefreshTokenData data = refreshTokenCache.get(refreshToken);
        return data != null && data.userId().equals(userId) && data.expiresAt().isAfter(Instant.now());
    }

    public record RefreshTokenData(
            UUID userId,
            Instant expiresAt) {
    }
}
