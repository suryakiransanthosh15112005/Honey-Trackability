import React from 'react'
import { Link } from 'react-router-dom'
import Button from '../../../components/ui/Button'

export const PendingTestCard = ({ batch }) => {
  return (
    <div className="bg-white rounded-2xl p-5 border border-slate-200 hover:border-blue-400 transition-all shadow-sm flex flex-col justify-between">
      <div>
        {/* Header */}
        <div className="flex items-start justify-between gap-3 mb-3">
          <div className="flex items-center gap-3">
            {batch.photoUrl ? (
              <img
                src={batch.photoUrl}
                alt={batch.batchId}
                className="w-12 h-12 rounded-xl object-cover border border-amber-200"
              />
            ) : (
              <div className="w-12 h-12 rounded-xl bg-blue-50 flex items-center justify-center text-2xl border border-blue-200">
                🧪
              </div>
            )}
            <div>
              <p className="text-amber-800 font-mono font-bold text-sm tracking-wide">
                {batch.batchId}
              </p>
              <p className="text-slate-900 font-semibold text-xs mt-0.5">
                {batch.beekeeperName} · <span className="text-slate-500">{batch.village}</span>
              </p>
            </div>
          </div>
          <span className="px-2.5 py-1 rounded-full text-xs font-bold bg-blue-50 border border-blue-200 text-blue-700 flex items-center gap-1">
            <span className="w-1.5 h-1.5 rounded-full bg-blue-600 animate-pulse" />
            Awaiting Test
          </span>
        </div>

        {/* Batch Info Grid */}
        <div className="grid grid-cols-2 gap-2 my-3 p-3 rounded-xl bg-slate-50 border border-slate-200">
          <div>
            <p className="text-[10px] text-slate-500 uppercase font-bold tracking-wider">Hive</p>
            <p className="text-slate-900 text-xs font-medium mt-0.5">
              {batch.hiveCode} <span className="text-slate-500">({batch.clusterName})</span>
            </p>
          </div>
          <div>
            <p className="text-[10px] text-slate-500 uppercase font-bold tracking-wider">Quantity</p>
            <p className="text-amber-800 text-xs font-mono font-bold mt-0.5">
              {batch.quantityKg} KG
            </p>
          </div>
        </div>
      </div>

      {/* Action Footer */}
      <div className="pt-3 border-t border-slate-100 flex items-center justify-between">
        <span className="text-[11px] text-slate-500">
          Submitted {batch.submittedAt ? new Date(batch.submittedAt).toLocaleDateString('en-IN') : '—'}
        </span>
        <Link to={`/lab/tests/${batch.batchId}`}>
          <Button variant="primary" size="sm" className="bg-blue-600 hover:bg-blue-700 border-blue-600 font-bold">
            Open Test →
          </Button>
        </Link>
      </div>
    </div>
  )
}

export default PendingTestCard
