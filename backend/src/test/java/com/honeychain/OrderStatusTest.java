package com.honeychain;

import com.honeychain.beekeeper.entity.BeekeeperProfile;
import com.honeychain.beekeeper.entity.BeekeeperVerificationStatus;
import com.honeychain.beekeeper.entity.PreferredLanguage;
import com.honeychain.beekeeper.repository.BeekeeperProfileRepository;
import com.honeychain.common.exception.BadRequestException;
import com.honeychain.common.exception.UnauthorizedException;
import com.honeychain.marketplace.entity.Product;
import com.honeychain.marketplace.repository.ProductRepository;
import com.honeychain.order.dto.OrderResponse;
import com.honeychain.order.entity.Order;
import com.honeychain.order.entity.OrderItem;
import com.honeychain.order.entity.OrderStatus;
import com.honeychain.order.mapper.OrderMapper;
import com.honeychain.order.repository.OrderRepository;
import com.honeychain.order.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderStatusTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private BeekeeperProfileRepository beekeeperProfileRepository;

    @Mock
    private ProductRepository productRepository;

    private OrderMapper orderMapper;
    private OrderServiceImpl orderService;

    private BeekeeperProfile beekeeperProfile;
    private Product product;
    private Order order;

    @BeforeEach
    void setUp() {
        orderMapper = new OrderMapper();
        orderService = new OrderServiceImpl(
                orderRepository,
                null,
                null,
                productRepository,
                null,
                beekeeperProfileRepository,
                null,
                orderMapper
        );

        beekeeperProfile = new BeekeeperProfile(
                1L, "KVIC-TN-001", "Ramesh Kumar", "Kotagiri", null,
                11.42, 76.88, PreferredLanguage.TAMIL, BeekeeperVerificationStatus.APPROVED
        );
        beekeeperProfile.setId(20L);

        product = new Product();
        product.setId(10L);
        product.setBeekeeperProfile(beekeeperProfile);

        order = new Order();
        order.setId(1L);
        order.setOrderNumber("HC-ORD-2026-000001");
        order.setOrderStatus(OrderStatus.CONFIRMED);

        OrderItem orderItem = new OrderItem(order, 10L, "Honey", new BigDecimal("1.0"), new BigDecimal("850.00"), new BigDecimal("850.00"));
        order.setOrderItems(List.of(orderItem));
    }

    @Test
    @DisplayName("Valid transition: CONFIRMED -> PACKED")
    void testConfirmedToPacked() {
        when(beekeeperProfileRepository.findByUserPhoneNumber("9876543213")).thenReturn(Optional.of(beekeeperProfile));
        when(orderRepository.findByOrderNumber("HC-ORD-2026-000001")).thenReturn(Optional.of(order));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderResponse res = orderService.updateBeekeeperOrderStatus("9876543213", "HC-ORD-2026-000001", OrderStatus.PACKED);

        assertEquals(OrderStatus.PACKED, res.getOrderStatus());
    }

    @Test
    @DisplayName("Valid transition: PACKED -> SHIPPED")
    void testPackedToShipped() {
        order.setOrderStatus(OrderStatus.PACKED);

        when(beekeeperProfileRepository.findByUserPhoneNumber("9876543213")).thenReturn(Optional.of(beekeeperProfile));
        when(orderRepository.findByOrderNumber("HC-ORD-2026-000001")).thenReturn(Optional.of(order));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderResponse res = orderService.updateBeekeeperOrderStatus("9876543213", "HC-ORD-2026-000001", OrderStatus.SHIPPED);

        assertEquals(OrderStatus.SHIPPED, res.getOrderStatus());
    }

    @Test
    @DisplayName("Valid transition: SHIPPED -> DELIVERED")
    void testShippedToDelivered() {
        order.setOrderStatus(OrderStatus.SHIPPED);

        when(beekeeperProfileRepository.findByUserPhoneNumber("9876543213")).thenReturn(Optional.of(beekeeperProfile));
        when(orderRepository.findByOrderNumber("HC-ORD-2026-000001")).thenReturn(Optional.of(order));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderResponse res = orderService.updateBeekeeperOrderStatus("9876543213", "HC-ORD-2026-000001", OrderStatus.DELIVERED);

        assertEquals(OrderStatus.DELIVERED, res.getOrderStatus());
    }

    @Test
    @DisplayName("Invalid transition: DELIVERED -> PACKED rejected")
    void testDeliveredToPackedRejected() {
        order.setOrderStatus(OrderStatus.DELIVERED);

        when(beekeeperProfileRepository.findByUserPhoneNumber("9876543213")).thenReturn(Optional.of(beekeeperProfile));
        when(orderRepository.findByOrderNumber("HC-ORD-2026-000001")).thenReturn(Optional.of(order));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));

        assertThrows(BadRequestException.class, () ->
                orderService.updateBeekeeperOrderStatus("9876543213", "HC-ORD-2026-000001", OrderStatus.PACKED));
    }

    @Test
    @DisplayName("Invalid transition: CONFIRMED -> SHIPPED (skipping PACKED) rejected")
    void testConfirmedToShippedRejected() {
        when(beekeeperProfileRepository.findByUserPhoneNumber("9876543213")).thenReturn(Optional.of(beekeeperProfile));
        when(orderRepository.findByOrderNumber("HC-ORD-2026-000001")).thenReturn(Optional.of(order));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));

        assertThrows(BadRequestException.class, () ->
                orderService.updateBeekeeperOrderStatus("9876543213", "HC-ORD-2026-000001", OrderStatus.SHIPPED));
    }

    @Test
    @DisplayName("Unowned order management rejected with UnauthorizedException")
    void testUnownedOrderRejected() {
        BeekeeperProfile otherProfile = new BeekeeperProfile();
        otherProfile.setId(99L);
        product.setBeekeeperProfile(otherProfile);

        when(beekeeperProfileRepository.findByUserPhoneNumber("9876543213")).thenReturn(Optional.of(beekeeperProfile));
        when(orderRepository.findByOrderNumber("HC-ORD-2026-000001")).thenReturn(Optional.of(order));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));

        assertThrows(UnauthorizedException.class, () ->
                orderService.updateBeekeeperOrderStatus("9876543213", "HC-ORD-2026-000001", OrderStatus.PACKED));
    }
}
