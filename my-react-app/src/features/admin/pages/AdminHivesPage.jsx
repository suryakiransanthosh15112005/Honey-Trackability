import React, { useState, useEffect } from 'react'
import AdminLayout from '../../../layouts/AdminLayout'
import LoadingSpinner from '../../../components/feedback/LoadingSpinner'
import Alert from '../../../components/feedback/Alert'
import adminApi from '../api/adminApi'
import { useLanguage } from '../../../i18n/LanguageContext'

export const AdminHivesPage = () => {
  const { t } = useLanguage()
  const [hives, setHives] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [statusFilter, setStatusFilter] = useState('')
  const [searchQuery, setSearchQuery] = useState('')
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)

  const loadHives = async (p = 0) => {
    setLoading(true)
    setError(null)
    try {
      const res = await adminApi.getHives({
        status: statusFilter || undefined,
        search: searchQuery || undefined,
        page: p,
        size: 20,
      })
      const data = res.data?.data
      setHives(data?.content || [])
      setTotalPages(data?.totalPages || 0)
      setPage(p)
    } catch (err) {
      setError(err?.response?.data?.message || t('errors.generic', 'Failed to load hives'))
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadHives(0)
  }, [statusFilter])

  const handleSearch = (e) => {
    e.preventDefault()
    loadHives(0)
  }

  const getHealthBadge = (health) => {
    switch (health) {
      case 'HEALTHY':
        return (
          <span className="inline-flex items-center gap-1 px-2.5 py-1 rounded-full text-xs font-bold bg-emerald-50 text-emerald-700 border border-emerald-200">
            ✓ {t('iot.healthy', 'HEALTHY')}
          </span>
        )
      case 'WATCH':
        return (
          <span className="inline-flex items-center gap-1 px-2.5 py-1 rounded-full text-xs font-bold bg-amber-50 text-amber-800 border border-amber-300">
            ⚠️ {t('iot.watch', 'WATCH')}
          </span>
        )
      case 'ALERT':
        return (
          <span className="inline-flex items-center gap-1 px-2.5 py-1 rounded-full text-xs font-bold bg-red-50 text-red-700 border border-red-200">
            🚨 {t('iot.alert', 'ALERT')}
          </span>
        )
      default:
        return (
          <span className="inline-flex items-center gap-1 px-2.5 py-1 rounded-full text-xs font-bold bg-slate-100 text-slate-700 border border-slate-200">
            UNKNOWN
          </span>
        )
    }
  }

  return (
    <AdminLayout>
      <div className="space-y-6 text-left">
        {/* Executive Header Banner */}
        <div className="relative overflow-hidden rounded-2xl bg-gradient-to-br from-slate-900 via-slate-900 to-slate-950 text-white p-6 sm:p-7 shadow-md border border-slate-800 flex flex-col sm:flex-row sm:items-center justify-between gap-4">
          <div className="space-y-1.5 max-w-2xl">
            <div className="flex items-center gap-2 text-xs font-semibold text-amber-400 uppercase tracking-widest">
              <span>📡</span>
              <span>{t('admin.liveHiveTelemetry', 'Live IoT Hive Telemetry')}</span>
            </div>
            <h1 className="text-2xl sm:text-3xl font-extrabold font-['Outfit'] tracking-tight text-white leading-tight">
              {t('admin.hivesTitle', 'Apiary Hives & IoT Telemetry')}
            </h1>
            <p className="text-slate-300 text-xs sm:text-sm leading-relaxed">
              {t('admin.hivesSub', 'Monitor hive status, IoT temperature, humidity, bee activity, and colony health')}
            </p>
          </div>

          <button
            type="button"
            onClick={() => loadHives(page)}
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
                placeholder={t('admin.searchHivePlaceholder', 'Search by Hive Code (e.g. HIV-2026-001) or Cluster...')}
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
              />
            </div>

            <div className="w-full sm:w-48">
              <select
                className="w-full text-xs sm:text-sm py-2 px-3 bg-slate-50 border border-slate-200 rounded-xl text-slate-900 font-semibold cursor-pointer focus:outline-none focus:ring-2 focus:ring-amber-500/20 focus:border-amber-500 transition-all"
                value={statusFilter}
                onChange={(e) => setStatusFilter(e.target.value)}
              >
                <option value="">{t('admin.allStatuses', 'All Statuses')}</option>
                <option value="ACTIVE">{t('hive.active', 'ACTIVE')}</option>
                <option value="ALERT">{t('hive.alert', 'ALERT')}</option>
                <option value="INACTIVE">{t('hive.inactive', 'INACTIVE')}</option>
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
            <LoadingSpinner text={t('loading.loading', 'Loading hives and IoT telemetry...')} />
          </div>
        ) : (
          <div className="overflow-x-auto bg-white rounded-2xl border border-slate-200/80 shadow-xs">
            <table className="w-full text-left border-collapse text-xs sm:text-sm">
              <thead>
                <tr className="bg-slate-50/90 border-b border-slate-200 text-[11px] font-bold text-slate-600 uppercase tracking-wider">
                  <th className="py-3.5 px-4">{t('hive.hiveCode', 'Hive Code')}</th>
                  <th className="py-3.5 px-4">{t('auth.beekeeperRole', 'Beekeeper')}</th>
                  <th className="py-3.5 px-4">{t('hive.clusterName', 'Cluster')}</th>
                  <th className="py-3.5 px-4">{t('common.status', 'Status')}</th>
                  <th className="py-3.5 px-4">{t('admin.iotHealth', 'IoT Health')}</th>
                  <th className="py-3.5 px-4">{t('iot.temp', 'Temperature')}</th>
                  <th className="py-3.5 px-4">{t('iot.humidity', 'Humidity')}</th>
                  <th className="py-3.5 px-4">{t('iot.activity', 'Bee Activity')}</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {hives.map((h) => (
                  <tr key={h.id} className="hover:bg-slate-50/70 transition-colors">
                    <td className="py-3.5 px-4 font-mono font-bold text-slate-900">
                      <code className="px-2 py-0.5 rounded bg-slate-100 border border-slate-200 text-xs">
                        {h.hiveCode}
                      </code>
                    </td>
                    <td className="py-3.5 px-4">
                      <strong className="block text-slate-900 font-bold">{h.beekeeperName || 'N/A'}</strong>
                      <span className="text-slate-500 text-xs">{h.village}</span>
                    </td>
                    <td className="py-3.5 px-4 text-slate-700 font-medium">{h.clusterName || 'N/A'}</td>
                    <td className="py-3.5 px-4">
                      <span className={`inline-flex items-center px-2.5 py-1 rounded-full text-xs font-bold ${
                        h.status === 'ACTIVE'
                          ? 'bg-emerald-50 text-emerald-700 border border-emerald-200'
                          : 'bg-amber-50 text-amber-800 border border-amber-200'
                      }`}>
                        {h.status}
                      </span>
                    </td>
                    <td className="py-3.5 px-4">{getHealthBadge(h.healthStatus)}</td>
                    <td className="py-3.5 px-4 font-extrabold text-slate-900">{h.lastTemperature != null ? `${h.lastTemperature.toFixed(1)}°C` : 'N/A'}</td>
                    <td className="py-3.5 px-4 font-extrabold text-slate-900">{h.lastHumidity != null ? `${h.lastHumidity.toFixed(0)}%` : 'N/A'}</td>
                    <td className="py-3.5 px-4 font-mono font-bold text-slate-700">{h.lastBeeActivity != null ? `${h.lastBeeActivity} / 100` : 'N/A'}</td>
                  </tr>
                ))}
                {hives.length === 0 && (
                  <tr>
                    <td colSpan="8" className="py-8 text-center text-slate-400">
                      {t('empty.noHives', 'No hives registered yet.')}
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

export default AdminHivesPage
