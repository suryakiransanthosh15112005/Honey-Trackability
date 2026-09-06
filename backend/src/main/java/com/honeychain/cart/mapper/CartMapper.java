package com.honeychain.cart.mapper;

import com.honeychain.cart.dto.CartItemResponse;
import com.honeychain.cart.dto.CartResponse;
import com.honeychain.cart.entity.Cart;
import com.honeychain.cart.entity.CartItem;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Component
public class CartMapper {

    public CartItemResponse toItemResponse(CartItem item) {
        CartItemResponse dto = new CartItemResponse();
        dto.setId(item.getId());
        dto.setProductId(item.getProduct().getId());
        dto.setProductName(item.getProduct().getProductName());
        dto.setImageUrl(item.getProduct().getImageUrl());
        dto.setQuantityKg(item.getQuantityKg());
        dto.setUnitPrice(item.getUnitPriceSnapshot());
        BigDecimal subtotal = item.getUnitPriceSnapshot()
                .multiply(item.getQuantityKg())
                .setScale(2, RoundingMode.HALF_UP);
        dto.setSubtotal(subtotal);
        dto.setAvailableQuantityKg(item.getProduct().getAvailableQuantityKg());
        return dto;
    }


    public CartResponse toCartResponse(Cart cart) {
        List<CartItemResponse> itemResponses = cart.getItems().stream()
                .map(this::toItemResponse)
                .toList();

        BigDecimal subtotal = itemResponses.stream()
                .map(CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        CartResponse response = new CartResponse();
        response.setCartId(cart.getId());
        response.setItems(itemResponses);
        response.setSubtotal(subtotal);
        response.setItemCount(itemResponses.size());
        return response;
    }
}
