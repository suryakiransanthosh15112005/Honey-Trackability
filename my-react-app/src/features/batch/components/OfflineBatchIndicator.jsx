import React from 'react'
import useNetworkStatus from '../hooks/useNetworkStatus'
import useBatchSync from '../hooks/useBatchSync'

export const OfflineBatchIndicator = () => {
  const { isOnline } = useNetworkStatus()
  const { pendingCount, isSyncing, syncNow } = useBatchSync()

  return (
    <div className="flex items-center gap-3 px-3 py-1.5 rounded-full bg-slate-100 border border-slate-200 text-xs">
      {/* Network Status Badge */}
      {isOnline ? (
        <span className="flex items-center gap-1 text-blue-700 font-semibold">
          <span className="w-2 h-2 rounded-full bg-blue-600 animate-ping inline-block" />
          🟢 Online
        </span>
      ) : (
        <span className="flex items-center gap-1 text-slate-600 font-semibold">
          <span className="w-2 h-2 rounded-full bg-slate-400 inline-block" />
          🔴 Offline
        </span>
      )}

      {/* Sync Queue Counter & Manual Sync Trigger */}
      {pendingCount > 0 && (
        <div className="flex items-center gap-2 pl-2 border-l border-slate-300">
          {isSyncing ? (
            <span className="text-blue-600 font-medium flex items-center gap-1">
              🔄 Syncing ({pendingCount})...
            </span>
          ) : (
            <span className="text-amber-700 font-medium">
              🔴 {pendingCount} pending draft{pendingCount > 1 ? 's' : ''}
            </span>
          )}

          {isOnline && !isSyncing && (
            <button
              type="button"
              className="px-2 py-0.5 rounded bg-blue-600 text-white font-semibold text-xs hover:bg-blue-700 transition-colors"
              onClick={syncNow}
            >
              Sync Now ⚡
            </button>
          )}
        </div>
      )}
    </div>
  )
}

export default OfflineBatchIndicator
