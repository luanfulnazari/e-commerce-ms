package com.foursales.ecommerce.dto;

import java.math.BigDecimal;
import java.util.UUID;

public interface UserAverageTicketDTO {
    UUID getUserId();

    String getEmail();

    BigDecimal getAverageTicket();
}
