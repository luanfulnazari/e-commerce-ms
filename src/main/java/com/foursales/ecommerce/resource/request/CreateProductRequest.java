package com.foursales.ecommerce.resource.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateProductRequest(
        @NotBlank
        String name,
        @NotBlank
        String description,
        @NotNull
        BigDecimal price,
        @NotBlank
        String category,
        @NotNull
        Integer stockQuantity) {
}
