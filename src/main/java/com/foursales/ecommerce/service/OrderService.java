package com.foursales.ecommerce.service;

import com.foursales.ecommerce.entity.Order;
import com.foursales.ecommerce.entity.OrderItem;
import com.foursales.ecommerce.entity.Product;
import com.foursales.ecommerce.entity.User;
import com.foursales.ecommerce.enums.OrderStatus;
import com.foursales.ecommerce.enums.ProductStatus;
import com.foursales.ecommerce.exceptions.BusinessException;
import com.foursales.ecommerce.mapper.OrderItemMapper;
import com.foursales.ecommerce.mapper.OrderMapper;
import com.foursales.ecommerce.repository.OrderRepository;
import com.foursales.ecommerce.repository.ProductRepository;
import com.foursales.ecommerce.repository.UserRepository;
import com.foursales.ecommerce.resource.request.CreateOrderRequest;
import com.foursales.ecommerce.resource.request.OrderItemRequest;
import com.foursales.ecommerce.resource.response.OrderResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        UUID userId = jwtService.getAuthenticatedUserId();

        User user = userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("User not found: " + userId));

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (OrderItemRequest itemRequest : request.items()) {
            UUID productId = itemRequest.productId();

            Product product = productRepository.findById(productId).orElseThrow(() ->
                    new EntityNotFoundException("Product not found: " + productId));

            if (!ProductStatus.ACTIVE.equals(product.getStatus())) {
                throw new BusinessException("Product is not active: " + productId);
            }

            BigDecimal itemTotalPrice = product.getPrice().multiply(BigDecimal.valueOf(itemRequest.quantity()));
            totalPrice = totalPrice.add(itemTotalPrice);

            OrderItem orderItem = OrderItemMapper.toEntity(product, itemRequest.quantity());
            orderItems.add(orderItem);
        }

        Order order = OrderMapper.toEntity(user, totalPrice);
        orderItems.forEach(item -> item.setOrder(order));
        order.setItems(orderItems);

        Order savedOrder = orderRepository.save(order);
        return OrderMapper.toResponse(savedOrder);
    }

    public OrderResponse payOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found: " + orderId));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BusinessException("Order already processed: " + orderId);
        }

        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            if (product.getStockQuantity() < item.getQuantity()) {
                order.setStatus(OrderStatus.CANCELED);
                orderRepository.save(order);
                throw new BusinessException(String.format(
                        "Insufficient stock for product '%s': available only %d",
                        product.getId(), product.getStockQuantity()
                ));
            }
        }

        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.setStockQuantity(product.getStockQuantity() - item.getQuantity());
            productRepository.save(product);
        }

        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);
        return OrderMapper.toResponse(order);
    }

    public List<OrderResponse> getUserOrders() {
        UUID userId = jwtService.getAuthenticatedUserId();

        User user = userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("User not found: " + userId));

        return orderRepository.findAllByUserId(user.getId()).stream()
                .map(OrderMapper::toResponse)
                .collect(Collectors.toList());
    }
}
