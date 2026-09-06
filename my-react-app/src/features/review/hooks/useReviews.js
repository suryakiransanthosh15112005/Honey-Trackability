import { useState, useEffect, useCallback } from 'react'
import reviewApi from '../api/reviewApi'

/**
 * useReviews — fetches paginated product reviews.
 * Returns { reviews, loading, error, page, totalPages, totalElements, averageRating, totalReviews, setPage, refresh }
 */
const useReviews = (productId, pageSize = 10) => {
  const [reviews, setReviews] = useState([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  const [totalElements, setTotalElements] = useState(0)
  const [refreshKey, setRefreshKey] = useState(0)

  const refresh = useCallback(() => {
    setRefreshKey((k) => k + 1)
    setPage(0)
  }, [])

  useEffect(() => {
    if (!productId) return

    let cancelled = false
    const load = async () => {
      setLoading(true)
      setError(null)
      try {
        const res = await reviewApi.getProductReviews(productId, page, pageSize)
        const data = res.data?.data
        if (!cancelled) {
          setReviews(data?.content || [])
          setTotalPages(data?.totalPages ?? 0)
          setTotalElements(data?.totalElements ?? 0)
        }
      } catch (err) {
        if (!cancelled) {
          setError(err?.response?.data?.message || 'Failed to load reviews')
        }
      } finally {
        if (!cancelled) setLoading(false)
      }
    }

    load()
    return () => { cancelled = true }
  }, [productId, page, pageSize, refreshKey])

  return {
    reviews,
    loading,
    error,
    page,
    totalPages,
    totalElements,
    setPage,
    refresh,
  }
}

export default useReviews
