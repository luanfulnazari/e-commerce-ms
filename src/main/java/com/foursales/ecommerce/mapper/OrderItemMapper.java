package com.foursales.ecommerce.mapper;

import com.foursales.ecommerce.entity.OrderItem;
import com.foursales.ecommerce.entity.Product;
import com.foursales.ecommerce.resource.response.OrderItemResponse;

import java.util.List;
import java.util.stream.Collectors;

public class OrderItemMapper {

    public static OrderItem toEntity(Product product, Integer quantity) {
        return OrderItem.builder()
                .product(product)
                .quantity(quantity)
                .price(product.getPrice())
                .build();
    }

    public static List<OrderItemResponse> toListResponse(List<OrderItem> orderItems) {
        return orderItems.stream()
                .map(OrderItemMapper::toResponse)
                .collect(Collectors.toList());
    }

    public static OrderItemResponse toResponse(OrderItem orderItem) {
        return OrderItemResponse.builder()
                .productId(orderItem.getProduct().getId())
                .productName(orderItem.getProduct().getName())
                .quantity(orderItem.getQuantity())
                .price(orderItem.getPrice())
                .build();
    }
}
