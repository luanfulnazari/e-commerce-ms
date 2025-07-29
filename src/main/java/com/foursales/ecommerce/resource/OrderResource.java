package com.foursales.ecommerce.resource;

import com.foursales.ecommerce.resource.request.CreateOrderRequest;
import com.foursales.ecommerce.resource.response.OrderResponse;
import com.foursales.ecommerce.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("v1/orders")
@RequiredArgsConstructor
public class OrderResource {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(
            @RequestBody @Valid CreateOrderRequest request) {
        return orderService.createOrder(request);
    }

    @PostMapping("/{id}/pay")
    @ResponseStatus(HttpStatus.OK)
    public OrderResponse payOrder(@PathVariable UUID id) {
        return orderService.payOrder(id);
    }

    @GetMapping("/my")
    @ResponseStatus(HttpStatus.OK)
    public List<OrderResponse> getMyOrders() {
        return orderService.getUserOrders();
    }
}
