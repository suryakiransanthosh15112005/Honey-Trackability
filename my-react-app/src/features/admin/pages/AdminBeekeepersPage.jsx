import React, { useState, useEffect } from 'react'
import { useSearchParams } from 'react-router-dom'
import AdminLayout from '../../../layouts/AdminLayout'
import BeekeeperTable from '../components/BeekeeperTable'
import LoadingSpinner from '../../../components/feedback/LoadingSpinner'
import Alert from '../../../components/feedback/Alert'
import adminApi from '../api/adminApi'
import { useLanguage } from '../../../i18n/LanguageContext'

export const AdminBeekeepersPage = () => {
  const { t } = useLanguage()
  const [searchParams, setSearchParams] = useSearchParams()
  const initialStatus = searchParams.get('status') || ''

  const [beekeepers, setBeekeepers] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [statusFilter, setStatusFilter] = useState(initialStatus)
  const [searchQuery, setSearchQuery] = useState('')
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  const [updatingId, setUpdatingId] = useState(null)

  const loadBeekeepers = async (p = 0) => {
    setLoading(true)
    setError(null)
    try {
      const res = await adminApi.getBeekeepers({
        status: statusFilter || undefined,
        search: searchQuery || undefined,
        page: p,
        size: 20,
      })
      const data = res.data?.data
      setBeekeepers(data?.content || [])
      setTotalPages(data?.totalPages || 0)
      setPage(p)
    } catch (err) {
      setError(err?.response?.data?.message || t('errors.generic', 'Failed to load beekeepers'))
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadBeekeepers(0)
  }, [statusFilter])

  const handleSearch = (e) => {
    e.preventDefault()
    loadBeekeepers(0)
  }

  const handleStatusUpdate = async (id, newStatus) => {
    if (!window.confirm(t('dialog.confirmTitle', 'Are you sure?'))) return

    setUpdatingId(id)
    try {
      await adminApi.updateBeekeeperStatus(id, newStatus)
      await loadBeekeepers(page)
    } catch (err) {
      alert(err?.response?.data?.message || t('errors.generic', 'Failed to update status'))
    } finally {
      setUpdatingId(null)
    }
  }

  return (
    <AdminLayout>
      <div className="space-y-6 text-left">
        {/* Executive Header Banner */}
        <div className="relative overflow-hidden rounded-2xl bg-gradient-to-br from-slate-900 via-slate-900 to-slate-950 text-white p-6 sm:p-7 shadow-md border border-slate-800 flex flex-col sm:flex-row sm:items-center justify-between gap-4">
          <div className="space-y-1.5 max-w-2xl">
            <div className="flex items-center gap-2 text-xs font-semibold text-amber-400 uppercase tracking-widest">
              <span>🏛️</span>
              <span>{t('admin.oversightBadge', 'KVIC & HoneyChain Oversight')}</span>
            </div>
            <h1 className="text-2xl sm:text-3xl font-extrabold font-['Outfit'] tracking-tight text-white leading-tight">
              {t('admin.beekeeperGovernance', 'Beekeeper Governance & Audit')}
            </h1>
            <p className="text-slate-300 text-xs sm:text-sm leading-relaxed">
              {t('admin.beekeeperGovernanceSub', 'Review KVIC credentials, approve onboarding, and inspect honey apiaries')}
            </p>
          </div>

          <button
            type="button"
            onClick={() => loadBeekeepers(page)}
            disabled={loading}
            className="self-start sm:self-center inline-flex items-center gap-2 px-3.5 py-2 rounded-xl bg-slate-800 hover:bg-slate-750 border border-slate-700 text-slate-200 text-xs font-semibold shadow-xs transition-all disabled:opacity-50 cursor-pointer"
          >
            <span>🔄</span>
            <span>{t('common.refresh', 'Refresh')}</span>
          </button>
        </div>

        {/* Filter & Search Bar Card */}
        <div className="bg-white rounded-2xl border border-slate-200/80 p-4 sm:p-5 shadow-xs">
          <form onSubmit={handleSearch} className="flex flex-wrap gap-3 items-center">
            <div className="flex-1 min-w-0 sm:min-w-64 w-full sm:w-auto">
              <input
                type="text"
                className="w-full text-xs sm:text-sm py-2 px-3 bg-slate-50 border border-slate-200 rounded-xl text-slate-900 placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-amber-500/20 focus:border-amber-500 transition-all"
                placeholder={t('admin.searchBeekeeperPlaceholder', 'Search by Beekeeper Name, KVIC ID, or Village...')}
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
              />
            </div>

            <div className="w-full sm:w-48">
              <select
                className="w-full text-xs sm:text-sm py-2 px-3 bg-slate-50 border border-slate-200 rounded-xl text-slate-900 font-semibold cursor-pointer focus:outline-none focus:ring-2 focus:ring-amber-500/20 focus:border-amber-500 transition-all"
                value={statusFilter}
                onChange={(e) => {
                  setStatusFilter(e.target.value)
                  setSearchParams(e.target.value ? { status: e.target.value } : {})
                }}
              >
                <option value="">{t('admin.allStatuses', 'All Statuses')}</option>
                <option value="PENDING">{t('profile.statusPending', 'PENDING')}</option>
                <option value="APPROVED">{t('profile.statusApproved', 'APPROVED')}</option>
                <option value="REJECTED">{t('profile.statusRejected', 'REJECTED')}</option>
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
                  setSearchParams({})
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
            <LoadingSpinner text={t('loading.loading', 'Loading beekeepers...')} />
          </div>
        ) : (
          <>
            <BeekeeperTable
              beekeepers={beekeepers}
              onStatusUpdate={handleStatusUpdate}
              updatingId={updatingId}
            />

            {totalPages > 1 && (
              <div className="flex items-center justify-center gap-3 pt-2">
                <button
                  type="button"
                  className="px-3 py-1.5 rounded-xl bg-white border border-slate-200 text-xs font-semibold text-slate-700 hover:bg-slate-50 disabled:opacity-40 cursor-pointer shadow-2xs transition-all"
                  disabled={page === 0}
                  onClick={() => loadBeekeepers(page - 1)}
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
                  onClick={() => loadBeekeepers(page + 1)}
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

export default AdminBeekeepersPage
