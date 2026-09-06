import React, { useState, useEffect } from 'react'
import Card from '../../../components/ui/Card'
import VerificationRiskBadge from './VerificationRiskBadge'
import RecentVerificationList from './RecentVerificationList'
import verificationApi from '../api/verificationApi'

export const VerificationHistoryCard = ({ batchId, initialSummary }) => {
  const [history, setHistory] = useState(null)
  const [showRecent, setShowRecent] = useState(false)
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    if (batchId) {
      setLoading(true)
      verificationApi.getPublicHistory(batchId)
        .then((res) => setHistory(res.data.data))
        .catch(() => setHistory(null))
        .finally(() => setLoading(false))
    }
  }, [batchId])

  const total = history?.totalVerifications ?? initialSummary?.totalVerifications ?? 1
  const riskLevel = history?.riskLevel ?? initialSummary?.riskLevel ?? 'NORMAL'
  const riskMessage = history?.riskMessage ?? initialSummary?.riskMessage ?? 'Verification activity appears normal.'
  const lastVerified = history?.lastVerifiedAt ?? initialSummary?.lastVerifiedAt

  const getRiskBorder = () => {
    if (riskLevel === 'HIGH_RISK') return 'border-blue-300 bg-blue-50/40 text-blue-950'
    if (riskLevel === 'WATCH') return 'border-amber-300 bg-amber-50/40 text-amber-950'
    return 'border-slate-200 bg-white text-slate-900 shadow-sm'
  }

  return (
    <Card className={`p-6 space-y-4 border ${getRiskBorder()}`}>
      <div className="flex items-center justify-between">
        <h2 className="text-lg font-bold text-slate-900 font-['Outfit'] flex items-center gap-2">
          <span>🛡️</span> Verification History & Anti-Counterfeit
        </h2>
        <VerificationRiskBadge riskLevel={riskLevel} size="lg" />
      </div>

      {/* Metric Readout */}
      <div className="grid grid-cols-2 gap-3 text-xs">
        <div className="p-3.5 rounded-xl bg-slate-50 border border-slate-200">
          <p className="text-slate-500 font-medium">Total Scans</p>
          <p className="text-2xl font-black text-blue-600 font-mono mt-0.5">
            ✓ {total} {total === 1 ? 'time' : 'times'}
          </p>
        </div>
        <div className="p-3.5 rounded-xl bg-slate-50 border border-slate-200">
          <p className="text-slate-500 font-medium">Last Verified</p>
          <p className="text-xs font-semibold text-slate-800 mt-1">
            {lastVerified
              ? new Date(lastVerified).toLocaleDateString('en-IN', {
                  day: '2-digit',
                  month: 'short',
                  hour: '2-digit',
                  minute: '2-digit',
                })
              : 'Just now'}
          </p>
        </div>
      </div>

      {/* Risk Explanation */}
      <div className="p-3.5 rounded-xl bg-slate-50 border border-slate-200 text-xs space-y-1">
        <p className="text-slate-800 font-medium leading-relaxed">{riskMessage}</p>
        <p className="text-[11px] text-slate-500">
          ℹ️ Verification activity is a risk heuristic. A high scan count does not by itself prove that a product is counterfeit.
        </p>
      </div>

      {/* Expandable Recent Scans */}
      {history?.recentEvents && history.recentEvents.length > 0 && (
        <div className="pt-1">
          <button
            type="button"
            onClick={() => setShowRecent(!showRecent)}
            className="text-xs text-slate-600 hover:text-blue-600 flex items-center gap-1 transition-colors font-medium"
          >
            <span>{showRecent ? '▲ Hide' : '▼ View'} Recent Verification Activity</span>
          </button>

          {showRecent && (
            <div className="mt-3">
              <RecentVerificationList events={history.recentEvents} />
            </div>
          )}
        </div>
      )}
    </Card>
  )
}

export default VerificationHistoryCard
