package com.foursales.ecommerce.service;

import com.foursales.ecommerce.entity.Order;
import com.foursales.ecommerce.entity.OrderItem;
import com.foursales.ecommerce.entity.Product;
import com.foursales.ecommerce.entity.User;
import com.foursales.ecommerce.enums.OrderStatus;
import com.foursales.ecommerce.enums.ProductStatus;
import com.foursales.ecommerce.exceptions.BusinessException;
import com.foursales.ecommerce.repository.OrderRepository;
import com.foursales.ecommerce.repository.ProductRepository;
import com.foursales.ecommerce.repository.UserRepository;
import com.foursales.ecommerce.resource.request.CreateOrderRequest;
import com.foursales.ecommerce.resource.request.OrderItemRequest;
import com.foursales.ecommerce.resource.response.OrderResponse;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private OrderService orderService;

    private final UUID userId = UUID.randomUUID();
    private final UUID productId = UUID.randomUUID();
    private final UUID orderId = UUID.randomUUID();
    private final UUID orderItemId = UUID.randomUUID();

    private CreateOrderRequest createOrderRequest;
    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder().id(userId).build();
    }

    @Nested
    class CreateOrder {

        @Test
        @DisplayName("Should create an order successfully")
        void shouldCreateOrderSuccessfully() {
            buildCreateOrderRequest();

            Product product = buildProduct();
            OrderItem orderItem = buildOrderItem(product, createOrderRequest.items().get(0).quantity());
            Order order = buildOrder(orderItem, product);

            when(jwtService.getAuthenticatedUserId()).thenReturn(userId);
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(productRepository.findById(productId)).thenReturn(Optional.of(product));
            when(orderRepository.save(any(Order.class))).thenReturn(order);

            OrderResponse response = orderService.createOrder(createOrderRequest);

            assertEquals(orderId, response.getId());
            assertEquals(OrderStatus.PENDING, response.getStatus());
            assertEquals(new BigDecimal("100.00"), response.getTotalPrice());
            assertEquals(1, response.getItems().size());

            verify(jwtService).getAuthenticatedUserId();
            verify(userRepository).findById(userId);
            verify(productRepository).findById(productId);
            verify(orderRepository).save(any(Order.class));
            verifyNoMoreInteractions(jwtService, userRepository, productRepository, orderRepository);
        }

        @Test
        @DisplayName("Should throw BusinessException when product is not active")
        void shouldThrowWhenProductIsNotActive() {
            Product product = buildProduct();
            product.setStatus(ProductStatus.INACTIVE);

            buildCreateOrderRequest();

            when(jwtService.getAuthenticatedUserId()).thenReturn(userId);
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(productRepository.findById(productId)).thenReturn(Optional.of(product));

            BusinessException exception = assertThrows(BusinessException.class, () ->
                    orderService.createOrder(createOrderRequest));

            assertEquals("Product is not active: " + productId, exception.getMessage());

            verify(jwtService).getAuthenticatedUserId();
            verify(userRepository).findById(userId);
            verify(productRepository).findById(productId);
            verifyNoMoreInteractions(jwtService, userRepository, productRepository, orderRepository);
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when user not found")
        void shouldThrowWhenUserNotFound() {
            when(jwtService.getAuthenticatedUserId()).thenReturn(userId);
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                    orderService.createOrder(createOrderRequest));

            assertEquals("User not found: " + userId, exception.getMessage());

            verify(jwtService).getAuthenticatedUserId();
            verify(userRepository).findById(userId);
            verifyNoMoreInteractions(jwtService, userRepository, productRepository, orderRepository);
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when product not found")
        void shouldThrowWhenProductNotFound() {
            buildCreateOrderRequest();

            when(jwtService.getAuthenticatedUserId()).thenReturn(userId);
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(productRepository.findById(productId)).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                    orderService.createOrder(createOrderRequest));

            assertEquals("Product not found: " + productId, exception.getMessage());

            verify(jwtService).getAuthenticatedUserId();
            verify(userRepository).findById(userId);
            verify(productRepository).findById(productId);
            verifyNoMoreInteractions(jwtService, userRepository, productRepository, orderRepository);
        }
    }

    @Nested
    class PayOrder {

        @Test
        @DisplayName("Should pay order successfully")
        void shouldPayOrderSuccessfully() {
            Product product = buildProduct();
            OrderItem item = buildOrderItem(product, 1);
            Order order = buildOrder(item, product);

            when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
            when(productRepository.save(any())).thenReturn(product);
            when(orderRepository.save(any())).thenReturn(order);

            OrderResponse response = orderService.payOrder(orderId);

            assertEquals(orderId, response.getId());
            assertEquals(OrderStatus.PAID, response.getStatus());
            assertEquals(new BigDecimal("100.00"), response.getTotalPrice());
            assertEquals(1, response.getItems().size());

            verify(orderRepository).findById(orderId);
            verify(productRepository).save(any(Product.class));
            verify(orderRepository).save(any(Order.class));
            verifyNoMoreInteractions(productRepository, orderRepository);
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when order not found")
        void shouldThrowWhenOrderNotFound() {
            when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                    orderService.payOrder(orderId));

            assertEquals("Order not found: " + orderId, exception.getMessage());

            verify(orderRepository).findById(orderId);
            verifyNoMoreInteractions(productRepository, orderRepository);
        }

        @Test
        @DisplayName("Should throw BusinessException when order already processed")
        void shouldThrowWhenOrderAlreadyProcessed() {
            Order order = Order.builder().id(orderId).status(OrderStatus.PAID).build();

            when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

            BusinessException exception = assertThrows(BusinessException.class, () ->
                    orderService.payOrder(orderId));

            assertEquals("Order already processed: " + orderId, exception.getMessage());

            verify(orderRepository).findById(orderId);
            verifyNoMoreInteractions(productRepository, orderRepository);
        }

        @Test
        @DisplayName("Should throw BusinessException and cancel order when insufficient stock")
        void shouldCancelOrderIfInsufficientStock() {
            Product product = buildProduct();
            OrderItem item = buildOrderItem(product, 5);
            Order order = buildOrder(item, product);

            when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
            when(orderRepository.save(any())).thenReturn(order);

            BusinessException exception = assertThrows(BusinessException.class, () ->
                    orderService.payOrder(orderId));

            String errorMessage = String.format("Insufficient stock for product '%s': available only %d",
                    product.getId(), product.getStockQuantity());

            assertEquals(errorMessage, exception.getMessage());
            assertEquals(OrderStatus.CANCELED, order.getStatus());

            verify(orderRepository).findById(orderId);
            verify(orderRepository).save(any(Order.class));
            verifyNoMoreInteractions(productRepository, orderRepository);
        }
    }

    @Nested
    class GetUserOrders {

        @Test
        @DisplayName("Should return orders of authenticated user")
        void shouldReturnUserOrdersSuccessfully() {
            Product product = buildProduct();
            OrderItem item = buildOrderItem(product, 1);
            Order order = buildOrder(item, product);

            when(jwtService.getAuthenticatedUserId()).thenReturn(userId);
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(orderRepository.findAllByUserId(userId)).thenReturn(List.of(order));

            List<OrderResponse> responses = orderService.getUserOrders();

            assertNotNull(responses);
            assertEquals(1, responses.size());

            verify(jwtService).getAuthenticatedUserId();
            verify(userRepository).findById(userId);
            verify(orderRepository).findAllByUserId(userId);
            verifyNoMoreInteractions(jwtService, userRepository, orderRepository);
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when user not found")
        void shouldThrowWhenUserNotFound() {
            when(jwtService.getAuthenticatedUserId()).thenReturn(userId);
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                    orderService.getUserOrders());

            assertEquals("User not found: " + userId, exception.getMessage());

            verify(jwtService).getAuthenticatedUserId();
            verify(userRepository).findById(userId);
            verifyNoMoreInteractions(jwtService, userRepository, orderRepository);
        }
    }

    private void buildCreateOrderRequest() {
        OrderItemRequest itemRequest = new OrderItemRequest(productId, 1);
        createOrderRequest = new CreateOrderRequest(List.of(itemRequest));
    }

    private Product buildProduct() {
        return Product.builder()
                .id(productId)
                .price(new BigDecimal("100.00"))
                .status(ProductStatus.ACTIVE)
                .stockQuantity(1)
                .build();
    }

    private OrderItem buildOrderItem(Product product, Integer quantity) {
        return OrderItem.builder()
                .id(orderItemId)
                .product(product)
                .quantity(quantity)
                .price(product.getPrice())
                .build();
    }

    private Order buildOrder(OrderItem orderItem, Product product) {
        return Order.builder()
                .id(orderId)
                .user(user)
                .items(List.of(orderItem))
                .totalPrice(product.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity())))
                .build();
    }
}
