import React from 'react'
import { Link } from 'react-router-dom'
import { useLanguage } from '../../../i18n/LanguageContext'

export const BatchTable = ({ batches = [] }) => {
  const { t } = useLanguage()

  if (batches.length === 0) {
    return (
      <div className="bg-white rounded-2xl border border-slate-200/80 p-8 text-center shadow-xs">
        <span className="text-3xl block mb-2">🍯</span>
        <p className="text-slate-500 text-sm font-medium">{t('empty.noBatches', 'No honey batches found.')}</p>
      </div>
    )
  }

  const getStatusBadge = (status) => {
    switch (status) {
      case 'PURE':
      case 'IN_STOCK':
      case 'QR_GENERATED':
        return (
          <span className="inline-flex items-center gap-1 px-2.5 py-1 rounded-full text-xs font-bold bg-emerald-50 text-emerald-700 border border-emerald-200">
            ✓ {t(`batch.status${status}`, status)}
          </span>
        )
      case 'FAILED':
        return (
          <span className="inline-flex items-center gap-1 px-2.5 py-1 rounded-full text-xs font-bold bg-red-50 text-red-700 border border-red-200">
            ✕ {t('lab.failed', 'FAILED')}
          </span>
        )
      case 'UNDER_REVIEW':
      case 'SENT_FOR_TESTING':
        return (
          <span className="inline-flex items-center gap-1 px-2.5 py-1 rounded-full text-xs font-bold bg-amber-50 text-amber-800 border border-amber-300">
            ⏳ {t(`batch.status${status}`, status)}
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

  const getRiskBadge = (risk) => {
    switch (risk) {
      case 'HIGH_RISK':
        return (
          <span className="inline-flex items-center gap-1 px-2.5 py-1 rounded-full text-xs font-extrabold bg-red-100 text-red-800 border border-red-300">
            🚨 {t('verification.riskHigh', 'HIGH RISK')}
          </span>
        )
      case 'WATCH':
        return (
          <span className="inline-flex items-center gap-1 px-2.5 py-1 rounded-full text-xs font-bold bg-amber-50 text-amber-800 border border-amber-300">
            ⚠️ {t('verification.riskWatch', 'WATCH')}
          </span>
        )
      case 'NORMAL':
      default:
        return (
          <span className="inline-flex items-center gap-1 px-2.5 py-1 rounded-full text-xs font-bold bg-emerald-50 text-emerald-700 border border-emerald-200">
            🛡️ {t('verification.riskNormal', 'NORMAL')}
          </span>
        )
    }
  }

  return (
    <div className="overflow-x-auto bg-white rounded-2xl border border-slate-200/80 shadow-xs">
      <table className="w-full text-left border-collapse text-xs sm:text-sm">
        <thead>
          <tr className="bg-slate-50/90 border-b border-slate-200 text-[11px] font-bold text-slate-600 uppercase tracking-wider">
            <th className="py-3.5 px-4">{t('batch.batchId', 'Batch ID')}</th>
            <th className="py-3.5 px-4">{t('auth.beekeeperRole', 'Beekeeper')}</th>
            <th className="py-3.5 px-4">{t('hive.hiveCode', 'Hive')}</th>
            <th className="py-3.5 px-4">{t('batch.quantityKg', 'Quantity')}</th>
            <th className="py-3.5 px-4">{t('common.status', 'Status')}</th>
            <th className="py-3.5 px-4">{t('lab.purityScore', 'Purity')}</th>
            <th className="py-3.5 px-4">{t('blockchain.network', 'Blockchain')}</th>
            <th className="py-3.5 px-4">{t('verification.riskLevel', 'Risk')}</th>
            <th className="py-3.5 px-4 text-right">{t('common.actions', 'Actions')}</th>
          </tr>
        </thead>
        <tbody className="divide-y divide-slate-100">
          {batches.map((b) => (
            <tr key={b.id || b.batchId} className="hover:bg-slate-50/70 transition-colors">
              <td className="py-3.5 px-4 font-mono font-bold text-slate-900">
                <code className="px-2 py-0.5 rounded bg-slate-100 border border-slate-200 text-xs">
                  {b.batchId}
                </code>
              </td>
              <td className="py-3.5 px-4">
                <strong className="block text-slate-900 font-bold">{b.beekeeperName || 'N/A'}</strong>
                <span className="text-slate-500 text-xs">{b.village}</span>
              </td>
              <td className="py-3.5 px-4 font-mono text-slate-700">{b.hiveCode || 'Hive #' + b.hiveId}</td>
              <td className="py-3.5 px-4 font-extrabold text-slate-900">{b.quantityKg != null ? `${b.quantityKg.toFixed(1)} kg` : 'N/A'}</td>
              <td className="py-3.5 px-4">{getStatusBadge(b.status)}</td>
              <td className="py-3.5 px-4">
                {b.purityScore != null ? (
                  <span className="font-extrabold text-emerald-600">{b.purityScore}%</span>
                ) : (
                  <span className="text-slate-400 text-xs">{t('lab.untested', 'Untested')}</span>
                )}
              </td>
              <td className="py-3.5 px-4">
                {b.blockchainTxHash ? (
                  <span className="inline-flex items-center gap-1 px-2 py-0.5 rounded bg-slate-100 border border-slate-200 text-[11px] font-mono font-medium text-slate-700" title={b.blockchainTxHash}>
                    <span>🔗</span>
                    <span>{t('blockchain.integrityVerified', 'Verified')}</span>
                  </span>
                ) : (
                  <span className="text-slate-400 text-xs">{t('profile.statusPending', 'Pending')}</span>
                )}
              </td>
              <td className="py-3.5 px-4">{getRiskBadge(b.riskLevel)}</td>
              <td className="py-3.5 px-4 text-right">
                <Link
                  to={`/admin/batches/${b.batchId}`}
                  className="px-3 py-1.5 rounded-lg bg-slate-100 hover:bg-slate-200 text-slate-700 text-xs font-semibold transition-colors"
                >
                  {t('common.viewDetails', 'Inspect')}
                </Link>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}

export default BatchTable
