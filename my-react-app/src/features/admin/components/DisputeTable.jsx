import React from 'react'
import { useLanguage } from '../../../i18n/LanguageContext'

export const DisputeTable = ({ disputes = [], onSelectDispute }) => {
  const { t } = useLanguage()

  if (disputes.length === 0) {
    return (
      <div className="bg-white rounded-2xl border border-slate-200/80 p-8 text-center shadow-xs">
        <span className="text-3xl block mb-2">⚖️</span>
        <p className="text-slate-500 text-sm font-medium">{t('empty.noData', 'No customer disputes found.')}</p>
      </div>
    )
  }

  const getStatusBadge = (status) => {
    switch (status) {
      case 'OPEN':
        return (
          <span className="inline-flex items-center gap-1 px-2.5 py-1 rounded-full text-xs font-bold bg-red-50 text-red-700 border border-red-200">
            🚨 OPEN
          </span>
        )
      case 'INVESTIGATING':
        return (
          <span className="inline-flex items-center gap-1 px-2.5 py-1 rounded-full text-xs font-bold bg-amber-50 text-amber-800 border border-amber-300">
            🔍 INVESTIGATING
          </span>
        )
      case 'RESOLVED':
        return (
          <span className="inline-flex items-center gap-1 px-2.5 py-1 rounded-full text-xs font-bold bg-emerald-50 text-emerald-700 border border-emerald-200">
            ✓ RESOLVED
          </span>
        )
      case 'REJECTED':
        return (
          <span className="inline-flex items-center gap-1 px-2.5 py-1 rounded-full text-xs font-bold bg-slate-100 text-slate-700 border border-slate-200">
            ✕ REJECTED
          </span>
        )
      default:
        return (
          <span className="inline-flex items-center gap-1 px-2.5 py-1 rounded-full text-xs font-bold bg-slate-100 text-slate-700 border border-slate-200">
            {status}
          </span>
        )
    }
  }

  return (
    <div className="overflow-x-auto bg-white rounded-2xl border border-slate-200/80 shadow-xs">
      <table className="w-full text-left border-collapse text-xs sm:text-sm">
        <thead>
          <tr className="bg-slate-50/90 border-b border-slate-200 text-[11px] font-bold text-slate-600 uppercase tracking-wider">
            <th className="py-3.5 px-4">ID</th>
            <th className="py-3.5 px-4">{t('batch.batchId', 'Batch ID')}</th>
            <th className="py-3.5 px-4">Order Ref</th>
            <th className="py-3.5 px-4">Reason & Details</th>
            <th className="py-3.5 px-4">{t('common.status', 'Status')}</th>
            <th className="py-3.5 px-4">{t('common.date', 'Submitted')}</th>
            <th className="py-3.5 px-4 text-right">{t('common.actions', 'Actions')}</th>
          </tr>
        </thead>
        <tbody className="divide-y divide-slate-100">
          {disputes.map((d) => (
            <tr key={d.id} className="hover:bg-slate-50/70 transition-colors">
              <td className="py-3.5 px-4 font-mono font-bold text-slate-900">#{d.id}</td>
              <td className="py-3.5 px-4 font-mono">
                <code className="px-2 py-0.5 rounded bg-slate-100 border border-slate-200 text-xs">
                  {d.batchId}
                </code>
              </td>
              <td className="py-3.5 px-4 font-mono text-slate-700">{d.orderNumber || 'N/A'}</td>
              <td className="py-3.5 px-4">
                <strong className="block text-slate-900 font-bold">{d.reason}</strong>
                {d.description && (
                  <span className="text-slate-500 text-xs truncate max-w-xs block">
                    {d.description}
                  </span>
                )}
              </td>
              <td className="py-3.5 px-4">{getStatusBadge(d.status)}</td>
              <td className="py-3.5 px-4 text-slate-500 font-mono text-xs">
                {d.createdAt ? new Date(d.createdAt).toLocaleDateString('en-IN') : 'N/A'}
              </td>
              <td className="py-3.5 px-4 text-right">
                <button
                  type="button"
                  className="px-3 py-1.5 rounded-lg bg-amber-500 hover:bg-amber-600 text-slate-950 text-xs font-bold transition-colors cursor-pointer shadow-2xs"
                  onClick={() => onSelectDispute?.(d)}
                >
                  {t('admin.manageDispute', 'Manage Dispute')}
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}

export default DisputeTable
