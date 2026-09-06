import axiosInstance from '../../../services/axios'

const reviewApi = {
  // Create a new review for a delivered order item
  createReview: (data) => axiosInstance.post('/reviews', data),

  // Update customer's own review
  updateReview: (reviewId, data) => axiosInstance.put(`/reviews/${reviewId}`, data),

  // Delete customer's own review
  deleteReview: (reviewId) => axiosInstance.delete(`/reviews/${reviewId}`),

  // Get customer's review for a specific order item (may return null body)
  getReviewByOrderItem: (orderItemId) =>
    axiosInstance.get(`/reviews/order-item/${orderItemId}`),

  // Get paginated reviews for a product (PUBLIC)
  getProductReviews: (productId, page = 0, size = 10) =>
    axiosInstance.get(`/products/${productId}/reviews`, { params: { page, size } }),

  // Get current customer's authored reviews
  getMyReviews: (page = 0, size = 10) =>
    axiosInstance.get('/reviews/my', { params: { page, size } }),
}

export default reviewApi
