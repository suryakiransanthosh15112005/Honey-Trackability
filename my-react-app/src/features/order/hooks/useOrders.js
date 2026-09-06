import { useState, useEffect, useCallback } from 'react'
import orderApi from '../api/orderApi'

export const useOrders = (page = 0, size = 10) => {
  const [orders, setOrders] = useState([])
  const [totalElements, setTotalElements] = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [cancellingId, setCancellingId] = useState(null)

  const fetchOrders = useCallback(async () => {
    setLoading(true)
    setError(null)
    try {
      const res = await orderApi.getMyOrders({ page, size })
      const data = res.data?.data
      if (data?.content) {
        setOrders(data.content)
        setTotalElements(data.totalElements || 0)
        setTotalPages(data.totalPages || 1)
      } else if (Array.isArray(data)) {
        setOrders(data)
        setTotalElements(data.length)
        setTotalPages(1)
      }
    } catch (err) {
      setError(err?.response?.data?.message || 'Failed to load order history')
    } finally {
      setLoading(false)
    }
  }, [page, size])

  useEffect(() => {
    fetchOrders()
  }, [fetchOrders])

  const cancelOrder = async (orderNumber) => {
    setCancellingId(orderNumber)
    try {
      await orderApi.cancelOrder(orderNumber)
      await fetchOrders()
    } catch (err) {
      alert(err?.response?.data?.message || 'Failed to cancel order')
    } finally {
      setCancellingId(null)
    }
  }

  return {
    orders,
    totalElements,
    totalPages,
    loading,
    error,
    cancellingId,
    cancelOrder,
    reload: fetchOrders,
  }
}

export default useOrders
