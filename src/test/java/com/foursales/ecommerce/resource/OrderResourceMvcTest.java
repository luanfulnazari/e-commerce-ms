package com.foursales.ecommerce.resource;

import com.foursales.ecommerce.enums.OrderStatus;
import com.foursales.ecommerce.resource.request.CreateOrderRequest;
import com.foursales.ecommerce.resource.request.OrderItemRequest;
import com.foursales.ecommerce.resource.response.OrderItemResponse;
import com.foursales.ecommerce.resource.response.OrderResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = OrderResource.class)
class OrderResourceMvcTest extends AbstractResourceMvcTest {

    private final UUID orderItemId = UUID.randomUUID();
    private final UUID orderId = UUID.randomUUID();
    private final UUID productId = UUID.randomUUID();
    private final String productName = "product name";
    private final BigDecimal price = new BigDecimal("500.00");
    private final BigDecimal totalPrice = new BigDecimal("1000.00");
    private final Integer quantity = 2;

    @Nested
    class CreateOrder {

        @Test
        @DisplayName("Should return 201 when order is successfully created")
        void shouldReturnCreatedWhenOrderIsCreated() throws Exception {

            CreateOrderRequest request = new CreateOrderRequest(List.of(
                    new OrderItemRequest(orderItemId, quantity)
            ));

            OrderItemResponse orderItemResponse = new OrderItemResponse(productId, productName, quantity, price);
            OrderResponse response = new OrderResponse(orderId, OrderStatus.PENDING, totalPrice, List.of(orderItemResponse));
            String expectedJson = objectMapper.writeValueAsString(response);
            when(orderService.createOrder(any(CreateOrderRequest.class))).thenReturn(response);

            mockMvc.perform(post("/v1/orders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(content().json(expectedJson));

            verify(orderService).createOrder(any());
        }

        @Test
        @DisplayName("Should return BadRequest when required fields are null")
        void shouldReturnBadRequest_whenRequiredFieldsAreNull() throws Exception {

            CreateOrderRequest request = new CreateOrderRequest(List.of(
                    new OrderItemRequest(null, null)
            ));

            mockMvc.perform(post("/v1/orders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message.size()")
                            .value(2))
                    .andExpect(jsonPath("$.message[0].error")
                            .value("must not be null"));

            verify(orderService, never()).createOrder(any());
        }

        @Test
        @DisplayName("Should return BadRequest when order items is empty")
        void shouldReturnBadRequest_whenOrderItemsIsEmpty() throws Exception {

            CreateOrderRequest request = new CreateOrderRequest(List.of());

            mockMvc.perform(post("/v1/orders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message[0].error")
                            .value("must not be empty"));

            verify(orderService, never()).createOrder(any());
        }
    }

    @Nested
    class PayOrder {

        @Test
        @DisplayName("Should return 200 when order is paid")
        void shouldReturnOkWhenOrderIsPaid() throws Exception {

            OrderItemResponse orderItemResponse = new OrderItemResponse(productId, productName, quantity, price);
            OrderResponse response = new OrderResponse(orderId, OrderStatus.PAID, totalPrice, List.of(orderItemResponse));
            String expectedJson = objectMapper.writeValueAsString(response);
            when(orderService.payOrder(orderId)).thenReturn(response);

            mockMvc.perform(post("/v1/orders/{id}/pay", orderId))
                    .andExpect(status().isOk())
                    .andExpect(content().json(expectedJson));

            verify(orderService).payOrder(orderId);
        }
    }

    @Nested
    class GetMyOrders {

        @Test
        @DisplayName("Should return 200 with list of orders")
        void shouldReturnListOfOrders() throws Exception {

            OrderItemResponse orderItemResponse = new OrderItemResponse(productId, productName, quantity, price);
            OrderResponse response = new OrderResponse(orderId, OrderStatus.PAID, totalPrice, List.of(orderItemResponse));
            String expectedJson = objectMapper.writeValueAsString(List.of(response));
            when(orderService.getUserOrders()).thenReturn(List.of(response));

            mockMvc.perform(get("/v1/orders/my"))
                    .andExpect(status().isOk())
                    .andExpect(content().json(expectedJson));

            verify(orderService).getUserOrders();
        }

        @Test
        @DisplayName("Should return empty list when no orders found")
        void shouldReturnEmptyList() throws Exception {
            String expectedJson = objectMapper.writeValueAsString(List.of());
            when(orderService.getUserOrders()).thenReturn(List.of());

            mockMvc.perform(get("/v1/orders/my"))
                    .andExpect(status().isOk())
                    .andExpect(content().json(expectedJson));

            verify(orderService).getUserOrders();
        }
    }
}
