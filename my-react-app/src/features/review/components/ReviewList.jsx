import React from 'react'
import RatingStars from './RatingStars'
import ReviewCard from './ReviewCard'

/**
 * ReviewSummary — shows average rating, star breakdown, and total count.
 * Props:
 *   averageRating  – number
 *   totalReviews   – number
 */
const ReviewSummary = ({ averageRating = 0, totalReviews = 0 }) => {
  if (totalReviews === 0) {
    return (
      <div className="review-summary review-summary--empty">
        <p className="text-secondary text-sm">
          No reviews yet. Be the first to review this product!
        </p>
      </div>
    )
  }

  return (
    <div className="review-summary flex items-center gap-5">
      <div className="review-summary__score text-center">
        <div className="review-summary__avg text-5xl font-black text-amber-500 leading-none">
          {Number(averageRating).toFixed(1)}
        </div>
        <RatingStars value={Math.round(averageRating)} size="md" />
        <div className="text-secondary mt-1 text-xs">
          {totalReviews} review{totalReviews !== 1 ? 's' : ''}
        </div>
      </div>
    </div>
  )
}

/**
 * ReviewList — paginated list of reviews for a product.
 * Props:
 *   reviews        – array of ReviewResponse
 *   loading        – boolean
 *   error          – string|null
 *   page           – current page index (0-based)
 *   totalPages     – total pages from backend
 *   onPageChange   – callback(newPage)
 *   averageRating  – number
 *   totalReviews   – number
 */
const ReviewList = ({
  reviews = [],
  loading = false,
  error = null,
  page = 0,
  totalPages = 0,
  onPageChange,
  averageRating = 0,
  totalReviews = 0,
}) => {
  return (
    <section className="review-list section-card">
      <div className="review-list__header">
        <h3 className="review-list__title">Customer Reviews</h3>
      </div>

      <ReviewSummary averageRating={averageRating} totalReviews={totalReviews} />

      {loading && (
        <div className="review-list__loading mt-4">
          {[...Array(3)].map((_, i) => (
            <div key={i} className="skeleton skeleton--card mb-3 h-24" />
          ))}
        </div>
      )}

      {error && (
        <div className="alert alert--danger mt-4">
          <span className="alert__icon">⚠️</span>
          <div className="alert__body"><p className="alert__message">{error}</p></div>
        </div>
      )}

      {!loading && !error && reviews.length === 0 && totalReviews === 0 && (
        <p className="text-secondary mt-4">No reviews yet for this product.</p>
      )}

      {!loading && !error && reviews.length > 0 && (
        <div className="review-list__cards mt-4 flex flex-col gap-3">
          {reviews.map((review) => (
            <ReviewCard key={review.id} review={review} />
          ))}
        </div>
      )}

      {totalPages > 1 && (
        <div className="review-list__pagination mt-4 flex gap-2 justify-center">
          <button
            className="btn btn--ghost btn--sm"
            disabled={page === 0}
            onClick={() => onPageChange?.(page - 1)}
          >
            ← Prev
          </button>
          <span className="text-secondary self-center text-sm">
            Page {page + 1} of {totalPages}
          </span>
          <button
            className="btn btn--ghost btn--sm"
            disabled={page >= totalPages - 1}
            onClick={() => onPageChange?.(page + 1)}
          >
            Next →
          </button>
        </div>
      )}
    </section>
  )
}

export { ReviewSummary }
export default ReviewList
