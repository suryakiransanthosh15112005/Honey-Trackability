import React, { useState } from 'react'
import reviewApi from '../api/reviewApi'
import RatingStars from './RatingStars'

/**
 * ReviewForm — inline form for creating or editing a review.
 *
 * Props:
 *   orderItemId   – required for creation
 *   existingReview – ReviewResponse object if editing
 *   onSuccess     – callback(ReviewResponse) when submitted
 *   onCancel      – callback to close / dismiss
 *   productName   – display name in the form header
 */
const ReviewForm = ({ orderItemId, existingReview, onSuccess, onCancel, productName }) => {
  const [rating, setRating] = useState(existingReview?.rating ?? 0)
  const [comment, setComment] = useState(existingReview?.comment ?? '')
  const [submitting, setSubmitting] = useState(false)
  const [error, setError] = useState(null)

  const isEditing = !!existingReview

  const handleSubmit = async (e) => {
    e.preventDefault()
    if (rating < 1 || rating > 5) {
      setError('Please select a rating between 1 and 5 stars.')
      return
    }

    setSubmitting(true)
    setError(null)

    try {
      let res
      if (isEditing) {
        res = await reviewApi.updateReview(existingReview.id, { rating, comment: comment.trim() || null })
      } else {
        res = await reviewApi.createReview({ orderItemId, rating, comment: comment.trim() || null })
      }
      onSuccess?.(res.data?.data)
    } catch (err) {
      setError(err?.response?.data?.message || 'Failed to submit review. Please try again.')
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <form className="review-form" onSubmit={handleSubmit}>
      <div className="review-form__header">
        <h4 className="review-form__title">
          {isEditing ? '✏️ Edit Your Review' : '⭐ Rate Your Purchase'}
        </h4>
        {productName && (
          <p className="review-form__product">{productName}</p>
        )}
      </div>

      <div className="review-form__stars">
        <label className="form-label">Your Rating</label>
        <div className="mt-2">
          <RatingStars value={rating} onChange={setRating} size="lg" />
        </div>
        {rating > 0 && (
          <p className="review-form__rating-label mt-1">
            {['', '😕 Poor', '😐 Fair', '🙂 Good', '😊 Great', '🤩 Excellent!'][rating]}
          </p>
        )}
      </div>

      <div className="review-form__comment mt-4">
        <label htmlFor="review-comment" className="form-label">
          Your Review <span className="text-secondary">(optional, max 1000 characters)</span>
        </label>
        <textarea
          id="review-comment"
          className="form-input mt-1 resize-y min-h-24"
          placeholder="Share your experience with this honey — taste, aroma, packaging, purity…"
          value={comment}
          onChange={(e) => setComment(e.target.value)}
          maxLength={1000}
        />
        <div className="review-form__char-count text-secondary">
          {comment.length} / 1000
        </div>
      </div>

      {error && (
        <div className="alert alert--danger mt-3">
          <span className="alert__icon">⚠️</span>
          <div className="alert__body">
            <p className="alert__message">{error}</p>
          </div>
        </div>
      )}

      <div className="review-form__actions mt-5 flex gap-3">
        <button
          type="submit"
          className="btn btn--primary btn--sm"
          disabled={submitting || rating === 0}
        >
          {submitting
            ? 'Submitting…'
            : isEditing
            ? 'Update Review'
            : 'Submit Review'}
        </button>
        {onCancel && (
          <button
            type="button"
            className="btn btn--ghost btn--sm"
            onClick={onCancel}
            disabled={submitting}
          >
            Cancel
          </button>
        )}
      </div>
    </form>
  )
}

export default ReviewForm
