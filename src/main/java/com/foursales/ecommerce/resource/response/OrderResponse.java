package com.foursales.ecommerce.resource.response;

import com.foursales.ecommerce.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {

    private UUID id;
    private OrderStatus status;
    private BigDecimal totalPrice;
    private List<OrderItemResponse> items;
}
