// ====================================================================
// HONEYCHAIN — useProducts Hook
// Fetches public marketplace products with filters, search, sort, pagination.
// ====================================================================

import { useState, useEffect, useCallback, useRef } from 'react'
import productApi from '../api/productApi'

const DEFAULT_FILTERS = {
  search: '',
  region: '',
  flowerSource: '',
  minPrice: '',
  maxPrice: '',
  sortBy: 'newest',
  page: 0,
  size: 12,
}

export function useProducts(initialFilters = {}) {
  const [filters, setFilters] = useState({ ...DEFAULT_FILTERS, ...initialFilters })
  const [products, setProducts] = useState([])
  const [totalElements, setTotalElements] = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)

  // Debounce search input
  const searchTimer = useRef(null)

  const fetchProducts = useCallback(async (f) => {
    setLoading(true)
    setError(null)
    try {
      const params = {}
      if (f.search)      params.search      = f.search
      if (f.region)      params.region      = f.region
      if (f.flowerSource) params.flowerSource = f.flowerSource
      if (f.minPrice)    params.minPrice    = f.minPrice
      if (f.maxPrice)    params.maxPrice    = f.maxPrice
      if (f.sortBy)      params.sortBy      = f.sortBy
      params.page = f.page ?? 0
      params.size = f.size ?? 12

      const res = await productApi.getProducts(params)
      const data = res.data?.data
      if (data?.content !== undefined) {
        // Paginated response
        setProducts(data.content)
        setTotalElements(data.totalElements ?? 0)
        setTotalPages(data.totalPages ?? 1)
      } else if (Array.isArray(data)) {
        setProducts(data)
        setTotalElements(data.length)
        setTotalPages(1)
      } else {
        setProducts([])
        setTotalElements(0)
        setTotalPages(1)
      }
    } catch (err) {
      setError(err?.response?.data?.message || 'Failed to load products')
      setProducts([])
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    fetchProducts(filters)
  }, [filters, fetchProducts])

  const updateFilter = useCallback((key, value) => {
    setFilters((prev) => ({ ...prev, [key]: value, page: 0 }))
  }, [])

  const updateSearch = useCallback((value) => {
    clearTimeout(searchTimer.current)
    searchTimer.current = setTimeout(() => {
      setFilters((prev) => ({ ...prev, search: value, page: 0 }))
    }, 400)
  }, [])

  const goToPage = useCallback((page) => {
    setFilters((prev) => ({ ...prev, page }))
  }, [])

  const resetFilters = useCallback(() => {
    setFilters({ ...DEFAULT_FILTERS })
  }, [])

  const refresh = useCallback(() => {
    fetchProducts(filters)
  }, [filters, fetchProducts])

  return {
    products,
    filters,
    totalElements,
    totalPages,
    loading,
    error,
    updateFilter,
    updateSearch,
    goToPage,
    resetFilters,
    refresh,
  }
}

export default useProducts
