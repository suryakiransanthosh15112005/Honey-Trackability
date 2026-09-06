package com.honeychain.cart.controller;

import com.honeychain.cart.dto.AddCartItemRequest;
import com.honeychain.cart.dto.CartResponse;
import com.honeychain.cart.dto.UpdateCartItemRequest;
import com.honeychain.cart.service.CartService;
import com.honeychain.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Cart management endpoints — CUSTOMER role required.
 * Customer identity is always derived from JWT (authentication.getName()),
 * never from client-submitted IDs.
 */
@RestController
@RequestMapping("/api/cart")
@Tag(name = "Cart", description = "Customer cart management")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    @Operation(summary = "Get current customer cart")
    public ResponseEntity<ApiResponse<CartResponse>> getCart(Authentication auth) {
        CartResponse cart = cartService.getMyCart(auth.getName());
        return ResponseEntity.ok(ApiResponse.success("Cart retrieved", cart));
    }

    @PostMapping("/items")
    @Operation(summary = "Add product to cart (or increment if already present)")
    public ResponseEntity<ApiResponse<CartResponse>> addItem(
            @Valid @RequestBody AddCartItemRequest request,
            Authentication auth) {
        CartResponse cart = cartService.addItem(auth.getName(), request);
        return ResponseEntity.ok(ApiResponse.success("Item added to cart", cart));
    }

    @PutMapping("/items/{id}")
    @Operation(summary = "Update cart item quantity")
    public ResponseEntity<ApiResponse<CartResponse>> updateItem(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCartItemRequest request,
            Authentication auth) {
        CartResponse cart = cartService.updateItem(auth.getName(), id, request);
        return ResponseEntity.ok(ApiResponse.success("Cart item updated", cart));
    }

    @DeleteMapping("/items/{id}")
    @Operation(summary = "Remove a specific item from cart")
    public ResponseEntity<ApiResponse<CartResponse>> removeItem(
            @PathVariable Long id,
            Authentication auth) {
        CartResponse cart = cartService.removeItem(auth.getName(), id);
        return ResponseEntity.ok(ApiResponse.success("Item removed from cart", cart));
    }

    @DeleteMapping
    @Operation(summary = "Clear all items from cart")
    public ResponseEntity<ApiResponse<CartResponse>> clearCart(Authentication auth) {
        CartResponse cart = cartService.clearCart(auth.getName());
        return ResponseEntity.ok(ApiResponse.success("Cart cleared", cart));
    }
}
