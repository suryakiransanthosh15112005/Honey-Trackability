import batchApi from '../api/batchApi'
import {
  getPendingBatches,
  updateBatchRecord,
  SyncStatus,
} from './offlineBatchStore'

const MAX_RETRIES = 3
const RETRY_DELAYS = [5000, 15000, 30000] // 5s, 15s, 30s

let isSyncing = false
let syncListeners = []

export function subscribeSyncStatus(listener) {
  syncListeners.push(listener)
  return () => {
    syncListeners = syncListeners.filter((l) => l !== listener)
  }
}

function notifySyncListeners(event) {
  syncListeners.forEach((l) => l(event))
}

/**
 * Main Sync Queue Processor.
 * Reads PENDING batches from IndexedDB and processes them one by one in FIFO order.
 * Prevents concurrent sync executions using the isSyncing lock flag.
 */
export async function syncQueue() {
  if (isSyncing) {
    console.info('[BatchSyncManager] Sync already in progress. Skipping.')
    return { success: false, reason: 'ALREADY_SYNCING' }
  }

  isSyncing = true
  notifySyncListeners({ status: 'STARTING' })

  let syncedCount = 0
  let failedCount = 0

  try {
    const pendingList = await getPendingBatches()
    if (pendingList.length === 0) {
      console.info('[BatchSyncManager] Queue empty. Nothing to sync.')
      notifySyncListeners({ status: 'IDLE', pendingCount: 0 })
      return { success: true, syncedCount: 0 }
    }

    console.info(`[BatchSyncManager] Starting sync for ${pendingList.length} draft(s)...`)

    for (const batchDraft of pendingList) {
      const { localId, harvestDate, hiveId, quantityKg, photoBlob, retryCount } = batchDraft

      // Mark local status as SYNCING
      await updateBatchRecord(localId, { syncStatus: SyncStatus.SYNCING })
      notifySyncListeners({ status: 'SYNCING_ITEM', localId })

      // Build FormData for multipart request
      const formData = new FormData()
      formData.append('hiveId', hiveId)
      formData.append('harvestDate', harvestDate)
      formData.append('quantityKg', quantityKg)

      if (photoBlob) {
        formData.append('photo', photoBlob, `offline_${localId}.jpg`)
      }

      try {
        // Send request with X-Idempotency-Key header
        const res = await batchApi.createBatch(formData, localId)
        const serverBatch = res.data?.data

        console.info(`[BatchSyncManager] Sync SUCCESS for ${localId} -> ${serverBatch?.batchId}`)

        // Update local record to SYNCED
        await updateBatchRecord(localId, {
          syncStatus: SyncStatus.SYNCED,
          officialBatchId: serverBatch?.batchId,
          serverCreatedAt: serverBatch?.createdAt,
          lastError: null,
        })

        syncedCount++
        notifySyncListeners({ status: 'ITEM_SYNCED', localId, officialBatchId: serverBatch?.batchId })
      } catch (err) {
        const response = err.response
        const isNetworkError = !response || err.code === 'ERR_NETWORK' || err.code === 'ECONNABORTED'

        if (isNetworkError) {
          // Transient network failure -> retry logic
          const newRetryCount = (retryCount || 0) + 1
          console.warn(`[BatchSyncManager] Network error for ${localId}. Retry ${newRetryCount}/${MAX_RETRIES}`)

          if (newRetryCount >= MAX_RETRIES) {
            await updateBatchRecord(localId, {
              syncStatus: SyncStatus.FAILED,
              retryCount: newRetryCount,
              lastError: 'Network unreachable after multiple attempts.',
            })
            failedCount++
          } else {
            await updateBatchRecord(localId, {
              syncStatus: SyncStatus.PENDING,
              retryCount: newRetryCount,
              lastError: `Network error. Will retry shortly (Attempt ${newRetryCount}/${MAX_RETRIES}).`,
            })
            // Wait retry delay before continuing
            const delay = RETRY_DELAYS[Math.min(newRetryCount - 1, RETRY_DELAYS.length - 1)]
            await new Promise((r) => setTimeout(r, delay))
          }
        } else if (response.status === 401) {
          // Auth required -> mark AUTH_REQUIRED and stop queue
          console.warn(`[BatchSyncManager] Auth expired while syncing ${localId}`)
          await updateBatchRecord(localId, {
            syncStatus: SyncStatus.AUTH_REQUIRED,
            lastError: 'Session expired. Please log in again to sync this batch.',
          })
          failedCount++
          break // Stop queue until re-authenticated
        } else {
          // Permanent business/validation failure (400, 403, 409, 422, etc.)
          const errorMsg = response.data?.message || 'Server rejected batch upload.'
          console.error(`[BatchSyncManager] Permanent failure for ${localId}: ${errorMsg}`)

          await updateBatchRecord(localId, {
            syncStatus: SyncStatus.FAILED,
            lastError: errorMsg,
          })
          failedCount++
        }
      }
    }

    notifySyncListeners({ status: 'FINISHED', syncedCount, failedCount })
    return { success: true, syncedCount, failedCount }
  } finally {
    isSyncing = false
  }
}

export function isBatchSyncing() {
  return isSyncing
}

export default {
  syncQueue,
  subscribeSyncStatus,
  isBatchSyncing,
}
