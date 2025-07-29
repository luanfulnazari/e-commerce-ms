package com.foursales.ecommerce.resource.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SignUpRequest(
        @Email
        String email,
        @NotBlank
        String password) {
}
