package com.honeychain;

import com.honeychain.beekeeper.entity.BeekeeperProfile;
import com.honeychain.common.exception.BadRequestException;
import com.honeychain.common.exception.ConflictException;
import com.honeychain.common.exception.ResourceNotFoundException;
import com.honeychain.common.exception.UnauthorizedException;
import com.honeychain.marketplace.entity.Product;
import com.honeychain.marketplace.repository.ProductRepository;
import com.honeychain.order.entity.Order;
import com.honeychain.order.entity.OrderItem;
import com.honeychain.order.entity.OrderStatus;
import com.honeychain.order.repository.OrderItemRepository;
import com.honeychain.review.dto.ReviewCreateRequest;
import com.honeychain.review.dto.ReviewResponse;
import com.honeychain.review.dto.ReviewUpdateRequest;
import com.honeychain.review.entity.Review;
import com.honeychain.review.mapper.ReviewMapper;
import com.honeychain.review.repository.ReviewRepository;
import com.honeychain.review.service.impl.ReviewServiceImpl;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    private ReviewMapper reviewMapper;
    private ReviewServiceImpl reviewService;

    private User customer;
    private Product product;
    private BeekeeperProfile beekeeper;
    private Order deliveredOrder;
    private OrderItem orderItem;

    @BeforeEach
    void setUp() {
        reviewMapper = new ReviewMapper();
        reviewService = new ReviewServiceImpl(
                reviewRepository,
                orderItemRepository,
                productRepository,
                userRepository,
                reviewMapper
        );

        customer = new User("9876543214", "pass", Role.CUSTOMER);
        customer.setId(5L);

        beekeeper = new BeekeeperProfile();
        beekeeper.setId(20L);
        beekeeper.setName("Ramesh Kumar");

        product = new Product();
        product.setId(10L);
        product.setProductName("Nilgiris Raw Honey");
        product.setBeekeeperProfile(beekeeper);
        product.setRating(BigDecimal.ZERO);
        product.setReviewCount(0);

        deliveredOrder = new Order();
        deliveredOrder.setId(100L);
        deliveredOrder.setOrderNumber("HC-ORD-2026-000001");
        deliveredOrder.setCustomerId(5L);
        deliveredOrder.setOrderStatus(OrderStatus.DELIVERED);

        orderItem = new OrderItem(deliveredOrder, 10L, "Nilgiris Raw Honey",
                new BigDecimal("1.00"), new BigDecimal("850.00"), new BigDecimal("850.00"));
        orderItem.setId(50L);
    }

    @Test
    @DisplayName("1. Customer with DELIVERED order item can submit 5-star review")
    void testCreateReviewSuccess() {
        ReviewCreateRequest req = new ReviewCreateRequest(50L, 5, "Amazing honey taste!");

        when(userRepository.findByPhoneNumber("9876543214")).thenReturn(Optional.of(customer));
        when(orderItemRepository.findById(50L)).thenReturn(Optional.of(orderItem));
        when(reviewRepository.existsByCustomerIdAndOrderItemId(5L, 50L)).thenReturn(false);
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> {
            Review r = invocation.getArgument(0);
            r.setId(1L);
            return r;
        });

        // Mock rating aggregation
        when(reviewRepository.getAverageRatingByProductId(10L)).thenReturn(5.0);
        when(reviewRepository.countByProductId(10L)).thenReturn(1L);
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ReviewResponse response = reviewService.createReview("9876543214", req);

        assertNotNull(response);
        assertEquals(5, response.getRating());
        assertEquals("Amazing honey taste!", response.getComment());
        assertEquals("Nilgiris Raw Honey", response.getProductName());

        // Verify product rating updated to 5.00 and count 1
        assertEquals(new BigDecimal("5.00"), product.getRating());
        assertEquals(1, product.getReviewCount());
    }

    @Test
    @DisplayName("2. Review rejected if order is not DELIVERED (e.g. SHIPPED)")
    void testCreateReviewNonDeliveredOrderRejected() {
        deliveredOrder.setOrderStatus(OrderStatus.SHIPPED);
        ReviewCreateRequest req = new ReviewCreateRequest(50L, 5, "Good");

        when(userRepository.findByPhoneNumber("9876543214")).thenReturn(Optional.of(customer));
        when(orderItemRepository.findById(50L)).thenReturn(Optional.of(orderItem));

        assertThrows(BadRequestException.class, () -> reviewService.createReview("9876543214", req));
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    @DisplayName("3. Review rejected if order belongs to another customer")
    void testCreateReviewUnownedOrderRejected() {
        deliveredOrder.setCustomerId(999L); // Other customer
        ReviewCreateRequest req = new ReviewCreateRequest(50L, 5, "Good");

        when(userRepository.findByPhoneNumber("9876543214")).thenReturn(Optional.of(customer));
        when(orderItemRepository.findById(50L)).thenReturn(Optional.of(orderItem));

        assertThrows(UnauthorizedException.class, () -> reviewService.createReview("9876543214", req));
    }

    @Test
    @DisplayName("4. Duplicate review on same order item rejected with ConflictException (409)")
    void testDuplicateReviewRejected() {
        ReviewCreateRequest req = new ReviewCreateRequest(50L, 4, "Second attempt");

        when(userRepository.findByPhoneNumber("9876543214")).thenReturn(Optional.of(customer));
        when(orderItemRepository.findById(50L)).thenReturn(Optional.of(orderItem));
        when(reviewRepository.existsByCustomerIdAndOrderItemId(5L, 50L)).thenReturn(true);

        assertThrows(ConflictException.class, () -> reviewService.createReview("9876543214", req));
    }

    @Test
    @DisplayName("5. Rating below 1 or above 5 rejected")
    void testInvalidRatingRangeRejected() {
        ReviewCreateRequest reqZero = new ReviewCreateRequest(50L, 0, "Too low");
        ReviewCreateRequest reqSix = new ReviewCreateRequest(50L, 6, "Too high");

        when(userRepository.findByPhoneNumber("9876543214")).thenReturn(Optional.of(customer));

        assertThrows(BadRequestException.class, () -> reviewService.createReview("9876543214", reqZero));
        assertThrows(BadRequestException.class, () -> reviewService.createReview("9876543214", reqSix));
    }

    @Test
    @DisplayName("6. Customer can update their own review rating and comment")
    void testUpdateReviewSuccess() {
        Review existing = new Review(customer, product, beekeeper, deliveredOrder, orderItem, 4, "Nice");
        existing.setId(10L);

        ReviewUpdateRequest req = new ReviewUpdateRequest(5, "Updated to 5 stars! Even better after a week.");

        when(userRepository.findByPhoneNumber("9876543214")).thenReturn(Optional.of(customer));
        when(reviewRepository.findByIdAndCustomerId(10L, 5L)).thenReturn(Optional.of(existing));
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Rating aggregation update
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(reviewRepository.getAverageRatingByProductId(10L)).thenReturn(5.0);
        when(reviewRepository.countByProductId(10L)).thenReturn(1L);

        ReviewResponse response = reviewService.updateReview("9876543214", 10L, req);

        assertNotNull(response);
        assertEquals(5, response.getRating());
        assertEquals("Updated to 5 stars! Even better after a week.", response.getComment());
    }

    @Test
    @DisplayName("7. Customer can delete their own review and recalculate product rating")
    void testDeleteReviewSuccess() {
        Review existing = new Review(customer, product, beekeeper, deliveredOrder, orderItem, 5, "To delete");
        existing.setId(10L);

        when(userRepository.findByPhoneNumber("9876543214")).thenReturn(Optional.of(customer));
        when(reviewRepository.findByIdAndCustomerId(10L, 5L)).thenReturn(Optional.of(existing));

        // After deletion, 0 reviews remain
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(reviewRepository.getAverageRatingByProductId(10L)).thenReturn(null);
        when(reviewRepository.countByProductId(10L)).thenReturn(0L);

        reviewService.deleteReview("9876543214", 10L);

        verify(reviewRepository).delete(existing);
        assertEquals(BigDecimal.ZERO, product.getRating());
        assertEquals(0, product.getReviewCount());
    }
}
