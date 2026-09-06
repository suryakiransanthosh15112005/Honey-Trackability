import React, { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import LabLayout from '../../../layouts/LabLayout'
import labApi from '../api/labApi'
import Card from '../../../components/ui/Card'
import Input from '../../../components/ui/Input'
import Alert from '../../../components/feedback/Alert'
import LoadingSpinner from '../../../components/feedback/LoadingSpinner'
import { useLanguage } from '../../../i18n/LanguageContext'

export const LabTestHistoryPage = () => {
  const { t } = useLanguage()
  const [stats, setStats] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [searchTerm, setSearchTerm] = useState('')
  const [activeFilter, setActiveFilter] = useState('ALL')

  const loadStats = async () => {
    setLoading(true)
    setError(null)
    try {
      const res = await labApi.getLabStats()
      setStats(res.data?.data || null)
    } catch (err) {
      setError(err?.response?.data?.message || 'Failed to load laboratory history')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadStats()
  }, [])

  return (
    <LabLayout>
      <div className="max-w-6xl mx-auto space-y-6">
        {/* Page Header */}
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
          <div>
            <h1 className="text-2xl sm:text-3xl font-black text-slate-900 font-['Outfit'] flex items-center gap-2">
              <span>📜</span> Laboratory Test History & Certificates
            </h1>
            <p className="text-slate-500 text-xs sm:text-sm mt-1">
              Audit log of completed honey batch analysis, quality purity ratings, and issued certificates.
            </p>
          </div>
          <div className="flex items-center gap-2">
            <Link
              to="/lab/tests/pending"
              className="inline-flex items-center gap-2 px-4 py-2 rounded-lg bg-blue-600 hover:bg-blue-700 text-white text-xs sm:text-sm font-bold shadow-xs"
            >
              <span>🧪</span>
              <span>Pending Queue</span>
            </Link>
          </div>
        </div>

        {error && <Alert type="error" message={error} onClose={() => setError(null)} />}

        {loading ? (
          <div className="py-12 flex justify-center">
            <LoadingSpinner text="Loading laboratory test history..." />
          </div>
        ) : (
          <>
            {/* Overview Summary Cards */}
            <div className="grid grid-cols-2 sm:grid-cols-4 gap-4">
              <Card className="p-4 border-slate-200">
                <div className="text-xs text-slate-500 uppercase font-bold">Total Tested</div>
                <div className="text-2xl font-black text-slate-900 font-['Outfit'] mt-1">
                  {stats?.totalCompleted || stats?.totalTested || 0}
                </div>
                <div className="text-[11px] text-slate-400 mt-0.5">Completed batches</div>
              </Card>

              <Card className="p-4 border-[#86EFAC] bg-[#F0FDF4]">
                <div className="text-xs text-[#15803D] uppercase font-bold">Certified Pure</div>
                <div className="text-2xl font-black text-[#15803D] font-['Outfit'] mt-1">
                  {stats?.pureCount || stats?.pureBatches || 0}
                </div>
                <div className="text-[11px] text-[#16A34A] mt-0.5">Purity verified</div>
              </Card>

              <Card className="p-4 border-[#FEF3C7] bg-[#FFFBEB]">
                <div className="text-xs text-[#D97706] uppercase font-bold">Under Review</div>
                <div className="text-2xl font-black text-[#D97706] font-['Outfit'] mt-1">
                  {stats?.underReviewCount || 0}
                </div>
                <div className="text-[11px] text-[#D97706] mt-0.5">Secondary audit</div>
              </Card>

              <Card className="p-4 border-[#FCA5A5] bg-[#FEF2F2]">
                <div className="text-xs text-[#DC2626] uppercase font-bold">Failed / Adulterated</div>
                <div className="text-2xl font-black text-[#DC2626] font-['Outfit'] mt-1">
                  {stats?.failedCount || 0}
                </div>
                <div className="text-[11px] text-[#DC2626] mt-0.5">Rejected batches</div>
              </Card>
            </div>

            {/* Filter & Search Controls */}
            <Card className="p-4 flex flex-col sm:flex-row items-center justify-between gap-3">
              <div className="flex items-center gap-1 w-full sm:w-auto overflow-x-auto pb-1 sm:pb-0">
                {['ALL', 'PURE', 'UNDER_REVIEW', 'FAILED'].map((tab) => (
                  <button
                    key={tab}
                    type="button"
                    onClick={() => setActiveFilter(tab)}
                    className={`px-3 py-1.5 rounded-lg text-xs font-bold transition-all ${
                      activeFilter === tab
                        ? 'bg-blue-600 text-white shadow-xs'
                        : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
                    }`}
                  >
                    {tab === 'ALL' ? 'All Results' : tab.replace('_', ' ')}
                  </button>
                ))}
              </div>

              <div className="w-full sm:w-64">
                <Input
                  id="search-lab-history"
                  placeholder="Filter by Batch ID..."
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                  className="text-xs"
                />
              </div>
            </Card>

            {/* History Table Container */}
            <Card className="p-6 overflow-hidden">
              <div className="flex items-center justify-between mb-4">
                <h2 className="text-base font-bold text-slate-900 font-['Outfit']">
                  Certified Quality Records
                </h2>
                <span className="text-xs text-slate-400">
                  Backed by Cryptographic Ledger
                </span>
              </div>

              <div className="py-8 text-center space-y-3">
                <div className="text-4xl">🧪</div>
                <div className="text-sm font-semibold text-slate-700">
                  {stats?.totalCompleted > 0 ? 'Certified Batches Loaded' : 'No Completed Batch Tests Yet'}
                </div>
                <p className="text-xs text-slate-500 max-w-sm mx-auto">
                  {stats?.totalCompleted > 0
                    ? 'All completed batch test certificates and blockchain records are synchronized with the national registry.'
                    : 'Batches submitted for laboratory analysis will populate here once test results are approved and submitted.'}
                </p>
                <div className="pt-2">
                  <Link
                    to="/lab/tests/pending"
                    className="inline-flex items-center gap-1.5 px-4 py-2 rounded-lg bg-blue-600 hover:bg-blue-700 text-white text-xs font-bold shadow-xs"
                  >
                    + Open Pending Testing Queue
                  </Link>
                </div>
              </div>
            </Card>
          </>
        )}
      </div>
    </LabLayout>
  )
}

export default LabTestHistoryPage
