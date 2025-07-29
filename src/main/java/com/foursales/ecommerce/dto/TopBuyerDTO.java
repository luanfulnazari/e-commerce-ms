package com.foursales.ecommerce.dto;

import java.math.BigDecimal;
import java.util.UUID;

public interface TopBuyerDTO {
    UUID getUserId();

    String getEmail();

    BigDecimal getTotalSpent();
}
