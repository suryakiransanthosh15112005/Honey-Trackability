import React, { useState, useEffect } from 'react'
import AdminLayout from '../../../layouts/AdminLayout'
import MetricCard from '../../../components/ui/MetricCard'
import LoadingSpinner from '../../../components/feedback/LoadingSpinner'
import Alert from '../../../components/feedback/Alert'
import adminApi from '../api/adminApi'
import { useLanguage } from '../../../i18n/LanguageContext'

export const AdminLabPage = () => {
  const { t } = useLanguage()
  const [labTests, setLabTests] = useState([])
  const [purityStats, setPurityStats] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [resultFilter, setResultFilter] = useState('')
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)

  const loadData = async (p = 0) => {
    setLoading(true)
    setError(null)
    try {
      const [testsRes, purityRes] = await Promise.all([
        adminApi.getLabTests({ result: resultFilter || undefined, page: p, size: 20 }),
        adminApi.getPurityAnalytics(),
      ])
      const data = testsRes.data?.data
      setLabTests(data?.content || [])
      setTotalPages(data?.totalPages || 0)
      setPurityStats(purityRes.data?.data)
      setPage(p)
    } catch (err) {
      setError(err?.response?.data?.message || t('errors.generic', 'Failed to load laboratory records'))
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadData(0)
  }, [resultFilter])

  return (
    <AdminLayout>
      <div className="space-y-6 text-left">
        {/* Executive Header Banner */}
        <div className="relative overflow-hidden rounded-2xl bg-gradient-to-br from-slate-900 via-slate-900 to-slate-950 text-white p-6 sm:p-7 shadow-md border border-slate-800 flex flex-col sm:flex-row sm:items-center justify-between gap-4">
          <div className="space-y-1.5 max-w-2xl">
            <div className="flex items-center gap-2 text-xs font-semibold text-amber-400 uppercase tracking-widest">
              <span>🔬</span>
              <span>{t('admin.labAuditsTitle', 'Laboratory Purity Audits')}</span>
            </div>
            <h1 className="text-2xl sm:text-3xl font-extrabold font-['Outfit'] tracking-tight text-white leading-tight">
              {t('admin.navLabTests', 'Laboratory Testing Records')}
            </h1>
            <p className="text-slate-300 text-xs sm:text-sm leading-relaxed">
              {t('admin.labRecordsSub', 'Audited purity evaluations, chemical parameters, and certified honey pass rates')}
            </p>
          </div>

          <button
            type="button"
            onClick={() => loadData(page)}
            disabled={loading}
            className="self-start sm:self-center inline-flex items-center gap-2 px-3.5 py-2 rounded-xl bg-slate-800 hover:bg-slate-750 border border-slate-700 text-slate-200 text-xs font-semibold shadow-xs transition-all disabled:opacity-50 cursor-pointer"
          >
            <span>🔄</span>
            <span>{t('common.refresh', 'Refresh')}</span>
          </button>
        </div>

        {/* Top Purity Metric Cards */}
        {purityStats && (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5">
            <MetricCard
              icon="🧪"
              label={t('admin.totalTests', 'Total Tests')}
              value={purityStats.totalTests ?? 0}
              subtext={t('admin.certifiedTestsLogged', 'Certified tests logged')}
              accent="neutral"
              badge="Audited"
            />
            <MetricCard
              icon="✅"
              label={t('admin.passRate', 'Pass Rate')}
              value={`${purityStats.passRate}%`}
              subtext={`${purityStats.pureCount} ${t('lab.pure', 'Pure')} ${t('navigation.batches', 'batches')}`}
              accent="amber"
              badge="FSSAI Standard"
            />
            <MetricCard
              icon="⭐"
              label={t('admin.avgPurityScore', 'Average Purity Score')}
              value={`${purityStats.averagePurityScore}%`}
              subtext={t('admin.acrossAllBatches', 'Across all lab tested batches')}
              accent="amber"
              badge="Grade A"
            />
            <MetricCard
              icon="⚠️"
              label={t('admin.failedUnderReview', 'Failed / Under Review')}
              value={purityStats.failedCount + purityStats.underReviewCount}
              subtext={`${purityStats.failedCount} ${t('lab.failed', 'Failed')} • ${purityStats.underReviewCount} ${t('lab.underReview', 'Review')}`}
              accent="neutral"
              badge={purityStats.failedCount > 0 ? "Flagged" : "Nominal"}
            />
          </div>
        )}

        {/* Filter Card */}
        <div className="bg-white rounded-2xl border border-slate-200/80 p-4 sm:p-5 shadow-xs flex flex-wrap gap-4 items-center">
          <label className="text-xs sm:text-sm font-bold text-slate-700">{t('admin.filterByResult', 'Filter by Result:')}</label>
          <select
            className="text-xs sm:text-sm py-2 px-3 bg-slate-50 border border-slate-200 rounded-xl text-slate-900 font-semibold cursor-pointer focus:outline-none focus:ring-2 focus:ring-amber-500/20 focus:border-amber-500 transition-all w-52"
            value={resultFilter}
            onChange={(e) => setResultFilter(e.target.value)}
          >
            <option value="">{t('admin.allResults', 'All Results')}</option>
            <option value="PURE">{t('lab.pure', 'PURE')}</option>
            <option value="UNDER_REVIEW">{t('lab.underReview', 'UNDER_REVIEW')}</option>
            <option value="FAILED">{t('lab.failed', 'FAILED')}</option>
          </select>
        </div>

        {error && <Alert type="danger" message={error} />}

        {loading ? (
          <div className="py-16 text-center">
            <LoadingSpinner text={t('loading.loading', 'Loading laboratory tests...')} />
          </div>
        ) : (
          <div className="overflow-x-auto bg-white rounded-2xl border border-slate-200/80 shadow-xs">
            <table className="w-full text-left border-collapse text-xs sm:text-sm">
              <thead>
                <tr className="bg-slate-50/90 border-b border-slate-200 text-[11px] font-bold text-slate-600 uppercase tracking-wider">
                  <th className="py-3.5 px-4">{t('batch.batchId', 'Batch ID')}</th>
                  <th className="py-3.5 px-4">{t('auth.beekeeperRole', 'Beekeeper')}</th>
                  <th className="py-3.5 px-4">{t('lab.testingFacility', 'Testing Facility')}</th>
                  <th className="py-3.5 px-4">{t('lab.purityScore', 'Purity Score')}</th>
                  <th className="py-3.5 px-4">{t('lab.testResult', 'Test Outcome')}</th>
                  <th className="py-3.5 px-4 text-right">{t('lab.testedAt', 'Date Tested')}</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {labTests.map((item) => (
                  <tr key={item.id || item.batchId} className="hover:bg-slate-50/70 transition-colors">
                    <td className="py-3.5 px-4 font-mono font-bold text-slate-900">
                      <code className="px-2 py-0.5 rounded bg-slate-100 border border-slate-200 text-xs">
                        {item.batchId}
                      </code>
                    </td>
                    <td className="py-3.5 px-4 font-semibold text-slate-900">{item.beekeeperName || 'N/A'}</td>
                    <td className="py-3.5 px-4 text-slate-600 font-medium">{item.labName || 'Central Agmark Lab'}</td>
                    <td className="py-3.5 px-4">
                      <strong className={`font-extrabold ${item.purityScore >= 80 ? 'text-emerald-600' : 'text-red-600'}`}>
                        {item.purityScore}%
                      </strong>
                    </td>
                    <td className="py-3.5 px-4">
                      <span className={`inline-flex items-center px-2.5 py-1 rounded-full text-xs font-bold ${
                        item.result === 'PURE'
                          ? 'bg-emerald-50 text-emerald-700 border border-emerald-200'
                          : item.result === 'FAILED'
                          ? 'bg-red-50 text-red-700 border border-red-200'
                          : 'bg-amber-50 text-amber-800 border border-amber-200'
                      }`}>
                        {item.result}
                      </span>
                    </td>
                    <td className="py-3.5 px-4 text-right text-slate-500 font-mono text-xs">
                      {item.testDate ? new Date(item.testDate).toLocaleDateString('en-IN') : 'N/A'}
                    </td>
                  </tr>
                ))}
                {labTests.length === 0 && (
                  <tr>
                    <td colSpan="6" className="py-8 text-center text-slate-400">
                      {t('empty.noTests', 'No lab test records found.')}
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </AdminLayout>
  )
}

export default AdminLabPage
