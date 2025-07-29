package com.foursales.ecommerce.dto;

import java.math.BigDecimal;

public interface MonthlyRevenueDTO {
    String getRevenueMonth();

    BigDecimal getTotalRevenue();
}
