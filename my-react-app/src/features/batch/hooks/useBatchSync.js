import { useState, useEffect, useCallback } from 'react'
import { getPendingCount, getPendingBatches } from '../services/offlineBatchStore'
import batchSyncManager, { subscribeSyncStatus } from '../services/batchSyncManager'
import useNetworkStatus from './useNetworkStatus'

/**
 * useBatchSync hook
 * Exposes pending count, local drafts list, syncing status, auto-sync triggers on network return and app startup.
 */
export function useBatchSync() {
  const { isOnline } = useNetworkStatus()
  const [pendingCount, setPendingCount] = useState(0)
  const [localDrafts, setLocalDrafts] = useState([])
  const [isSyncing, setIsSyncing] = useState(batchSyncManager.isBatchSyncing())
  const [lastSyncAt, setLastSyncAt] = useState(null)

  const refreshPendingData = useCallback(async () => {
    try {
      const [count, drafts] = await Promise.all([
        getPendingCount(),
        getPendingBatches(),
      ])
      setPendingCount(count || 0)
      setLocalDrafts(Array.isArray(drafts) ? drafts : [])
    } catch (err) {
      console.error('[useBatchSync] Failed to read pending drafts:', err)
      setLocalDrafts([])
    }
  }, [])

  const syncNow = useCallback(async () => {
    if (!isOnline) return
    setIsSyncing(true)
    try {
      await batchSyncManager.syncQueue()
      setLastSyncAt(new Date())
    } finally {
      setIsSyncing(false)
      await refreshPendingData()
    }
  }, [isOnline, refreshPendingData])

  // Initial load
  useEffect(() => {
    refreshPendingData()
  }, [refreshPendingData])

  // Listen to sync manager events
  useEffect(() => {
    const unsubscribe = subscribeSyncStatus((event) => {
      if (event.status === 'STARTING') {
        setIsSyncing(true)
      } else if (event.status === 'FINISHED') {
        setIsSyncing(false)
        setLastSyncAt(new Date())
        refreshPendingData()
      } else if (event.status === 'ITEM_SYNCED') {
        refreshPendingData()
      }
    })
    return unsubscribe
  }, [refreshPendingData])

  // Auto-sync when coming back online
  useEffect(() => {
    if (isOnline && pendingCount > 0 && !isSyncing) {
      const timer = setTimeout(() => {
        syncNow()
      }, 1500) // Brief delay to let connection stabilize
      return () => clearTimeout(timer)
    }
  }, [isOnline, pendingCount, isSyncing, syncNow])

  return {
    pendingCount,
    localDrafts,
    isSyncing,
    lastSyncAt,
    syncNow,
    refreshPendingCount: refreshPendingData,
  }
}

export default useBatchSync
