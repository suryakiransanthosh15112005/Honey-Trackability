package com.honeychain.order.controller;

import com.honeychain.common.dto.ApiResponse;
import com.honeychain.common.dto.PageResponse;
import com.honeychain.order.dto.OrderResponse;
import com.honeychain.order.dto.OrderStatusUpdateRequest;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/beekeepers/orders")
@Tag(name = "Beekeeper Orders", description = "Beekeeper order fulfillment and management endpoints")
public class BeekeeperOrderController {

    private final OrderService orderService;

    public BeekeeperOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    @Operation(summary = "Get paginated orders containing beekeeper's products")
    public ResponseEntity<ApiResponse<PageResponse<OrderResponse>>> getBeekeeperOrders(
            @PageableDefault(size = 10) Pageable pageable,
            Authentication auth) {
        PageResponse<OrderResponse> response = orderService.getBeekeeperOrders(auth.getName(), pageable);
        return ResponseEntity.ok(ApiResponse.success("Beekeeper orders retrieved", response));
    }

    @PutMapping("/{orderNumber}/status")
    @Operation(summary = "Update order fulfillment status (CONFIRMED -> PACKED -> SHIPPED -> DELIVERED)")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @PathVariable String orderNumber,
            @Valid @RequestBody OrderStatusUpdateRequest request,
            Authentication auth) {
        OrderResponse response = orderService.updateBeekeeperOrderStatus(auth.getName(), orderNumber, request.getStatus());
        return ResponseEntity.ok(ApiResponse.success("Order status updated successfully", response));
    }
}
