package com.foursales.ecommerce.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;

@Configuration
@RequiredArgsConstructor
public class JwtConfig {

    private final PropertiesConfig propertiesConfig;

    @Bean
    public JwtEncoder jwtEncoder() {
        String keyId = generateKeyId();
        RSAPublicKey publicKey = propertiesConfig.getJwt().getPublicKey();
        RSAPrivateKey privateKey = propertiesConfig.getJwt().getPrivateKey();

        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(keyId)
                .build();

        JWKSet jwkSet = new JWKSet(rsaKey);
        return new NimbusJwtEncoder(new ImmutableJWKSet<>(jwkSet));
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        RSAPublicKey publicKey = propertiesConfig.getJwt().getPublicKey();
        return NimbusJwtDecoder.withPublicKey(publicKey).build();
    }

    public String generateKeyId() {
        try {
            RSAPublicKey publicKey = propertiesConfig.getJwt().getPublicKey();
            String data = publicKey.getModulus().toString(16) + publicKey.getPublicExponent().toString(16);
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to generate key ID", e);
        }
    }
}