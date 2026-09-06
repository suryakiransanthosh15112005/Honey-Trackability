import React from 'react'
import RatingStars from './RatingStars'

/**
 * ReviewCard — displays a single review with rating, display name, date, comment.
 *
 * Props:
 *   review         – ReviewResponse object from backend
 *   isOwn          – boolean, if true show Edit / Delete actions
 *   onEdit         – callback to open edit form
 *   onDelete       – callback to trigger deletion
 */
const ReviewCard = ({ review, isOwn = false, onEdit, onDelete }) => {
  const formattedDate = review.createdAt
    ? new Date(review.createdAt).toLocaleDateString('en-IN', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
      })
    : ''

  return (
    <article className="review-card card">
      <div className="review-card__header flex justify-between items-start">
        <div className="flex items-center gap-3">
          <div className="review-card__avatar">
            {review.displayName?.charAt(0).toUpperCase() || 'V'}
          </div>
          <div>
            <p className="review-card__name font-semibold">{review.displayName || 'Verified Buyer'}</p>
            <p className="review-card__date text-secondary text-xs">
              {formattedDate}
            </p>
          </div>
        </div>
        <RatingStars value={review.rating} size="sm" />
      </div>

      {review.comment && (
        <p className="review-card__comment mt-3">{review.comment}</p>
      )}

      {review.productName && (
        <p className="review-card__product mt-2 text-secondary text-xs">
          Product: {review.productName}
        </p>
      )}

      {isOwn && (
        <div className="review-card__actions mt-3 flex gap-2">
          <button
            type="button"
            className="btn btn--outline btn--xs"
            onClick={() => onEdit?.(review)}
          >
            ✏️ Edit
          </button>
          <button
            type="button"
            className="btn btn--danger-outline btn--xs"
            onClick={() => onDelete?.(review)}
          >
            🗑 Delete
          </button>
        </div>
      )}
    </article>
  )
}

export default ReviewCard
