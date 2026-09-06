import { useSelector, useDispatch } from 'react-redux'
import { useEffect, useCallback } from 'react'
import {
  fetchBatches,
  fetchBatch,
  createBatch,
  updateBatch,
  sendForTesting,
  fetchBatchStats,
  clearBatchError,
  clearSelectedBatch,
} from '../batchSlice'
import { savePendingBatch } from '../services/offlineBatchStore'

export const useBatches = (autoFetch = false) => {
  const batch = useSelector((state) => state.batch)
  const dispatch = useDispatch()

  useEffect(() => {
    if (autoFetch && batch.batches.length === 0 && !batch.loading) {
      dispatch(fetchBatches({ page: 0, size: 10 }))
      dispatch(fetchBatchStats())
    }
  }, [autoFetch, dispatch, batch.batches.length, batch.loading])

  const loadBatches = useCallback(
    (params = {}) => dispatch(fetchBatches(params)),
    [dispatch]
  )

  const loadBatch = useCallback(
    (batchId) => dispatch(fetchBatch(batchId)),
    [dispatch]
  )

  const addBatch = useCallback(
    (data) => dispatch(createBatch(data)),
    [dispatch]
  )

  /**
   * Offline-aware batch creation.
   * Attempts online upload via Redux thunk. If network connectivity fails,
   * falls back to saving the batch draft locally in IndexedDB.
   * Server validation errors (400/401/403) are NOT saved offline.
   */
  const createBatchOfflineAware = useCallback(
    async (formData, hiveInfo = {}) => {
      try {
        const actionResult = await dispatch(createBatch(formData)).unwrap()
        return { success: true, offline: false, payload: actionResult }
      } catch (errStringOrObj) {
        // Check if error is network failure
        const isNetworkFailure =
          !navigator.onLine ||
          (typeof errStringOrObj === 'string' &&
            (errStringOrObj.toLowerCase().includes('network error') ||
              errStringOrObj.toLowerCase().includes('failed to fetch') ||
              errStringOrObj.toLowerCase().includes('networkerror')))

        if (isNetworkFailure) {
          try {
            // Extract fields from FormData
            let harvestDate = ''
            let hiveId = null
            let quantityKg = ''
            let photoBlob = null

            if (formData instanceof FormData) {
              harvestDate = formData.get('harvestDate')
              hiveId = Number(formData.get('hiveId'))
              quantityKg = formData.get('quantityKg')
              photoBlob = formData.get('photo')
            } else {
              harvestDate = formData.harvestDate
              hiveId = Number(formData.hiveId)
              quantityKg = formData.quantityKg
              photoBlob = formData.photo || null
            }

            // Client validation check before saving offline
            if (!harvestDate || !hiveId || !quantityKg || Number(quantityKg) <= 0) {
              return { success: false, error: 'Please provide valid harvest date, hive, and quantity.' }
            }

            const localId = await savePendingBatch({
              harvestDate,
              hiveId,
              hiveCode: hiveInfo.hiveCode || `Hive #${hiveId}`,
              clusterName: hiveInfo.clusterName || 'Apiary',
              quantityKg,
              photoBlob: photoBlob instanceof Blob ? photoBlob : null,
            })

            return {
              success: true,
              offline: true,
              localId,
              payload: {
                batchId: localId,
                status: 'PENDING',
                quantityKg: Number(quantityKg),
                hiveCode: hiveInfo.hiveCode || `Hive #${hiveId}`,
                harvestDate,
              },
            }
          } catch (storageErr) {
            return {
              success: false,
              error: storageErr.message || 'Failed to save batch offline.',
            }
          }
        }

        // Permanent validation/server error
        return {
          success: false,
          error: typeof errStringOrObj === 'string' ? errStringOrObj : 'Failed to create batch.',
        }
      }
    },
    [dispatch]
  )

  const editBatch = useCallback(
    (batchId, data) => dispatch(updateBatch({ batchId, data })),
    [dispatch]
  )

  const testBatch = useCallback(
    (batchId) => dispatch(sendForTesting(batchId)),
    [dispatch]
  )

  const loadStats = useCallback(
    () => dispatch(fetchBatchStats()),
    [dispatch]
  )

  return {
    ...batch,
    fetchBatches: loadBatches,
    fetchBatch: loadBatch,
    createBatch: addBatch,
    createBatchOfflineAware,
    updateBatch: editBatch,
    sendForTesting: testBatch,
    fetchStats: loadStats,
    clearError: () => dispatch(clearBatchError()),
    clearSelectedBatch: () => dispatch(clearSelectedBatch()),
  }
}

export default useBatches
