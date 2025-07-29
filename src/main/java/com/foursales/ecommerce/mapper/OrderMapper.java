package com.foursales.ecommerce.mapper;

import com.foursales.ecommerce.entity.Order;
import com.foursales.ecommerce.entity.User;
import com.foursales.ecommerce.resource.response.OrderResponse;

import java.math.BigDecimal;

public class OrderMapper {

    public static Order toEntity(User user, BigDecimal totalPrice) {
        return Order.builder()
                .user(user)
                .totalPrice(totalPrice)
                .build();
    }

    public static OrderResponse toResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .status(order.getStatus())
                .totalPrice(order.getTotalPrice())
                .items(OrderItemMapper.toListResponse(order.getItems()))
                .build();
    }
}
