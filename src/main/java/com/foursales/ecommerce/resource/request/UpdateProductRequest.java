package com.foursales.ecommerce.resource.request;

import java.math.BigDecimal;

public record UpdateProductRequest(
        String name,
        String description,
        BigDecimal price,
        String category,
        Integer stockQuantity) {
}
