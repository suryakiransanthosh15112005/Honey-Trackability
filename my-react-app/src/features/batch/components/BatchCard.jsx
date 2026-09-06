import React from 'react'
import { Link } from 'react-router-dom'
import BatchStatusBadge from './BatchStatusBadge'
import SyncStatusBadge from './SyncStatusBadge'

export const BatchCard = ({ batch }) => {
  const isLocal = Boolean(batch._isLocal || batch.localId)

  return (
    <div
      className={`bg-white rounded-2xl p-5 border ${
        isLocal
          ? 'border-amber-300 bg-amber-50/50 hover:border-amber-400'
          : 'border-slate-200 hover:border-blue-300'
      } transition-all shadow-sm hover:shadow-md flex flex-col justify-between`}
    >
      {/* Top Header */}
      <div>
        <div className="flex items-start justify-between gap-3 mb-3">
          <div className="flex items-center gap-3">
            {batch.photoUrl ? (
              <img
                src={batch.photoUrl}
                alt={batch.batchId || batch.localId}
                className="w-12 h-12 rounded-xl object-cover border border-slate-200"
              />
            ) : (
              <div className="w-12 h-12 rounded-xl bg-amber-50 border border-amber-200 flex items-center justify-center text-2xl">
                🍯
              </div>
            )}
            <div>
              <p
                className={`font-mono font-bold text-sm tracking-wide ${
                  isLocal ? 'text-amber-700' : 'text-blue-600'
                }`}
              >
                {batch.batchId || batch.localId}
              </p>
              <p className="text-slate-800 font-semibold text-xs mt-0.5">
                {batch.hiveCode || 'Hive'}{' '}
                {batch.clusterName ? `· ${batch.clusterName}` : ''}
              </p>
            </div>
          </div>
          {isLocal ? (
            <SyncStatusBadge status={batch.syncStatus} lastError={batch.lastError} />
          ) : (
            <BatchStatusBadge status={batch.status} />
          )}
        </div>

        {/* Metrics Grid */}
        <div className="grid grid-cols-2 gap-2 my-3 p-3 rounded-xl bg-slate-50 border border-slate-100">
          <div>
            <p className="text-[10px] text-slate-500 uppercase font-bold tracking-wider">
              Harvested
            </p>
            <p className="text-slate-800 text-xs font-medium mt-0.5">
              {batch.harvestDate
                ? new Date(batch.harvestDate).toLocaleDateString('en-IN', {
                    day: '2-digit',
                    month: 'short',
                    year: 'numeric',
                  })
                : '—'}
            </p>
          </div>
          <div>
            <p className="text-[10px] text-slate-500 uppercase font-bold tracking-wider">
              Quantity
            </p>
            <p className="text-amber-600 text-xs font-mono font-bold mt-0.5">
              {batch.quantityKg} KG
            </p>
          </div>
        </div>

        {isLocal && batch.lastError && (
          <div className="p-2 mb-2 rounded-lg bg-amber-50 border border-amber-200 text-amber-900 text-xs font-medium">
            ⚠ {batch.lastError}
          </div>
        )}
      </div>

      {/* Action Footer */}
      <div className="pt-3 border-t border-slate-100 flex items-center justify-between">
        <span className="text-[11px] text-slate-500">
          {isLocal ? 'Saved Offline' : 'Registered'}{' '}
          {batch.createdAt
            ? new Date(batch.createdAt).toLocaleDateString('en-IN')
            : '—'}
        </span>
        {!isLocal && (
          <Link to={`/beekeeper/batches/${batch.batchId}`}>
            <button className="px-3.5 py-1.5 rounded-lg border border-blue-200 bg-blue-50 text-blue-600 text-xs font-semibold hover:bg-blue-100 transition-colors flex items-center gap-1">
              View Details →
            </button>
          </Link>
        )}
      </div>
    </div>
  )
}

export default BatchCard
