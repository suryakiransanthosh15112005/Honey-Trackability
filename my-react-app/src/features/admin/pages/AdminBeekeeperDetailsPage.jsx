import React, { useState, useEffect } from 'react'
import { useParams, Link } from 'react-router-dom'
import AdminLayout from '../../../layouts/AdminLayout'
import LoadingSpinner from '../../../components/feedback/LoadingSpinner'
import Alert from '../../../components/feedback/Alert'
import adminApi from '../api/adminApi'
import { useLanguage } from '../../../i18n/LanguageContext'

export const AdminBeekeeperDetailsPage = () => {
  const { id } = useParams()
  const { t } = useLanguage()
  const [beekeeper, setBeekeeper] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [updating, setUpdating] = useState(false)

  const loadDetails = async () => {
    setLoading(true)
    setError(null)
    try {
      const res = await adminApi.getBeekeeperDetails(id)
      setBeekeeper(res.data?.data)
    } catch (err) {
      setError(err?.response?.data?.message || t('errors.generic', 'Failed to load beekeeper profile'))
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadDetails()
  }, [id])

  const handleStatusUpdate = async (newStatus) => {
    if (!window.confirm(t('dialog.confirmTitle', `Are you sure you want to mark this beekeeper as ${newStatus}?`))) return
    setUpdating(true)
    try {
      await adminApi.updateBeekeeperStatus(id, newStatus)
      await loadDetails()
    } catch (err) {
      alert(err?.response?.data?.message || t('errors.generic', 'Failed to update status'))
    } finally {
      setUpdating(false)
    }
  }

  if (loading) {
    return (
      <AdminLayout>
        <div className="py-20 text-center">
          <LoadingSpinner text={t('loading.loading', 'Loading beekeeper audit record...')} />
        </div>
      </AdminLayout>
    )
  }

  if (error || !beekeeper) {
    return (
      <AdminLayout>
        <div className="space-y-4 text-left">
          <Alert type="danger" message={error || 'Beekeeper not found'} />
          <Link to="/admin/beekeepers" className="inline-flex items-center gap-2 px-4 py-2 rounded-xl bg-slate-100 hover:bg-slate-200 text-slate-700 text-xs font-bold transition-colors">
            ← {t('common.back', 'Back to Beekeepers List')}
          </Link>
        </div>
      </AdminLayout>
    )
  }

  return (
    <AdminLayout>
      <div className="space-y-6 text-left">
        {/* Navigation Breadcrumb */}
        <nav className="flex items-center gap-2 text-xs font-semibold text-slate-500">
          <Link to="/admin/dashboard" className="hover:text-slate-900 transition-colors">{t('admin.controlCenter', 'Admin')}</Link>
          <span>/</span>
          <Link to="/admin/beekeepers" className="hover:text-slate-900 transition-colors">{t('nav.beekeepers', 'Beekeepers')}</Link>
          <span>/</span>
          <span className="text-slate-900 font-bold">{beekeeper.name}</span>
        </nav>

        {/* Beekeeper Profile Hero Card */}
        <div className="bg-white rounded-2xl border border-slate-200/80 p-6 shadow-xs flex items-start justify-between flex-wrap gap-4">
          <div className="flex items-center gap-4">
            {beekeeper.photoUrl ? (
              <img
                src={beekeeper.photoUrl}
                alt={beekeeper.name}
                className="w-16 h-16 rounded-2xl object-cover border-2 border-amber-400"
              />
            ) : (
              <div className="w-16 h-16 rounded-2xl bg-amber-50 border border-amber-300 flex items-center justify-center text-2xl text-amber-800 font-bold shrink-0">
                🧑‍🌾
              </div>
            )}
            <div>
              <h1 className="text-2xl font-extrabold text-slate-900 font-['Outfit']">{beekeeper.name}</h1>
              <p className="text-slate-500 text-xs font-medium">📍 {beekeeper.village || 'Region Unspecified'}</p>
              <div className="mt-2 flex items-center gap-2">
                <span className="px-2.5 py-0.5 rounded-lg bg-slate-100 border border-slate-200 text-xs font-mono font-bold text-slate-700">
                  KVIC ID: {beekeeper.kvicId || 'Pending'}
                </span>
                <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-bold ${
                  beekeeper.verificationStatus === 'APPROVED'
                    ? 'bg-emerald-50 text-emerald-700 border border-emerald-200'
                    : beekeeper.verificationStatus === 'REJECTED'
                    ? 'bg-red-50 text-red-700 border border-red-200'
                    : 'bg-amber-50 text-amber-800 border border-amber-200'
                }`}>
                  {beekeeper.verificationStatus}
                </span>
              </div>
            </div>
          </div>

          {beekeeper.verificationStatus === 'PENDING' && (
            <div className="flex items-center gap-2">
              <button
                type="button"
                className="px-4 py-2 rounded-xl bg-emerald-600 hover:bg-emerald-700 text-white text-xs font-bold shadow-xs transition-colors disabled:opacity-50 cursor-pointer"
                disabled={updating}
                onClick={() => handleStatusUpdate('APPROVED')}
              >
                {updating ? t('loading.submitting', 'Updating...') : `✓ ${t('admin.approve', 'Approve Beekeeper')}`}
              </button>
              <button
                type="button"
                className="px-4 py-2 rounded-xl bg-red-50 hover:bg-red-100 text-red-700 border border-red-200 text-xs font-bold transition-colors disabled:opacity-50 cursor-pointer"
                disabled={updating}
                onClick={() => handleStatusUpdate('REJECTED')}
              >
                {updating ? t('loading.submitting', 'Updating...') : `✕ ${t('admin.reject', 'Reject')}`}
              </button>
            </div>
          )}
        </div>

        {/* Apiary & Production Audit Grid */}
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5">
          <div className="bg-white rounded-2xl border border-slate-200/80 p-5 shadow-xs">
            <h3 className="text-xs font-bold text-slate-500 uppercase tracking-wider mb-2">🐝 {t('navigation.myHives', 'Apiary & Hives')}</h3>
            <p className="text-2xl font-extrabold text-slate-900 font-['Outfit']">{beekeeper.hiveCount}</p>
            <p className="text-slate-500 text-xs mt-1">Registered hives managed</p>
          </div>

          <div className="bg-white rounded-2xl border border-slate-200/80 p-5 shadow-xs">
            <h3 className="text-xs font-bold text-slate-500 uppercase tracking-wider mb-2">🍯 {t('navigation.batches', 'Batches Harvested')}</h3>
            <p className="text-2xl font-extrabold text-amber-700 font-['Outfit']">{beekeeper.batchCount}</p>
            <p className="text-slate-500 text-xs mt-1">Traceable honey batches produced</p>
          </div>

          <div className="bg-white rounded-2xl border border-slate-200/80 p-5 shadow-xs">
            <h3 className="text-xs font-bold text-slate-500 uppercase tracking-wider mb-2">🛒 {t('admin.activeProducts', 'Marketplace Listings')}</h3>
            <p className="text-2xl font-extrabold text-slate-900 font-['Outfit']">{beekeeper.productCount}</p>
            <p className="text-slate-500 text-xs mt-1">Listed honey products</p>
          </div>

          <div className="bg-white rounded-2xl border border-slate-200/80 p-5 shadow-xs">
            <h3 className="text-xs font-bold text-slate-500 uppercase tracking-wider mb-2">⭐ {t('navigation.reviews', 'Consumer Reputation')}</h3>
            <p className="text-2xl font-extrabold text-amber-600 font-['Outfit']">
              {beekeeper.averageRating > 0 ? `★ ${beekeeper.averageRating.toFixed(1)}` : 'No Reviews'}
            </p>
            <p className="text-slate-500 text-xs mt-1">Average consumer rating</p>
          </div>
        </div>

        {/* Verification Guidelines Card */}
        <div className="bg-white rounded-2xl border border-slate-200/80 p-6 shadow-xs space-y-3">
          <h3 className="text-sm font-extrabold text-slate-900 font-['Outfit']">📋 KVIC Verification Guidelines</h3>
          <ul className="text-slate-600 text-xs sm:text-sm space-y-2">
            <li>• Ensure the Beekeeper identity card and KVIC ID number match state honey registry.</li>
            <li>• Verify village GPS coordinates match registered apiary location.</li>
            <li>• Once approved, beekeeper can submit honey batches for lab testing and blockchain tokenization.</li>
          </ul>
        </div>
      </div>
    </AdminLayout>
  )
}

export default AdminBeekeeperDetailsPage
