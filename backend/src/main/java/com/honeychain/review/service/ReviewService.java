package com.honeychain.review.service;

import com.honeychain.common.dto.PageResponse;
import com.honeychain.review.dto.ReviewCreateRequest;
import com.honeychain.review.dto.ReviewResponse;
import com.honeychain.review.dto.ReviewUpdateRequest;
import org.springframework.data.domain.Pageable;

public interface ReviewService {

    ReviewResponse createReview(String customerPhone, ReviewCreateRequest request);

    ReviewResponse updateReview(String customerPhone, Long reviewId, ReviewUpdateRequest request);

    void deleteReview(String customerPhone, Long reviewId);

    PageResponse<ReviewResponse> getProductReviews(Long productId, Pageable pageable);

    PageResponse<ReviewResponse> getMyReviews(String customerPhone, Pageable pageable);

    ReviewResponse getReviewByOrderItem(String customerPhone, Long orderItemId);
}
