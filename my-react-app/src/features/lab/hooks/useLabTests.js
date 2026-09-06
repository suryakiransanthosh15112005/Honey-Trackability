import { useSelector, useDispatch } from 'react-redux'
import { useCallback, useEffect } from 'react'
import {
  fetchPendingTests,
  fetchLabTest,
  submitLabTest,
  fetchLabStats,
  clearLabError,
  clearSelectedTest,
} from '../labSlice'

export const useLabTests = (autoFetchPending = false) => {
  const lab = useSelector((state) => state.lab)
  const dispatch = useDispatch()

  useEffect(() => {
    if (autoFetchPending) {
      dispatch(fetchPendingTests())
      dispatch(fetchLabStats())
    }
  }, [autoFetchPending, dispatch])

  const loadPending = useCallback(() => dispatch(fetchPendingTests()), [dispatch])
  const loadTest = useCallback((batchId) => dispatch(fetchLabTest(batchId)), [dispatch])
  const postTest = useCallback(
    (batchId, data) => dispatch(submitLabTest({ batchId, data })),
    [dispatch]
  )
  const loadStats = useCallback(() => dispatch(fetchLabStats()), [dispatch])

  return {
    ...lab,
    fetchPendingTests: loadPending,
    fetchLabTest: loadTest,
    submitLabTest: postTest,
    fetchLabStats: loadStats,
    clearError: () => dispatch(clearLabError()),
    clearSelectedTest: () => dispatch(clearSelectedTest()),
  }
}

export default useLabTests
