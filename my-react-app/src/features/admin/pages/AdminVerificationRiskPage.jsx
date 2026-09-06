import React, { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import AdminLayout from '../../../layouts/AdminLayout'
import PageHeader from '../../../components/layout/PageHeader'
import Card from '../../../components/ui/Card'
import LoadingSpinner from '../../../components/feedback/LoadingSpinner'
import Alert from '../../../components/feedback/Alert'
import adminApi from '../api/adminApi'
import { useLanguage } from '../../../i18n/LanguageContext'

export const AdminVerificationRiskPage = () => {
  const { t } = useLanguage()
  const [riskData, setRiskData] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
    adminApi
      .getVerificationRiskAnalytics()
      .then((res) => setRiskData(res.data?.data))
      .catch((err) =>
        setError(err?.response?.data?.message || t('errors.generic', 'Failed to load risk analytics'))
      )
      .finally(() => setLoading(false))
  }, [])

  const totalScans =
    (riskData?.normalCount ?? 0) + (riskData?.watchCount ?? 0) + (riskData?.highRiskCount ?? 0)
  const normalPct = totalScans > 0 ? Math.round(((riskData?.normalCount ?? 0) / totalScans) * 100) : 100

  return (
    <AdminLayout>
      <div className="space-y-6">
        {/* Standard Admin Header */}
        <PageHeader
          title={
            <span>
              🛡️ {t('admin.antiCounterfeitRisk', 'Anti-Counterfeit Verification Risk')}
            </span>
          }
          subtitle={t(
            'admin.antiCounterfeitRiskSub',
            'Scan velocity anomaly detection, counterfeit alerts, and high-risk batch auditing'
          )}
          actions={
            <div className="flex items-center gap-2">
              <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-bold bg-slate-100 text-slate-800 border border-slate-200">
                <span className="w-2 h-2 rounded-full bg-amber-500 animate-pulse" />
                Live Heuristic Engine
              </span>
            </div>
          }
        />

        {error && <Alert type="danger" message={error} onClose={() => setError(null)} />}

        {loading ? (
          <div className="py-16 text-center">
            <LoadingSpinner text={t('loading.verifying', 'Analyzing verification scan logs & velocity telemetry...')} />
          </div>
        ) : (
          <>
            {/* Real-time Threat Intelligence Overview */}
            <div className="p-5 rounded-2xl bg-slate-900 text-white shadow-sm flex flex-col md:flex-row items-start md:items-center justify-between gap-4">
              <div className="space-y-1">
                <div className="flex items-center gap-2 text-xs font-bold text-amber-400 uppercase tracking-wider">
                  <span>⚡ QR Velocity Telemetry</span>
                  <span>•</span>
                  <span>Global Audit Status</span>
                </div>
                <h2 className="text-xl font-black font-['Outfit']">
                  Anti-Tamper Cryptographic Scanning Grid
                </h2>
                <p className="text-xs text-slate-300 max-w-xl">
                  Monitoring scan patterns across India. Algorithmic thresholds flag duplicate QR scans from disparate geographical locations or sudden velocity spikes.
                </p>
              </div>

              <div className="flex items-center gap-4 shrink-0 bg-slate-800/80 p-3 rounded-xl border border-slate-700">
                <div className="text-center">
                  <span className="text-[11px] text-slate-400 block uppercase font-bold">Total Scans</span>
                  <span className="text-lg font-mono font-bold text-white">{totalScans}</span>
                </div>
                <div className="h-8 w-px bg-slate-700" />
                <div className="text-center">
                  <span className="text-[11px] text-slate-400 block uppercase font-bold">Integrity Score</span>
                  <span className="text-lg font-mono font-bold text-amber-400">{normalPct}%</span>
                </div>
              </div>
            </div>

            {/* Risk Distribution Cards */}
            <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
              <div className="p-5 rounded-xl bg-white border border-slate-200 shadow-xs space-y-2">
                <div className="flex items-center justify-between">
                  <span className="w-9 h-9 rounded-lg bg-blue-50 border border-blue-200 flex items-center justify-center text-lg">
                    ✅
                  </span>
                  <span className="text-xs font-bold text-blue-700 bg-blue-50 px-2 py-0.5 rounded-full border border-blue-200">
                    AUTHENTIC
                  </span>
                </div>
                <div>
                  <span className="text-xs text-slate-600 block font-semibold">
                    {t('verification.riskNormal', 'NORMAL Scans')}
                  </span>
                  <h3 className="text-2xl font-black font-mono text-slate-900">
                    {riskData?.normalCount ?? 0}
                  </h3>
                  <p className="text-[11px] text-slate-600 mt-0.5">
                    {t('admin.normalScansSub', 'Standard consumer verifications with valid unique QR tokens.')}
                  </p>
                </div>
              </div>

              <div className="p-5 rounded-xl bg-white border border-slate-200 shadow-xs space-y-2">
                <div className="flex items-center justify-between">
                  <span className="w-9 h-9 rounded-lg bg-amber-50 border border-amber-200 flex items-center justify-center text-lg">
                    ⚠️
                  </span>
                  <span className="text-xs font-bold text-amber-900 bg-amber-50 px-2 py-0.5 rounded-full border border-amber-300">
                    ELEVATED
                  </span>
                </div>
                <div>
                  <span className="text-xs text-slate-600 block font-semibold">
                    {t('verification.riskWatch', 'WATCH Scans')}
                  </span>
                  <h3 className="text-2xl font-black font-mono text-amber-600">
                    {riskData?.watchCount ?? 0}
                  </h3>
                  <p className="text-[11px] text-slate-600 mt-0.5">
                    {t('admin.watchScansSub', 'Elevated scan velocity detected above baseline retail rate.')}
                  </p>
                </div>
              </div>

              <div className="p-5 rounded-xl bg-white border border-slate-200 shadow-xs space-y-2">
                <div className="flex items-center justify-between">
                  <span className="w-9 h-9 rounded-lg bg-slate-100 border border-slate-300 flex items-center justify-center text-lg">
                    🚨
                  </span>
                  <span className="text-xs font-bold text-slate-900 bg-slate-200 px-2 py-0.5 rounded-full border border-slate-300">
                    HIGH ALERT
                  </span>
                </div>
                <div>
                  <span className="text-xs text-slate-600 block font-semibold">
                    {t('verification.riskHigh', 'HIGH RISK Scans')}
                  </span>
                  <h3 className="text-2xl font-black font-mono text-slate-900">
                    {riskData?.highRiskCount ?? 0}
                  </h3>
                  <p className="text-[11px] text-slate-600 mt-0.5">
                    {t('admin.highRiskScansSub', 'Potential duplicate QR cloning or counterfeit distribution.')}
                  </p>
                </div>
              </div>
            </div>

            {/* High-Risk Flagged Batches Table */}
            <Card className="p-6">
              <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2 mb-4 pb-3 border-b border-slate-100">
                <div>
                  <h3 className="text-base font-bold text-slate-900 font-['Outfit'] flex items-center gap-2">
                    <span>🚨</span> {t('admin.flaggedBatches', 'Flagged Batches Requiring Audit')}
                  </h3>
                  <p className="text-xs text-slate-600 mt-0.5">
                    Batches exhibiting anomalous verification velocity requiring KVIC officer inspection.
                  </p>
                </div>
                {riskData?.highRiskBatches?.length > 0 && (
                  <span className="text-xs font-bold text-amber-900 bg-amber-50 px-2.5 py-1 rounded-full border border-amber-300">
                    {riskData.highRiskBatches.length} Batches Flagged
                  </span>
                )}
              </div>

              {riskData?.highRiskBatches?.length > 0 ? (
                <div className="overflow-x-auto border border-slate-200 rounded-lg">
                  <table className="w-full text-left text-xs sm:text-sm">
                    <thead>
                      <tr className="bg-slate-50 border-b border-slate-200 text-slate-600 uppercase text-[11px] font-bold tracking-wider">
                        <th className="py-3 px-4">{t('batch.batchId', 'Batch ID')}</th>
                        <th className="py-3 px-4">{t('auth.beekeeperRole', 'Beekeeper')}</th>
                        <th className="py-3 px-4">{t('onboarding.village', 'Village')}</th>
                        <th className="py-3 px-4">{t('verification.historyTitle', 'Total Scans')}</th>
                        <th className="py-3 px-4">{t('verification.riskLevel', 'Risk Level')}</th>
                        <th className="py-3 px-4 text-right">{t('common.actions', 'Action')}</th>
                      </tr>
                    </thead>
                    <tbody className="divide-y divide-slate-100 bg-white">
                      {riskData.highRiskBatches.map((b) => (
                        <tr key={b.batchId} className="hover:bg-slate-50/80 transition-colors">
                          <td className="py-3.5 px-4 font-mono font-bold text-blue-600">
                            <code>{b.batchId}</code>
                          </td>
                          <td className="py-3.5 px-4 font-medium text-slate-900">
                            {b.beekeeperName || 'N/A'}
                          </td>
                          <td className="py-3.5 px-4 text-slate-600">
                            {b.village || 'N/A'}
                          </td>
                          <td className="py-3.5 px-4 font-semibold text-slate-900">
                            <span className="font-mono bg-slate-100 px-2 py-0.5 rounded text-xs">
                              {b.verificationCount} scans
                            </span>
                          </td>
                          <td className="py-3.5 px-4">
                            <span
                              className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-[11px] font-bold ${
                                b.riskLevel === 'HIGH_RISK'
                                  ? 'bg-slate-900 text-white'
                                  : 'bg-amber-50 text-amber-900 border border-amber-300'
                              }`}
                            >
                              {b.riskLevel}
                            </span>
                          </td>
                          <td className="py-3.5 px-4 text-right">
                            <Link
                              to={`/admin/batches/${b.batchId}`}
                              className="inline-flex items-center gap-1 px-3 py-1.5 rounded-lg bg-blue-600 hover:bg-blue-700 text-white text-xs font-bold transition-colors shadow-xs"
                            >
                              <span>{t('common.viewDetails', 'Inspect Audit Record')}</span>
                              <span>→</span>
                            </Link>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              ) : (
                <div className="py-12 text-center space-y-3">
                  <div className="w-12 h-12 rounded-full bg-blue-50 border border-blue-200 flex items-center justify-center mx-auto text-2xl">
                    ✅
                  </div>
                  <div>
                    <h4 className="font-bold text-slate-900 text-sm">No Counterfeit Risks Detected</h4>
                    <p className="text-xs text-slate-600 max-w-md mx-auto mt-0.5">
                      {t(
                        'admin.noFlaggedBatches',
                        'All public QR verification logs are within legitimate consumer rate thresholds. No suspicious cloning patterns identified.'
                      )}
                    </p>
                  </div>
                </div>
              )}
            </Card>

            {/* Anti-Counterfeit Safeguards & Protocol Card */}
            <div className="p-6 rounded-2xl bg-white border border-slate-200 shadow-xs space-y-3">
              <h3 className="font-bold text-base text-slate-900 font-['Outfit'] flex items-center gap-2">
                <span>🛡️</span> Multi-Layered Anti-Tamper Safeguards
              </h3>
              <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 text-xs text-slate-600 pt-1">
                <div className="p-3.5 rounded-xl bg-slate-50 border border-slate-200 space-y-1">
                  <h4 className="font-bold text-slate-900 flex items-center gap-1.5">
                    <span>📡</span> Velocity Anomaly Engine
                  </h4>
                  <p>
                    Flags jars scanned more than 5 times within a 60-minute window or from multiple distinct GPS coordinates simultaneously.
                  </p>
                </div>
                <div className="p-3.5 rounded-xl bg-slate-50 border border-slate-200 space-y-1">
                  <h4 className="font-bold text-slate-900 flex items-center gap-1.5">
                    <span>⛓️</span> Cryptographic QR Hash
                  </h4>
                  <p>
                    Each batch QR embeds an SHA-256 batch hash validated against the immutable public blockchain ledger.
                  </p>
                </div>
                <div className="p-3.5 rounded-xl bg-slate-50 border border-slate-200 space-y-1">
                  <h4 className="font-bold text-slate-900 flex items-center gap-1.5">
                    <span>⚖️</span> Automatic Freeze Mechanism
                  </h4>
                  <p>
                    Batches triggering critical HIGH RISK alerts can be locked instantly by KVIC officers, disabling marketplace checkout.
                  </p>
                </div>
              </div>
            </div>
          </>
        )}
      </div>
    </AdminLayout>
  )
}

export default AdminVerificationRiskPage
