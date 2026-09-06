package com.honeychain;

import com.honeychain.cart.dto.AddCartItemRequest;
import com.honeychain.cart.dto.CartResponse;
import com.honeychain.cart.dto.UpdateCartItemRequest;
import com.honeychain.cart.entity.Cart;
import com.honeychain.cart.entity.CartItem;
import com.honeychain.cart.mapper.CartMapper;
import com.honeychain.cart.repository.CartItemRepository;
import com.honeychain.cart.repository.CartRepository;
import com.honeychain.cart.service.impl.CartServiceImpl;
import com.honeychain.common.exception.BadRequestException;
import com.honeychain.marketplace.entity.Product;
import com.honeychain.marketplace.repository.ProductRepository;
import com.honeychain.user.entity.Role;
import com.honeychain.user.entity.User;
import com.honeychain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    private CartMapper cartMapper;
    private CartServiceImpl cartService;

    private User customer;
    private Product activeProduct;
    private Cart cart;

    @BeforeEach
    void setUp() {
        cartMapper = new CartMapper();
        cartService = new CartServiceImpl(
                cartRepository,
                cartItemRepository,
                productRepository,
                userRepository,
                cartMapper
        );

        customer = new User("9876543214", "pass", Role.CUSTOMER);
        customer.setId(5L);

        activeProduct = new Product();
        activeProduct.setId(10L);
        activeProduct.setProductName("Nilgiris Raw Honey");
        activeProduct.setPricePerKg(new BigDecimal("850.00"));
        activeProduct.setAvailableQuantityKg(new BigDecimal("10.00"));
        activeProduct.setIsActive(true);

        cart = new Cart(customer.getId());
        cart.setId(1L);
        cart.setItems(new ArrayList<>());
    }

    @Test
    @DisplayName("Should get empty cart when customer has no existing cart items")
    void testGetMyCartEmpty() {
        when(userRepository.findByPhoneNumber("9876543214")).thenReturn(Optional.of(customer));
        when(cartRepository.findByCustomerId(5L)).thenReturn(Optional.of(cart));

        CartResponse response = cartService.getMyCart("9876543214");

        assertNotNull(response);
        assertEquals(0, response.getItemCount());
        assertEquals(BigDecimal.ZERO, response.getSubtotal());
    }

    @Test
    @DisplayName("Should add new product to cart and calculate server-side price")
    void testAddItemSuccess() {
        AddCartItemRequest req = new AddCartItemRequest();
        req.setProductId(10L);
        req.setQuantityKg(new BigDecimal("1.50"));

        when(userRepository.findByPhoneNumber("9876543214")).thenReturn(Optional.of(customer));
        when(cartRepository.findByCustomerId(5L)).thenReturn(Optional.of(cart));
        when(productRepository.findById(10L)).thenReturn(Optional.of(activeProduct));
        when(cartItemRepository.findByCartIdAndProductId(1L, 10L)).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CartResponse response = cartService.addItem("9876543214", req);

        assertNotNull(response);
        assertEquals(1, response.getItemCount());
        assertEquals(new BigDecimal("1275.00"), response.getSubtotal());
        assertEquals(new BigDecimal("850.00"), response.getItems().get(0).getUnitPrice());
    }

    @Test
    @DisplayName("Should reject adding inactive product to cart")
    void testAddInactiveProductRejected() {
        activeProduct.setIsActive(false);

        AddCartItemRequest req = new AddCartItemRequest();
        req.setProductId(10L);
        req.setQuantityKg(new BigDecimal("1.00"));

        when(userRepository.findByPhoneNumber("9876543214")).thenReturn(Optional.of(customer));
        when(cartRepository.findByCustomerId(5L)).thenReturn(Optional.of(cart));
        when(productRepository.findById(10L)).thenReturn(Optional.of(activeProduct));

        assertThrows(BadRequestException.class, () -> cartService.addItem("9876543214", req));
    }

    @Test
    @DisplayName("Should reject adding quantity exceeding available stock")
    void testAddExceedingStockRejected() {
        AddCartItemRequest req = new AddCartItemRequest();
        req.setProductId(10L);
        req.setQuantityKg(new BigDecimal("15.00")); // 15kg > 10kg available

        when(userRepository.findByPhoneNumber("9876543214")).thenReturn(Optional.of(customer));
        when(cartRepository.findByCustomerId(5L)).thenReturn(Optional.of(cart));
        when(productRepository.findById(10L)).thenReturn(Optional.of(activeProduct));

        assertThrows(BadRequestException.class, () -> cartService.addItem("9876543214", req));
    }

    @Test
    @DisplayName("Should reject zero or negative quantity")
    void testAddZeroOrNegativeQuantityRejected() {
        AddCartItemRequest req = new AddCartItemRequest();
        req.setProductId(10L);
        req.setQuantityKg(BigDecimal.ZERO);

        assertThrows(BadRequestException.class, () -> cartService.addItem("9876543214", req));
    }

    @Test
    @DisplayName("Should update cart item quantity successfully")
    void testUpdateItemQuantity() {
        CartItem item = new CartItem(cart, activeProduct, new BigDecimal("1.00"), activeProduct.getPricePerKg());
        item.setId(100L);
        cart.getItems().add(item);

        UpdateCartItemRequest req = new UpdateCartItemRequest();
        req.setQuantityKg(new BigDecimal("2.50"));

        when(userRepository.findByPhoneNumber("9876543214")).thenReturn(Optional.of(customer));
        when(cartRepository.findByCustomerId(5L)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findById(100L)).thenReturn(Optional.of(item));
        when(productRepository.findById(10L)).thenReturn(Optional.of(activeProduct));
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CartResponse response = cartService.updateItem("9876543214", 100L, req);

        assertNotNull(response);
        assertEquals(new BigDecimal("2125.00"), response.getSubtotal());
    }

    @Test
    @DisplayName("Should clear all items from cart")
    void testClearCart() {
        CartItem item = new CartItem(cart, activeProduct, new BigDecimal("1.00"), activeProduct.getPricePerKg());
        cart.getItems().add(item);

        when(userRepository.findByPhoneNumber("9876543214")).thenReturn(Optional.of(customer));
        when(cartRepository.findByCustomerId(5L)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CartResponse response = cartService.clearCart("9876543214");

        assertNotNull(response);
        assertEquals(0, response.getItemCount());
        assertEquals(BigDecimal.ZERO, response.getSubtotal());
    }
}
