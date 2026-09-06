import React, { useState, useEffect } from 'react'
import AdminLayout from '../../../layouts/AdminLayout'
import DisputeTable from '../components/DisputeTable'
import LoadingSpinner from '../../../components/feedback/LoadingSpinner'
import Alert from '../../../components/feedback/Alert'
import adminApi from '../api/adminApi'
import { useLanguage } from '../../../i18n/LanguageContext'

export const AdminDisputesPage = () => {
  const { t } = useLanguage()
  const [disputes, setDisputes] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [statusFilter, setStatusFilter] = useState('')
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)

  // Dispute Management Modal / Selected dispute
  const [selectedDispute, setSelectedDispute] = useState(null)
  const [newStatus, setNewStatus] = useState('INVESTIGATING')
  const [resolutionNotes, setResolutionNotes] = useState('')
  const [updating, setUpdating] = useState(false)

  const loadDisputes = async (p = 0) => {
    setLoading(true)
    setError(null)
    try {
      const res = await adminApi.getDisputes({
        status: statusFilter || undefined,
        page: p,
        size: 20,
      })
      const data = res.data?.data
      setDisputes(data?.content || [])
      setTotalPages(data?.totalPages || 0)
      setPage(p)
    } catch (err) {
      setError(err?.response?.data?.message || t('errors.generic', 'Failed to load customer disputes'))
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadDisputes(0)
  }, [statusFilter])

  const handleUpdateStatus = async (e) => {
    e.preventDefault()
    if (!selectedDispute) return

    setUpdating(true)
    try {
      await adminApi.updateDisputeStatus(selectedDispute.id, {
        status: newStatus,
        resolutionNotes: resolutionNotes || undefined,
      })
      setSelectedDispute(null)
      setResolutionNotes('')
      await loadDisputes(page)
    } catch (err) {
      alert(err?.response?.data?.message || t('errors.generic', 'Failed to update dispute status'))
    } finally {
      setUpdating(false)
    }
  }

  return (
    <AdminLayout>
      <div className="space-y-6 text-left">
        {/* Executive Header Banner */}
        <div className="relative overflow-hidden rounded-2xl bg-gradient-to-br from-slate-900 via-slate-900 to-slate-950 text-white p-6 sm:p-7 shadow-md border border-slate-800 flex flex-col sm:flex-row sm:items-center justify-between gap-4">
          <div className="space-y-1.5 max-w-2xl">
            <div className="flex items-center gap-2 text-xs font-semibold text-amber-400 uppercase tracking-widest">
              <span>⚖️</span>
              <span>{t('admin.disputeManagement', 'Dispute Management')}</span>
            </div>
            <h1 className="text-2xl sm:text-3xl font-extrabold font-['Outfit'] tracking-tight text-white leading-tight">
              {t('admin.disputesTitle', 'Consumer Authenticity Disputes')}
            </h1>
            <p className="text-slate-300 text-xs sm:text-sm leading-relaxed">
              {t('admin.disputesSub', 'Investigate and resolve customer authenticity concerns and compromised product reports')}
            </p>
          </div>

          <button
            type="button"
            onClick={() => loadDisputes(page)}
            disabled={loading}
            className="self-start sm:self-center inline-flex items-center gap-2 px-3.5 py-2 rounded-xl bg-slate-800 hover:bg-slate-750 border border-slate-700 text-slate-200 text-xs font-semibold shadow-xs transition-all disabled:opacity-50 cursor-pointer"
          >
            <span>🔄</span>
            <span>{t('common.refresh', 'Refresh')}</span>
          </button>
        </div>

        {/* Filter Card */}
        <div className="bg-white rounded-2xl border border-slate-200/80 p-4 sm:p-5 shadow-xs flex flex-wrap gap-4 items-center">
          <label className="text-xs sm:text-sm font-bold text-slate-700">{t('admin.filterDisputeStatus', 'Filter by Dispute Status:')}</label>
          <select
            className="text-xs sm:text-sm py-2 px-3 bg-slate-50 border border-slate-200 rounded-xl text-slate-900 font-semibold cursor-pointer focus:outline-none focus:ring-2 focus:ring-amber-500/20 focus:border-amber-500 transition-all w-52"
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value)}
          >
            <option value="">{t('admin.allDisputes', 'All Disputes')}</option>
            <option value="OPEN">OPEN</option>
            <option value="INVESTIGATING">INVESTIGATING</option>
            <option value="RESOLVED">RESOLVED</option>
            <option value="REJECTED">REJECTED</option>
          </select>
        </div>

        {error && <Alert type="danger" message={error} />}

        {loading ? (
          <div className="py-16 text-center">
            <LoadingSpinner text={t('loading.loading', 'Loading disputes...')} />
          </div>
        ) : (
          <>
            <DisputeTable
              disputes={disputes}
              onSelectDispute={(d) => {
                setSelectedDispute(d)
                setNewStatus(d.status === 'OPEN' ? 'INVESTIGATING' : 'RESOLVED')
                setResolutionNotes(d.resolutionNotes || '')
              }}
            />

            {totalPages > 1 && (
              <div className="flex items-center justify-center gap-3 pt-2">
                <button
                  type="button"
                  className="px-3 py-1.5 rounded-xl bg-white border border-slate-200 text-xs font-semibold text-slate-700 hover:bg-slate-50 disabled:opacity-40 cursor-pointer shadow-2xs transition-all"
                  disabled={page === 0}
                  onClick={() => loadDisputes(page - 1)}
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
                  onClick={() => loadDisputes(page + 1)}
                >
                  {t('common.next', 'Next')} →
                </button>
              </div>
            )}
          </>
        )}

        {/* Dispute Resolution Card / Panel */}
        {selectedDispute && (
          <div className="bg-white rounded-2xl border-2 border-amber-400/80 p-6 shadow-md space-y-4">
            <div className="flex justify-between items-center pb-3 border-b border-slate-100">
              <h3 className="text-base font-extrabold text-slate-900 font-['Outfit']">
                {t('admin.manageDispute', 'Manage Dispute')} #{selectedDispute.id} ({t('batch.batchId', 'Batch')} <code className="text-amber-700 font-mono">{selectedDispute.batchId}</code>)
              </h3>
              <button
                type="button"
                className="px-2.5 py-1 rounded-lg text-slate-400 hover:text-slate-700 hover:bg-slate-100 text-xs font-bold transition-colors cursor-pointer"
                onClick={() => setSelectedDispute(null)}
              >
                ✕ {t('common.close', 'Close')}
              </button>
            </div>

            <div className="text-xs space-y-2 p-4 rounded-xl bg-slate-50 border border-slate-200/80">
              <p><strong className="text-slate-900">Reason:</strong> <span className="text-slate-700">{selectedDispute.reason}</span></p>
              {selectedDispute.description && <p><strong className="text-slate-900">Description:</strong> <span className="text-slate-700">{selectedDispute.description}</span></p>}
              <p><strong className="text-slate-900">Order Number:</strong> <code className="font-mono bg-white px-1.5 py-0.5 rounded border border-slate-200">{selectedDispute.orderNumber || 'N/A'}</code></p>
              <p><strong className="text-slate-900">Current Status:</strong> <span className="font-bold text-amber-700 uppercase ml-1">{selectedDispute.status}</span></p>
            </div>

            <form onSubmit={handleUpdateStatus} className="space-y-4">
              <div>
                <label className="block text-xs font-bold text-slate-700 mb-1.5">Set New Status</label>
                <select
                  className="w-full text-xs sm:text-sm py-2 px-3 bg-slate-50 border border-slate-200 rounded-xl text-slate-900 font-semibold cursor-pointer focus:outline-none focus:ring-2 focus:ring-amber-500/20 focus:border-amber-500 transition-all"
                  value={newStatus}
                  onChange={(e) => setNewStatus(e.target.value)}
                >
                  <option value="INVESTIGATING">INVESTIGATING</option>
                  <option value="RESOLVED">RESOLVED</option>
                  <option value="REJECTED">REJECTED</option>
                </select>
              </div>

              <div>
                <label className="block text-xs font-bold text-slate-700 mb-1.5">Resolution / Investigation Notes</label>
                <textarea
                  className="w-full text-xs sm:text-sm p-3 bg-slate-50 border border-slate-200 rounded-xl text-slate-900 placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-amber-500/20 focus:border-amber-500 transition-all"
                  rows={3}
                  placeholder="Describe resolution or investigation findings..."
                  value={resolutionNotes}
                  onChange={(e) => setResolutionNotes(e.target.value)}
                />
              </div>

              <div className="flex gap-3 pt-2">
                <button
                  type="submit"
                  className="px-4 py-2 rounded-xl bg-amber-500 hover:bg-amber-600 text-slate-950 text-xs sm:text-sm font-bold shadow-xs transition-colors disabled:opacity-50 cursor-pointer"
                  disabled={updating}
                >
                  {updating ? t('loading.submitting', 'Saving...') : t('common.save', 'Update Dispute Status')}
                </button>
                <button
                  type="button"
                  className="px-4 py-2 rounded-xl bg-slate-100 hover:bg-slate-200 text-slate-700 text-xs sm:text-sm font-semibold transition-colors cursor-pointer"
                  onClick={() => setSelectedDispute(null)}
                >
                  {t('common.cancel', 'Cancel')}
                </button>
              </div>
            </form>
          </div>
        )}
      </div>
    </AdminLayout>
  )
}

export default AdminDisputesPage
