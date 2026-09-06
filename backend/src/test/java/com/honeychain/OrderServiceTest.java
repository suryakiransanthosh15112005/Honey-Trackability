package com.honeychain;

import com.honeychain.beekeeper.entity.BeekeeperProfile;
import com.honeychain.beekeeper.entity.BeekeeperVerificationStatus;
import com.honeychain.beekeeper.entity.PreferredLanguage;
import com.honeychain.beekeeper.repository.BeekeeperProfileRepository;
import com.honeychain.cart.entity.Cart;
import com.honeychain.cart.entity.CartItem;
import com.honeychain.cart.repository.CartRepository;
import com.honeychain.common.exception.BadRequestException;
import com.honeychain.marketplace.entity.Product;
import com.honeychain.marketplace.repository.ProductRepository;
import com.honeychain.order.dto.CheckoutRequest;
import com.honeychain.order.dto.DeliveryAddressDto;
import com.honeychain.order.dto.OrderResponse;
import com.honeychain.order.entity.FulfillmentType;
import com.honeychain.order.entity.Order;
import com.honeychain.order.entity.OrderStatus;
import com.honeychain.order.mapper.OrderMapper;
import com.honeychain.order.repository.OrderItemRepository;
import com.honeychain.order.repository.OrderRepository;
import com.honeychain.order.service.impl.OrderServiceImpl;
import com.honeychain.payment.dto.PaymentRequest;
import com.honeychain.payment.dto.PaymentResult;
import com.honeychain.payment.exception.PaymentFailedException;
import com.honeychain.payment.service.PaymentService;
import com.honeychain.payment.service.PaymentStatus;
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BeekeeperProfileRepository beekeeperProfileRepository;

    @Mock
    private PaymentService paymentService;

    private OrderMapper orderMapper;
    private OrderServiceImpl orderService;

    private User customer;
    private BeekeeperProfile beekeeperProfile;
    private Product product;
    private Cart cart;

    @BeforeEach
    void setUp() {
        orderMapper = new OrderMapper();
        orderService = new OrderServiceImpl(
                orderRepository,
                orderItemRepository,
                cartRepository,
                productRepository,
                userRepository,
                beekeeperProfileRepository,
                paymentService,
                orderMapper
        );

        customer = new User("9876543214", "pass", Role.CUSTOMER);
        customer.setId(5L);

        beekeeperProfile = new BeekeeperProfile(
                1L, "KVIC-TN-001", "Ramesh Kumar", "Kotagiri", null,
                11.42, 76.88, PreferredLanguage.TAMIL, BeekeeperVerificationStatus.APPROVED
        );
        beekeeperProfile.setId(20L);

        product = new Product();
        product.setId(10L);
        product.setProductName("Nilgiris Raw Honey");
        product.setBeekeeperProfile(beekeeperProfile);
        product.setPricePerKg(new BigDecimal("850.00"));
        product.setAvailableQuantityKg(new BigDecimal("10.00"));
        product.setIsActive(true);

        cart = new Cart(customer.getId());
        cart.setId(1L);
        cart.setItems(new ArrayList<>());
    }

    @Test
    @DisplayName("Should reject checkout when cart is empty")
    void testCheckoutEmptyCartRejected() {
        CheckoutRequest req = new CheckoutRequest(FulfillmentType.LOCAL_PICKUP, null, "mock");

        when(userRepository.findByPhoneNumber("9876543214")).thenReturn(Optional.of(customer));
        when(cartRepository.findByCustomerId(5L)).thenReturn(Optional.of(cart));

        assertThrows(BadRequestException.class, () -> orderService.checkout("9876543214", req));
    }

    @Test
    @DisplayName("Should process checkout successfully: calculate server-side price, create order, deduct stock, clear cart")
    void testCheckoutSuccess() {
        CartItem item = new CartItem(cart, product, new BigDecimal("2.00"), new BigDecimal("850.00"));
        cart.getItems().add(item);

        DeliveryAddressDto address = new DeliveryAddressDto("Customer", "Line 1", "Line 2", "Coimbatore", "TN", "641001");
        CheckoutRequest req = new CheckoutRequest(FulfillmentType.DELIVERY, address, "mock");

        when(userRepository.findByPhoneNumber("9876543214")).thenReturn(Optional.of(customer));
        when(cartRepository.findByCustomerId(5L)).thenReturn(Optional.of(cart));
        when(productRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(product));
        when(orderRepository.countTotalOrders()).thenReturn(0L);
                

        PaymentResult paymentResult = new PaymentResult("MOCK-PAY-123456", PaymentStatus.SUCCESS, "Success");
        when(paymentService.processPayment(any(PaymentRequest.class))).thenReturn(paymentResult);

        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order o = invocation.getArgument(0);
            o.setId(100L);
            return o;
        });

        OrderResponse response = orderService.checkout("9876543214", req);

        assertNotNull(response);
        assertEquals(new BigDecimal("1700.00"), response.getTotalAmount());
        assertEquals(OrderStatus.CONFIRMED, response.getOrderStatus());
        assertEquals(PaymentStatus.SUCCESS, response.getPaymentStatus());
        assertTrue(response.getOrderNumber().startsWith("HC-ORD-"));

        // Verify stock deducted from 10.00 to 8.00
        assertEquals(new BigDecimal("8.00"), product.getAvailableQuantityKg());
        assertTrue(product.getIsActive());

        // Verify cart cleared
        assertTrue(cart.getItems().isEmpty());
    }

    @Test
    @DisplayName("Should deactivate product when stock reaches zero")
    void testProductDeactivatedOnZeroStock() {
        CartItem item = new CartItem(cart, product, new BigDecimal("10.00"), new BigDecimal("850.00"));
        cart.getItems().add(item);

        CheckoutRequest req = new CheckoutRequest(FulfillmentType.LOCAL_PICKUP, null, "mock");

        when(userRepository.findByPhoneNumber("9876543214")).thenReturn(Optional.of(customer));
        when(cartRepository.findByCustomerId(5L)).thenReturn(Optional.of(cart));
        when(productRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(product));
        when(orderRepository.countTotalOrders()).thenReturn(0L);

        PaymentResult paymentResult = new PaymentResult("MOCK-PAY-999", PaymentStatus.SUCCESS, "Success");
        when(paymentService.processPayment(any(PaymentRequest.class))).thenReturn(paymentResult);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        orderService.checkout("9876543214", req);

        assertEquals(0, product.getAvailableQuantityKg().compareTo(BigDecimal.ZERO));
        assertFalse(product.getIsActive());
    }


    @Test
    @DisplayName("Should throw PaymentFailedException and NOT deduct stock if payment fails")
    void testPaymentFailurePreservesStockAndCart() {
        CartItem item = new CartItem(cart, product, new BigDecimal("2.00"), new BigDecimal("850.00"));
        cart.getItems().add(item);

        CheckoutRequest req = new CheckoutRequest(FulfillmentType.LOCAL_PICKUP, null, "fail");

        when(userRepository.findByPhoneNumber("9876543214")).thenReturn(Optional.of(customer));
        when(cartRepository.findByCustomerId(5L)).thenReturn(Optional.of(cart));
        when(productRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(product));
        when(orderRepository.countTotalOrders()).thenReturn(0L);

        PaymentResult failedPayment = new PaymentResult(null, PaymentStatus.FAILED, "Card declined");
        when(paymentService.processPayment(any(PaymentRequest.class))).thenReturn(failedPayment);

        assertThrows(PaymentFailedException.class, () -> orderService.checkout("9876543214", req));

        // Verify stock remains untouched
        assertEquals(new BigDecimal("10.00"), product.getAvailableQuantityKg());
        // Verify cart items not cleared
        assertEquals(1, cart.getItems().size());
        verify(orderRepository, never()).save(any(Order.class));
    }
}
