import React, { useState, useEffect } from 'react'
import AdminLayout from '../../../layouts/AdminLayout'
import BatchTable from '../components/BatchTable'
import LoadingSpinner from '../../../components/feedback/LoadingSpinner'
import Alert from '../../../components/feedback/Alert'
import adminApi from '../api/adminApi'
import { useLanguage } from '../../../i18n/LanguageContext'

export const AdminBatchesPage = () => {
  const { t } = useLanguage()
  const [batches, setBatches] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [statusFilter, setStatusFilter] = useState('')
  const [searchQuery, setSearchQuery] = useState('')
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)

  const loadBatches = async (p = 0) => {
    setLoading(true)
    setError(null)
    try {
      const res = await adminApi.getBatches({
        status: statusFilter || undefined,
        search: searchQuery || undefined,
        page: p,
        size: 20,
      })
      const data = res.data?.data
      setBatches(data?.content || [])
      setTotalPages(data?.totalPages || 0)
      setPage(p)
    } catch (err) {
      setError(err?.response?.data?.message || t('errors.generic', 'Failed to load batches'))
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadBatches(0)
  }, [statusFilter])

  const handleSearch = (e) => {
    e.preventDefault()
    loadBatches(0)
  }

  return (
    <AdminLayout>
      <div className="space-y-6 text-left">
        {/* Executive Header Banner */}
        <div className="relative overflow-hidden rounded-2xl bg-gradient-to-br from-slate-900 via-slate-900 to-slate-950 text-white p-6 sm:p-7 shadow-md border border-slate-800 flex flex-col sm:flex-row sm:items-center justify-between gap-4">
          <div className="space-y-1.5 max-w-2xl">
            <div className="flex items-center gap-2 text-xs font-semibold text-amber-400 uppercase tracking-widest">
              <span>🍯</span>
              <span>{t('admin.totalBatches', 'Honey Batches')}</span>
            </div>
            <h1 className="text-2xl sm:text-3xl font-extrabold font-['Outfit'] tracking-tight text-white leading-tight">
              {t('admin.batchMonitoring', 'Honey Batch Monitoring')}
            </h1>
            <p className="text-slate-300 text-xs sm:text-sm leading-relaxed">
              {t('admin.batchMonitoringSub', 'Monitor lab testing, blockchain certification, QR generation, and anti-counterfeit logs')}
            </p>
          </div>

          <button
            type="button"
            onClick={() => loadBatches(page)}
            disabled={loading}
            className="self-start sm:self-center inline-flex items-center gap-2 px-3.5 py-2 rounded-xl bg-slate-800 hover:bg-slate-750 border border-slate-700 text-slate-200 text-xs font-semibold shadow-xs transition-all disabled:opacity-50 cursor-pointer"
          >
            <span>🔄</span>
            <span>{t('common.refresh', 'Refresh')}</span>
          </button>
        </div>

        {/* Filter Card */}
        <div className="bg-white rounded-2xl border border-slate-200/80 p-4 sm:p-5 shadow-xs">
          <form onSubmit={handleSearch} className="flex flex-wrap gap-3 items-center">
            <div className="flex-1 min-w-0 sm:min-w-64 w-full sm:w-auto">
              <input
                type="text"
                className="w-full text-xs sm:text-sm py-2 px-3 bg-slate-50 border border-slate-200 rounded-xl text-slate-900 placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-amber-500/20 focus:border-amber-500 transition-all"
                placeholder={t('admin.searchBatchPlaceholder', 'Search by Batch ID (e.g. HC-2026-AB12CD34)...')}
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
              />
            </div>

            <div className="w-full sm:w-52">
              <select
                className="w-full text-xs sm:text-sm py-2 px-3 bg-slate-50 border border-slate-200 rounded-xl text-slate-900 font-semibold cursor-pointer focus:outline-none focus:ring-2 focus:ring-amber-500/20 focus:border-amber-500 transition-all"
                value={statusFilter}
                onChange={(e) => setStatusFilter(e.target.value)}
              >
                <option value="">{t('admin.allStatuses', 'All Statuses')}</option>
                <option value="PURE">{t('lab.pure', 'PURE')}</option>
                <option value="UNDER_REVIEW">{t('lab.underReview', 'UNDER_REVIEW')}</option>
                <option value="FAILED">{t('lab.failed', 'FAILED')}</option>
                <option value="SENT_FOR_TESTING">{t('batch.statusSentForTesting', 'SENT_FOR_TESTING')}</option>
                <option value="QR_GENERATED">{t('batch.statusQrGenerated', 'QR_GENERATED')}</option>
                <option value="IN_STOCK">IN_STOCK</option>
                <option value="SOLD">SOLD</option>
              </select>
            </div>

            <button
              type="submit"
              className="px-4 py-2 rounded-xl bg-amber-500 hover:bg-amber-600 text-slate-950 text-xs sm:text-sm font-bold shadow-xs transition-colors cursor-pointer w-full sm:w-auto"
            >
              {t('common.search', 'Search')}
            </button>
            {(searchQuery || statusFilter) && (
              <button
                type="button"
                className="px-3.5 py-2 rounded-xl bg-slate-100 hover:bg-slate-200 text-slate-700 text-xs sm:text-sm font-semibold transition-colors cursor-pointer w-full sm:w-auto"
                onClick={() => {
                  setSearchQuery('')
                  setStatusFilter('')
                }}
              >
                {t('common.clear', 'Clear')}
              </button>
            )}
          </form>
        </div>

        {error && <Alert type="danger" message={error} />}

        {loading ? (
          <div className="py-16 text-center">
            <LoadingSpinner text={t('loading.loading', 'Loading batches...')} />
          </div>
        ) : (
          <>
            <BatchTable batches={batches} />

            {totalPages > 1 && (
              <div className="flex items-center justify-center gap-3 pt-2">
                <button
                  type="button"
                  className="px-3 py-1.5 rounded-xl bg-white border border-slate-200 text-xs font-semibold text-slate-700 hover:bg-slate-50 disabled:opacity-40 cursor-pointer shadow-2xs transition-all"
                  disabled={page === 0}
                  onClick={() => loadBatches(page - 1)}
                >
                  ← {t('common.back', 'Prev')}
                </button>
                <span className="text-slate-500 text-xs font-semibold">
                  {page + 1} / {totalPages}
                </span>
                <button
                  type="button"
                  className="px-3 py-1.5 rounded-xl bg-white border border-slate-200 text-xs font-semibold text-slate-700 hover:bg-slate-50 disabled:opacity-40 cursor-pointer shadow-2xs transition-all"
                  disabled={page >= totalPages - 1}
                  onClick={() => loadBatches(page + 1)}
                >
                  {t('common.next', 'Next')} →
                </button>
              </div>
            )}
          </>
        )}
      </div>
    </AdminLayout>
  )
}

export default AdminBatchesPage
