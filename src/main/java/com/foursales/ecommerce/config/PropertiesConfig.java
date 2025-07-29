package com.foursales.ecommerce.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Data
@Validated
@ConfigurationProperties(prefix = "app")
@Configuration
public class PropertiesConfig {

    @NotNull
    @NestedConfigurationProperty
    private Swagger swagger;

    @NotNull
    @NestedConfigurationProperty
    private Jwt jwt;

    @Data
    @Validated
    public static class Jwt {
        @NotBlank
        private String issuer;
        @NotNull
        private long expiresInSeconds;
        @NotNull
        private int refreshTokenExpiresInDays;
        @NotNull
        private RSAPrivateKey privateKey;
        @NotNull
        private RSAPublicKey publicKey;
    }

    @Data
    @Validated
    public static class Swagger {
        @NotBlank
        private String name;
        @NotBlank
        private String version;
        @NotBlank
        private String description;
    }
}
