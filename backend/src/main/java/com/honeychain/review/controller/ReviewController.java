package com.honeychain.review.controller;

import com.honeychain.common.dto.ApiResponse;
import com.honeychain.common.dto.PageResponse;
import com.honeychain.review.dto.ReviewCreateRequest;
import com.honeychain.review.dto.ReviewResponse;
import com.honeychain.review.dto.ReviewUpdateRequest;
import com.honeychain.review.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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

@RestController
@Tag(name = "Ratings & Reviews", description = "Customer review creation, editing, and public review retrieval endpoints")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/api/reviews")
    @Operation(summary = "Submit a rating and review for a delivered purchased order item (CUSTOMER role)")
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @Valid @RequestBody ReviewCreateRequest request,
            Authentication auth) {
        ReviewResponse response = reviewService.createReview(auth.getName(), request);
        return ResponseEntity.ok(ApiResponse.success("Review submitted successfully", response));
    }

    @GetMapping("/api/products/{productId}/reviews")
    @Operation(summary = "Get paginated reviews for a product (Public endpoint)")
    public ResponseEntity<ApiResponse<PageResponse<ReviewResponse>>> getProductReviews(
            @PathVariable Long productId,
            @PageableDefault(size = 10) Pageable pageable) {
        PageResponse<ReviewResponse> response = reviewService.getProductReviews(productId, pageable);
        return ResponseEntity.ok(ApiResponse.success("Product reviews retrieved", response));
    }

    @GetMapping("/api/reviews/my")
    @Operation(summary = "Get current customer's authored reviews (CUSTOMER role)")
    public ResponseEntity<ApiResponse<PageResponse<ReviewResponse>>> getMyReviews(
            @PageableDefault(size = 10) Pageable pageable,
            Authentication auth) {
        PageResponse<ReviewResponse> response = reviewService.getMyReviews(auth.getName(), pageable);
        return ResponseEntity.ok(ApiResponse.success("My reviews retrieved", response));
    }

    @GetMapping("/api/reviews/order-item/{orderItemId}")
    @Operation(summary = "Get customer's existing review for a specific order item if present")
    public ResponseEntity<ApiResponse<ReviewResponse>> getReviewByOrderItem(
            @PathVariable Long orderItemId,
            Authentication auth) {
        ReviewResponse response = reviewService.getReviewByOrderItem(auth.getName(), orderItemId);
        return ResponseEntity.ok(ApiResponse.success("Review status retrieved", response));
    }

    @PutMapping("/api/reviews/{id}")
    @Operation(summary = "Update an existing review authored by the customer (CUSTOMER role)")
    public ResponseEntity<ApiResponse<ReviewResponse>> updateReview(
            @PathVariable Long id,
            @Valid @RequestBody ReviewUpdateRequest request,
            Authentication auth) {
        ReviewResponse response = reviewService.updateReview(auth.getName(), id, request);
        return ResponseEntity.ok(ApiResponse.success("Review updated successfully", response));
    }

    @DeleteMapping("/api/reviews/{id}")
    @Operation(summary = "Delete an existing review authored by the customer (CUSTOMER role)")
    public ResponseEntity<ApiResponse<Void>> deleteReview(
            @PathVariable Long id,
            Authentication auth) {
        reviewService.deleteReview(auth.getName(), id);
        return ResponseEntity.ok(ApiResponse.success("Review deleted successfully", null));
    }
}
