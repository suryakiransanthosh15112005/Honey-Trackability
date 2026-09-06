import { useState, useEffect, useCallback } from 'react'
import yieldPredictionApi from '../api/yieldPredictionApi'

export const useYieldPrediction = (hiveId = null) => {
  const [prediction, setPrediction] = useState(null)
  const [allPredictions, setAllPredictions] = useState([])
  const [loading, setLoading] = useState(false)
  const [refreshing, setRefreshing] = useState(false)
  const [error, setError] = useState(null)

  const fetchPrediction = useCallback(async () => {
    if (!hiveId) return
    setLoading(true)
    setError(null)
    try {
      const res = await yieldPredictionApi.getYieldPrediction(hiveId)
      setPrediction(res.data.data)
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to load yield prediction')
    } finally {
      setLoading(false)
    }
  }, [hiveId])

  const fetchAllPredictions = useCallback(async () => {
    setLoading(true)
    setError(null)
    try {
      const res = await yieldPredictionApi.getAllYieldPredictions()
      setAllPredictions(res.data.data || [])
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to load yield predictions')
    } finally {
      setLoading(false)
    }
  }, [])

  const refreshPrediction = async () => {
    if (!hiveId) return
    setRefreshing(true)
    setError(null)
    try {
      const res = await yieldPredictionApi.refreshYieldPrediction(hiveId)
      setPrediction(res.data.data)
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to refresh yield prediction')
    } finally {
      setRefreshing(false)
    }
  }

  useEffect(() => {
    if (hiveId) {
      fetchPrediction()
    }
  }, [hiveId, fetchPrediction])

  return {
    prediction,
    allPredictions,
    loading,
    refreshing,
    error,
    fetchPrediction,
    fetchAllPredictions,
    refreshPrediction,
  }
}

export default useYieldPrediction
