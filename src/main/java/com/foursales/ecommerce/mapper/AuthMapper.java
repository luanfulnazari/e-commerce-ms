package com.foursales.ecommerce.mapper;


import com.foursales.ecommerce.resource.response.AuthResponse;

public class AuthMapper {

    private static final String BEARER_TOKEN_TYPE = "Bearer";

    public static AuthResponse toResponse(String newAccessToken, String newRefreshToken, long expiresInSeconds) {
        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType(BEARER_TOKEN_TYPE)
                .expiresIn(expiresInSeconds)
                .build();
    }
}
