package com.honeychain.review.mapper;

import com.honeychain.review.dto.ReviewResponse;
import com.honeychain.review.entity.Review;
import org.springframework.stereotype.Component;

@Component
public class ReviewMapper {

    public ReviewResponse toResponse(Review review) {
        if (review == null) return null;

        String displayName = "Verified Buyer";
        if (review.getOrder() != null && review.getOrder().getDeliveryAddress() != null &&
            review.getOrder().getDeliveryAddress().getName() != null &&
            !review.getOrder().getDeliveryAddress().getName().isBlank()) {
            displayName = review.getOrder().getDeliveryAddress().getName();
        } else if (review.getCustomer() != null && review.getCustomer().getPhoneNumber() != null) {
            String phone = review.getCustomer().getPhoneNumber();
            if (phone.length() >= 4) {
                displayName = "Customer (..." + phone.substring(phone.length() - 4) + ")";
            }
        }

        return new ReviewResponse(
                review.getId(),
                review.getRating(),
                review.getComment(),
                displayName,
                review.getProduct() != null ? review.getProduct().getId() : null,
                review.getProduct() != null ? review.getProduct().getProductName() : null,
                review.getOrder() != null ? review.getOrder().getOrderNumber() : null,
                review.getOrderItem() != null ? review.getOrderItem().getId() : null,
                review.getCreatedAt()
        );
    }
}
