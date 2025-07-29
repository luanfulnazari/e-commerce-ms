package com.foursales.ecommerce.resource.request;


import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record OrderItemRequest(
        @NotNull
        UUID productId,
        @NotNull
        Integer quantity) {
}
