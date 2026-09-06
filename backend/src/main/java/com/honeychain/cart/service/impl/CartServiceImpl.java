package com.honeychain.cart.service.impl;

import com.honeychain.cart.dto.AddCartItemRequest;
import com.honeychain.cart.dto.CartResponse;
import com.honeychain.cart.dto.UpdateCartItemRequest;
import com.honeychain.cart.entity.Cart;
import com.honeychain.cart.entity.CartItem;
import com.honeychain.cart.mapper.CartMapper;
import com.honeychain.cart.repository.CartItemRepository;
import com.honeychain.cart.repository.CartRepository;
import com.honeychain.cart.service.CartService;
import com.honeychain.common.exception.BadRequestException;
import com.honeychain.common.exception.ResourceNotFoundException;
import com.honeychain.common.exception.UnauthorizedException;
import com.honeychain.marketplace.entity.Product;
import com.honeychain.marketplace.repository.ProductRepository;
import com.honeychain.user.entity.User;
import com.honeychain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartMapper cartMapper;

    public CartServiceImpl(CartRepository cartRepository,
                           CartItemRepository cartItemRepository,
                           ProductRepository productRepository,
                           UserRepository userRepository,
                           CartMapper cartMapper) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.cartMapper = cartMapper;
    }

    // ── Get or create ───────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public CartResponse getMyCart(String customerPhone) {
        User user = getUser(customerPhone);
        Cart cart = cartRepository.findByCustomerId(user.getId())
                .orElseGet(() -> new Cart(user.getId())); // return empty cart view without persisting
        return cartMapper.toCartResponse(cart);
    }

    // ── Add item ─────────────────────────────────────────────────────

    @Override
    public CartResponse addItem(String customerPhone, AddCartItemRequest request) {
        validateQuantity(request.getQuantityKg());

        User user = getUser(customerPhone);
        Cart cart = getOrCreateCart(user);

        Product product = getActiveProduct(request.getProductId());
        validateStock(product, request.getQuantityKg());

        // If product already in cart, increment quantity
        cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId())
                .ifPresentOrElse(
                        existing -> {
                            BigDecimal newQty = existing.getQuantityKg().add(request.getQuantityKg());
                            validateStock(product, newQty);
                            existing.setQuantityKg(newQty);
                            // Refresh price snapshot on every add (keeps price fresh)
                            existing.setUnitPriceSnapshot(product.getPricePerKg());
                        },
                        () -> {
                            CartItem newItem = new CartItem(cart, product, request.getQuantityKg(),
                                    product.getPricePerKg());
                            cart.getItems().add(newItem);
                        }
                );

        Cart saved = cartRepository.save(cart);
        return cartMapper.toCartResponse(saved);
    }

    // ── Update item ───────────────────────────────────────────────────

    @Override
    public CartResponse updateItem(String customerPhone, Long cartItemId, UpdateCartItemRequest request) {
        validateQuantity(request.getQuantityKg());

        User user = getUser(customerPhone);
        Cart cart = getCart(user);

        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found: " + cartItemId));

        // Ownership: item must belong to this customer's cart
        if (!item.getCart().getId().equals(cart.getId())) {
            throw new UnauthorizedException("Access denied to this cart item.");
        }

        Product product = getActiveProduct(item.getProduct().getId());
        validateStock(product, request.getQuantityKg());

        item.setQuantityKg(request.getQuantityKg());
        item.setUnitPriceSnapshot(product.getPricePerKg()); // refresh snapshot

        Cart saved = cartRepository.save(cart);
        return cartMapper.toCartResponse(saved);
    }

    // ── Remove item ───────────────────────────────────────────────────

    @Override
    public CartResponse removeItem(String customerPhone, Long cartItemId) {
        User user = getUser(customerPhone);
        Cart cart = getCart(user);

        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found: " + cartItemId));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new UnauthorizedException("Access denied to this cart item.");
        }

        cart.getItems().remove(item);
        Cart saved = cartRepository.save(cart);
        return cartMapper.toCartResponse(saved);
    }

    // ── Clear cart ────────────────────────────────────────────────────

    @Override
    public CartResponse clearCart(String customerPhone) {
        User user = getUser(customerPhone);
        Cart cart = cartRepository.findByCustomerId(user.getId())
                .orElseGet(() -> new Cart(user.getId()));

        if (cart.getId() != null) {
            cart.clearItems();
            cartRepository.save(cart);
        }

        return cartMapper.toCartResponse(cart);
    }

    // ── Private helpers ────────────────────────────────────────────────

    private User getUser(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + phoneNumber));
    }

    private Cart getOrCreateCart(User user) {
        return cartRepository.findByCustomerId(user.getId())
                .orElseGet(() -> cartRepository.save(new Cart(user.getId())));
    }

    private Cart getCart(User user) {
        return cartRepository.findByCustomerId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for customer."));
    }

    private Product getActiveProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));
        if (Boolean.FALSE.equals(product.getIsActive())) {
            throw new BadRequestException("Product '" + product.getProductName() + "' is no longer available.");
        }
        return product;
    }

    private void validateQuantity(BigDecimal qty) {
        if (qty == null || qty.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Quantity must be greater than zero.");
        }
    }

    private void validateStock(Product product, BigDecimal requiredQty) {
        if (product.getAvailableQuantityKg().compareTo(requiredQty) < 0) {
            throw new BadRequestException("Only " + product.getAvailableQuantityKg() + " kg available for '"
                    + product.getProductName() + "'. Requested: " + requiredQty + " kg.");
        }
    }
}
