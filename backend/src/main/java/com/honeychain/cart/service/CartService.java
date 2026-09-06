package com.honeychain.cart.service;

import com.honeychain.cart.dto.AddCartItemRequest;
import com.honeychain.cart.dto.CartResponse;
import com.honeychain.cart.dto.UpdateCartItemRequest;

public interface CartService {

    /** Get (or lazily create) the customer's cart. */
    CartResponse getMyCart(String customerPhone);

    /**
     * Add a product to cart. If already present, increments quantity up to stock
     * limit.
     */
    CartResponse addItem(String customerPhone, AddCartItemRequest request);

    /** Update the quantity of an existing cart item. */
    CartResponse updateItem(String customerPhone, Long cartItemId, UpdateCartItemRequest request);

    /** Remove a specific item from the cart. */
    CartResponse removeItem(String customerPhone, Long cartItemId);

    /** Remove all items from the cart (does NOT delete the cart entity). */
    CartResponse clearCart(String customerPhone);
}
