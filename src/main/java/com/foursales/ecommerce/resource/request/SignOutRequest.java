package com.foursales.ecommerce.resource.request;

import jakarta.validation.constraints.NotBlank;

public record SignOutRequest(
        @NotBlank
        String refreshToken) {
}
