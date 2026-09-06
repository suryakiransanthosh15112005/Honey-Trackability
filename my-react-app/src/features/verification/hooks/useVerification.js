import { useState, useEffect, useCallback } from 'react'
import verificationApi from '../api/verificationApi'

export const useVerification = (batchId) => {
  const [data, setData] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  const verify = useCallback(async (idToVerify) => {
    const target = idToVerify || batchId
    if (!target) return

    try {
      setLoading(true)
      setError(null)
      // Call scan endpoint to record the QR verification scan event with anti-counterfeit heuristics
      const res = await verificationApi.scanBatch(target)
      setData(res.data.data)
    } catch (err) {
      setError(
        err.response?.data?.message || 'Unable to verify this batch right now. Please try again.'
      )
    } finally {
      setLoading(false)
    }
  }, [batchId])

  useEffect(() => {
    if (batchId) {
      verify(batchId)
    }
  }, [batchId, verify])

  return {
    verification: data,
    loading,
    error,
    refetch: () => verify(batchId),
  }
}

export default useVerification
