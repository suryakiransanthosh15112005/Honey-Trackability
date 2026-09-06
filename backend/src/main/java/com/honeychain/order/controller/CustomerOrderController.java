package com.honeychain.order.controller;

import com.honeychain.common.dto.ApiResponse;
import com.honeychain.common.dto.PageResponse;
import com.honeychain.order.dto.CheckoutRequest;
import com.honeychain.order.dto.OrderResponse;
import com.honeychain.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Customer Orders", description = "Customer checkout and order tracking endpoints")
public class CustomerOrderController {

    private final OrderService orderService;

    public CustomerOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/checkout")
    @Operation(summary = "Checkout active cart, process payment, and create order")
    public ResponseEntity<ApiResponse<OrderResponse>> checkout(
            @Valid @RequestBody CheckoutRequest request,
            Authentication auth) {
        OrderResponse response = orderService.checkout(auth.getName(), request);
        return ResponseEntity.ok(ApiResponse.success("Order placed successfully", response));
    }

    @GetMapping
    @Operation(summary = "Get paginated order history for authenticated customer")
    public ResponseEntity<ApiResponse<PageResponse<OrderResponse>>> getMyOrders(
            @PageableDefault(size = 10) Pageable pageable,
            Authentication auth) {
        PageResponse<OrderResponse> response = orderService.getMyOrders(auth.getName(), pageable);
        return ResponseEntity.ok(ApiResponse.success("Orders retrieved", response));
    }

    @GetMapping("/{orderNumber}")
    @Operation(summary = "Get order details by order number")
    public ResponseEntity<ApiResponse<OrderResponse>> getMyOrderByNumber(
            @PathVariable String orderNumber,
            Authentication auth) {
        OrderResponse response = orderService.getMyOrderByNumber(auth.getName(), orderNumber);
        return ResponseEntity.ok(ApiResponse.success("Order details retrieved", response));
    }

    @PutMapping("/{orderNumber}/cancel")
    @Operation(summary = "Cancel an order (only allowed if status is CONFIRMED)")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(
            @PathVariable String orderNumber,
            Authentication auth) {
        OrderResponse response = orderService.cancelOrder(auth.getName(), orderNumber);
        return ResponseEntity.ok(ApiResponse.success("Order cancelled successfully", response));
    }
}
