import React, { useState, useEffect } from 'react'
import CustomerLayout from '../../../layouts/CustomerLayout'
import reviewApi from '../api/reviewApi'
import ReviewCard from '../components/ReviewCard'
import ReviewForm from '../components/ReviewForm'

const MyReviewsPage = () => {
  const [reviews, setReviews] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  const [editingReview, setEditingReview] = useState(null)
  const [deletingId, setDeletingId] = useState(null)
  const [deleteError, setDeleteError] = useState(null)

  const loadReviews = async (p = 0) => {
    setLoading(true)
    setError(null)
    try {
      const res = await reviewApi.getMyReviews(p, 10)
      const data = res.data?.data
      setReviews(data?.content || [])
      setTotalPages(data?.totalPages ?? 0)
      setPage(p)
    } catch (err) {
      setError(err?.response?.data?.message || 'Failed to load reviews')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadReviews(0)
  }, [])

  const handleDelete = async (review) => {
    if (!window.confirm('Are you sure you want to delete this review?')) return
    setDeletingId(review.id)
    setDeleteError(null)
    try {
      await reviewApi.deleteReview(review.id)
      loadReviews(page)
    } catch (err) {
      setDeleteError(err?.response?.data?.message || 'Failed to delete review')
    } finally {
      setDeletingId(null)
    }
  }

  const handleEditSuccess = () => {
    setEditingReview(null)
    loadReviews(page)
  }

  return (
    <CustomerLayout>
      <div className="container section">
        <div className="dashboard__header mb-6">
          <div>
            <h1 className="dashboard__title">⭐ My Reviews</h1>
            <p className="text-secondary mt-1">All honey products you have reviewed</p>
          </div>
        </div>

        {loading && (
          <div className="flex flex-col gap-3">
            {[...Array(3)].map((_, i) => (
              <div key={i} className="skeleton skeleton--card h-28" />
            ))}
          </div>
        )}

        {error && (
          <div className="alert alert--danger">
            <span className="alert__icon">⚠️</span>
            <div className="alert__body"><p className="alert__message">{error}</p></div>
          </div>
        )}

        {deleteError && (
          <div className="alert alert--danger mb-4">
            <span className="alert__icon">⚠️</span>
            <div className="alert__body"><p className="alert__message">{deleteError}</p></div>
          </div>
        )}

        {!loading && reviews.length === 0 && (
          <div className="card text-center py-10">
            <div className="text-5xl">⭐</div>
            <h3 className="mt-3">No reviews yet</h3>
            <p className="text-secondary mt-2">
              Purchase honey and leave a review after delivery to help build trust in the marketplace.
            </p>
          </div>
        )}

        {editingReview && (
          <div className="card mb-6">
            <ReviewForm
              existingReview={editingReview}
              productName={editingReview.productName}
              onSuccess={handleEditSuccess}
              onCancel={() => setEditingReview(null)}
            />
          </div>
        )}

        {!loading && reviews.length > 0 && (
          <div className="flex flex-col gap-4">
            {reviews.map((review) => (
              <ReviewCard
                key={review.id}
                review={review}
                isOwn={true}
                onEdit={(r) => setEditingReview(r)}
                onDelete={(r) => handleDelete(r)}
              />
            ))}
          </div>
        )}

        {totalPages > 1 && (
          <div className="flex gap-2 justify-center mt-6">
            <button
              className="btn btn--ghost btn--sm"
              disabled={page === 0}
              onClick={() => loadReviews(page - 1)}
            >
              ← Prev
            </button>
            <span className="text-secondary self-center text-sm">
              Page {page + 1} of {totalPages}
            </span>
            <button
              className="btn btn--ghost btn--sm"
              disabled={page >= totalPages - 1}
              onClick={() => loadReviews(page + 1)}
            >
              Next →
            </button>
          </div>
        )}
      </div>
    </CustomerLayout>
  )
}

export default MyReviewsPage
