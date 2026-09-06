import React from 'react'
import { Link } from 'react-router-dom'
import { useLanguage } from '../../../i18n/LanguageContext'

export const BeekeeperTable = ({ beekeepers = [], onStatusUpdate, updatingId }) => {
  const { t } = useLanguage()

  if (beekeepers.length === 0) {
    return (
      <div className="bg-white rounded-2xl border border-slate-200/80 p-8 text-center shadow-xs">
        <span className="text-3xl block mb-2">🧑‍🌾</span>
        <p className="text-slate-500 text-sm font-medium">{t('empty.noData', 'No beekeeper records found.')}</p>
      </div>
    )
  }

  const getStatusBadge = (status) => {
    switch (status) {
      case 'APPROVED':
        return (
          <span className="inline-flex items-center gap-1 px-2.5 py-1 rounded-full text-xs font-bold bg-emerald-50 text-emerald-700 border border-emerald-200">
            ✓ {t('profile.statusApproved', 'APPROVED')}
          </span>
        )
      case 'REJECTED':
        return (
          <span className="inline-flex items-center gap-1 px-2.5 py-1 rounded-full text-xs font-bold bg-red-50 text-red-700 border border-red-200">
            ✕ {t('profile.statusRejected', 'REJECTED')}
          </span>
        )
      case 'PENDING':
      default:
        return (
          <span className="inline-flex items-center gap-1 px-2.5 py-1 rounded-full text-xs font-bold bg-amber-50 text-amber-800 border border-amber-300">
            ⏳ {t('profile.statusPending', 'PENDING')}
          </span>
        )
    }
  }

  return (
    <div className="overflow-x-auto bg-white rounded-2xl border border-slate-200/80 shadow-xs">
      <table className="w-full text-left border-collapse text-xs sm:text-sm">
        <thead>
          <tr className="bg-slate-50/90 border-b border-slate-200 text-[11px] font-bold text-slate-600 uppercase tracking-wider">
            <th className="py-3.5 px-4">{t('auth.beekeeperRole', 'Beekeeper')}</th>
            <th className="py-3.5 px-4">{t('onboarding.kvicId', 'KVIC ID')}</th>
            <th className="py-3.5 px-4">{t('onboarding.village', 'Village / Region')}</th>
            <th className="py-3.5 px-4">{t('common.status', 'Status')}</th>
            <th className="py-3.5 px-4">{t('navigation.myHives', 'Hives')}</th>
            <th className="py-3.5 px-4">{t('navigation.myBatches', 'Batches')}</th>
            <th className="py-3.5 px-4">{t('navigation.reviews', 'Rating')}</th>
            <th className="py-3.5 px-4 text-right">{t('common.actions', 'Actions')}</th>
          </tr>
        </thead>
        <tbody className="divide-y divide-slate-100">
          {beekeepers.map((b) => (
            <tr key={b.id} className="hover:bg-slate-50/70 transition-colors">
              <td className="py-3.5 px-4">
                <div className="flex items-center gap-3">
                  {b.photoUrl ? (
                    <img src={b.photoUrl} alt={b.name} className="w-9 h-9 rounded-xl object-cover border border-slate-200" />
                  ) : (
                    <div className="w-9 h-9 rounded-xl bg-amber-50 border border-amber-200 text-amber-800 flex items-center justify-center text-sm font-bold shrink-0">
                      🧑‍🌾
                    </div>
                  )}
                  <div>
                    <strong className="block text-slate-900 font-bold">{b.name}</strong>
                    <span className="text-slate-500 text-xs font-mono">{b.phoneNumber}</span>
                  </div>
                </div>
              </td>
              <td className="py-3.5 px-4">
                <code className="px-2 py-0.5 rounded bg-slate-100 text-slate-800 text-xs font-mono border border-slate-200">
                  {b.kvicId || 'N/A'}
                </code>
              </td>
              <td className="py-3.5 px-4 text-slate-700 font-medium">{b.village || 'N/A'}</td>
              <td className="py-3.5 px-4">{getStatusBadge(b.verificationStatus)}</td>
              <td className="py-3.5 px-4 font-bold text-slate-800">{b.hiveCount}</td>
              <td className="py-3.5 px-4 font-bold text-slate-800">{b.batchCount}</td>
              <td className="py-3.5 px-4">
                {b.averageRating > 0 ? (
                  <span className="text-amber-600 font-extrabold flex items-center gap-1">
                    <span>★</span>
                    <span>{b.averageRating.toFixed(1)}</span>
                  </span>
                ) : (
                  <span className="text-slate-400 text-xs">{t('admin.noRatings', 'No ratings')}</span>
                )}
              </td>
              <td className="py-3.5 px-4 text-right">
                <div className="flex items-center justify-end gap-2">
                  <Link
                    to={`/admin/beekeepers/${b.id}`}
                    className="px-3 py-1.5 rounded-lg bg-slate-100 hover:bg-slate-200 text-slate-700 text-xs font-semibold transition-colors"
                  >
                    {t('common.viewDetails', 'View')}
                  </Link>
                  {b.verificationStatus === 'PENDING' && (
                    <>
                      <button
                        type="button"
                        className="px-3 py-1.5 rounded-lg bg-emerald-600 hover:bg-emerald-700 text-white text-xs font-bold shadow-xs transition-colors disabled:opacity-50 cursor-pointer"
                        disabled={updatingId === b.id}
                        onClick={() => onStatusUpdate?.(b.id, 'APPROVED')}
                      >
                        {t('admin.approve', 'Approve')}
                      </button>
                      <button
                        type="button"
                        className="px-3 py-1.5 rounded-lg bg-red-50 hover:bg-red-100 text-red-700 border border-red-200 text-xs font-bold transition-colors disabled:opacity-50 cursor-pointer"
                        disabled={updatingId === b.id}
                        onClick={() => onStatusUpdate?.(b.id, 'REJECTED')}
                      >
                        {t('admin.reject', 'Reject')}
                      </button>
                    </>
                  )}
                </div>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}

export default BeekeeperTable
