import React from 'react'
import { SyncStatus } from '../services/offlineBatchStore'

export const SyncStatusBadge = ({ status, lastError }) => {
  switch (status) {
    case SyncStatus.PENDING:
      return (
        <span className="badge badge--warning flex items-center gap-1" title="Waiting for internet to sync">
          🟡 PENDING SYNC
        </span>
      )
    case SyncStatus.SYNCING:
      return (
        <span className="badge badge--info flex items-center gap-1 animate-pulse" title="Uploading to server...">
          🔄 SYNCING
        </span>
      )
    case SyncStatus.SYNCED:
      return (
        <span className="badge badge--success flex items-center gap-1" title="Synchronized with HoneyChain server">
          ✅ SYNCED
        </span>
      )
    case SyncStatus.FAILED:
      return (
        <span className="badge badge--danger flex items-center gap-1" title={lastError || 'Sync failed'}>
          ⚠ SYNC FAILED
        </span>
      )
    case SyncStatus.AUTH_REQUIRED:
      return (
        <span className="badge badge--warning flex items-center gap-1" title="Session expired. Log in again.">
          🔐 AUTH REQUIRED
        </span>
      )
    default:
      return <span className="badge badge--dark">{status}</span>
  }
}

export default SyncStatusBadge
