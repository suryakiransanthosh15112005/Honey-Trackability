package com.honeychain.review.service.impl;

import com.honeychain.beekeeper.entity.BeekeeperProfile;
import com.honeychain.common.dto.PageResponse;
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
import com.honeychain.review.service.ReviewService;
import com.honeychain.user.entity.User;
import com.honeychain.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@Transactional
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ReviewMapper reviewMapper;

    public ReviewServiceImpl(ReviewRepository reviewRepository,
                             OrderItemRepository orderItemRepository,
                             ProductRepository productRepository,
                             UserRepository userRepository,
                             ReviewMapper reviewMapper) {
        this.reviewRepository = reviewRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.reviewMapper = reviewMapper;
    }

    @Override
    public ReviewResponse createReview(String customerPhone, ReviewCreateRequest request) {
        User customer = getUser(customerPhone);
        validateRating(request.getRating());

        OrderItem orderItem = orderItemRepository.findById(request.getOrderItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Order item not found: " + request.getOrderItemId()));

        Order order = orderItem.getOrder();
        if (!order.getCustomerId().equals(customer.getId())) {
            throw new UnauthorizedException("You can only submit reviews for items in your own orders.");
        }

        if (order.getOrderStatus() != OrderStatus.DELIVERED) {
            throw new BadRequestException("Reviews can only be submitted for DELIVERED orders. Current status: " + order.getOrderStatus());
        }

        if (reviewRepository.existsByCustomerIdAndOrderItemId(customer.getId(), orderItem.getId())) {
            throw new ConflictException("You have already reviewed this purchased item.");
        }

        Product product = productRepository.findById(orderItem.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found for item ID: " + orderItem.getProductId()));

        BeekeeperProfile beekeeper = product.getBeekeeperProfile();

        String comment = request.getComment() != null ? request.getComment().trim() : null;
        Review review = new Review(
                customer,
                product,
                beekeeper,
                order,
                orderItem,
                request.getRating(),
                comment
        );

        Review savedReview = reviewRepository.save(review);

        // Recalculate and update Product rating
        updateProductRating(product.getId());

        return reviewMapper.toResponse(savedReview);
    }

    @Override
    public ReviewResponse updateReview(String customerPhone, Long reviewId, ReviewUpdateRequest request) {
        User customer = getUser(customerPhone);
        validateRating(request.getRating());

        Review review = reviewRepository.findByIdAndCustomerId(reviewId, customer.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Review not found or you do not have permission to edit it."));

        review.setRating(request.getRating());
        review.setComment(request.getComment() != null ? request.getComment().trim() : null);

        Review savedReview = reviewRepository.save(review);

        // Recalculate product rating
        updateProductRating(review.getProduct().getId());

        return reviewMapper.toResponse(savedReview);
    }

    @Override
    public void deleteReview(String customerPhone, Long reviewId) {
        User customer = getUser(customerPhone);

        Review review = reviewRepository.findByIdAndCustomerId(reviewId, customer.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Review not found or you do not have permission to delete it."));

        Long productId = review.getProduct().getId();
        reviewRepository.delete(review);

        // Recalculate product rating after deletion
        updateProductRating(productId);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ReviewResponse> getProductReviews(Long productId, Pageable pageable) {
        Page<Review> page = reviewRepository.findByProductIdOrderByCreatedAtDesc(productId, pageable);
        List<ReviewResponse> content = page.getContent().stream().map(reviewMapper::toResponse).toList();
        return PageResponse.of(page, content);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ReviewResponse> getMyReviews(String customerPhone, Pageable pageable) {
        User customer = getUser(customerPhone);
        Page<Review> page = reviewRepository.findByCustomerIdOrderByCreatedAtDesc(customer.getId(), pageable);
        List<ReviewResponse> content = page.getContent().stream().map(reviewMapper::toResponse).toList();
        return PageResponse.of(page, content);
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewResponse getReviewByOrderItem(String customerPhone, Long orderItemId) {
        User customer = getUser(customerPhone);
        return reviewRepository.findByCustomerIdAndOrderItemId(customer.getId(), orderItemId)
                .map(reviewMapper::toResponse)
                .orElse(null);
    }

    private void updateProductRating(Long productId) {
        productRepository.findById(productId).ifPresent(product -> {
            Double avg = reviewRepository.getAverageRatingByProductId(productId);
            long count = reviewRepository.countByProductId(productId);

            if (avg != null && count > 0) {
                product.setRating(BigDecimal.valueOf(avg).setScale(2, RoundingMode.HALF_UP));
                product.setReviewCount((int) count);
            } else {
                product.setRating(BigDecimal.ZERO);
                product.setReviewCount(0);
            }

            productRepository.save(product);
        });
    }

    private void validateRating(Integer rating) {
        if (rating == null || rating < 1 || rating > 5) {
            throw new BadRequestException("Rating must be an integer between 1 and 5.");
        }
    }

    private User getUser(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new ResourceNotFoundException("User account not found: " + phoneNumber));
    }
}
