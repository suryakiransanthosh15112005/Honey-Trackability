import { useState, useEffect, useCallback, useRef } from 'react'
import iotApi from '../api/iotApi'

/**
 * Hook for fetching and auto-refreshing hive health data.
 * @param {string|null} hiveId - If provided, fetches single hive health; else fetches all.
 * @param {number} refreshInterval - Auto-refresh interval in ms (0 = disabled).
 */
export const useHiveHealth = (hiveId = null, refreshInterval = 0) => {
  const [data, setData] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const intervalRef = useRef(null)

  const fetchHealth = useCallback(async () => {
    try {
      setError(null)
      const res = hiveId
        ? await iotApi.getHiveHealth(hiveId)
        : await iotApi.getAllHivesHealth()
      setData(res.data.data)
    } catch (err) {
      setError(
        err.response?.data?.message ||
        'Unable to fetch hive health data. Please try again.'
      )
    } finally {
      setLoading(false)
    }
  }, [hiveId])

  useEffect(() => {
    setLoading(true)
    fetchHealth()

    if (refreshInterval > 0) {
      intervalRef.current = setInterval(fetchHealth, refreshInterval)
    }

    return () => {
      if (intervalRef.current) clearInterval(intervalRef.current)
    }
  }, [fetchHealth, refreshInterval])

  return {
    data,
    loading,
    error,
    refresh: fetchHealth,
  }
}

export default useHiveHealth
