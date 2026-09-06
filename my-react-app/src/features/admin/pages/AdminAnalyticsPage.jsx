import React, { useState, useEffect } from 'react'
import AdminLayout from '../../../layouts/AdminLayout'
import LoadingSpinner from '../../../components/feedback/LoadingSpinner'
import Alert from '../../../components/feedback/Alert'
import adminApi from '../api/adminApi'
import { useLanguage } from '../../../i18n/LanguageContext'

export const AdminAnalyticsPage = () => {
  const { t } = useLanguage()
  const [regionalData, setRegionalData] = useState([])
  const [productionTrend, setProductionTrend] = useState([])
  const [salesData, setSalesData] = useState([])
  const [purityData, setPurityData] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
    Promise.all([
      adminApi.getRegionalAnalytics(),
      adminApi.getProductionTrend(),
      adminApi.getSalesAnalytics(),
      adminApi.getPurityAnalytics(),
    ])
      .then(([regRes, prodRes, salesRes, purityRes]) => {
        setRegionalData(regRes.data?.data || [])
        setProductionTrend(prodRes.data?.data || [])
        setSalesData(salesRes.data?.data || [])
        setPurityData(purityRes.data?.data)
      })
      .catch((err) => setError(err?.response?.data?.message || t('errors.generic', 'Failed to load analytics')))
      .finally(() => setLoading(false))
  }, [])

  return (
    <AdminLayout>
      <div className="space-y-6 text-left">
        {/* Executive Header Banner */}
        <div className="relative overflow-hidden rounded-2xl bg-gradient-to-br from-slate-900 via-slate-900 to-slate-950 text-white p-6 sm:p-7 shadow-md border border-slate-800 flex flex-col sm:flex-row sm:items-center justify-between gap-4">
          <div className="space-y-1.5 max-w-2xl">
            <div className="flex items-center gap-2 text-xs font-semibold text-amber-400 uppercase tracking-widest">
              <span>📈</span>
              <span>{t('admin.viewAnalytics', 'System Analytics')}</span>
            </div>
            <h1 className="text-2xl sm:text-3xl font-extrabold font-['Outfit'] tracking-tight text-white leading-tight">
              {t('navigation.analytics', 'Platform Analytics & Intelligence')}
            </h1>
            <p className="text-slate-300 text-xs sm:text-sm leading-relaxed">
              {t('admin.analyticsSub', 'Regional yield distribution, monthly honey harvests, sales trends, and certified purity metrics')}
            </p>
          </div>
        </div>

        {error && <Alert type="danger" message={error} />}

        {loading ? (
          <div className="py-16 text-center">
            <LoadingSpinner text={t('loading.loading', 'Crunching platform analytics...')} />
          </div>
        ) : (
          <div className="space-y-6">
            {/* Regional Production Table Card */}
            <div className="bg-white rounded-2xl border border-slate-200/80 shadow-xs overflow-hidden">
              <div className="p-5 border-b border-slate-100 flex items-center justify-between">
                <h2 className="text-base font-extrabold text-slate-900 font-['Outfit'] flex items-center gap-2">
                  <span>📍</span>
                  <span>{t('admin.regionalProduction', 'Regional Production & Purity Breakdown')}</span>
                </h2>
                <span className="text-xs font-bold text-slate-500 bg-slate-100 px-2.5 py-1 rounded-full">
                  {regionalData.length} {t('onboarding.village', 'Regions')}
                </span>
              </div>
              <div className="overflow-x-auto">
                <table className="w-full text-left border-collapse text-xs sm:text-sm">
                  <thead>
                    <tr className="bg-slate-50/90 border-b border-slate-200 text-[11px] font-bold text-slate-600 uppercase tracking-wider">
                      <th className="py-3.5 px-4">{t('onboarding.village', 'Region / Village')}</th>
                      <th className="py-3.5 px-4">{t('admin.navBeekeepers', 'Beekeepers')}</th>
                      <th className="py-3.5 px-4">{t('dashboard.registeredHives', 'Active Hives')}</th>
                      <th className="py-3.5 px-4">{t('navigation.batches', 'Batches')}</th>
                      <th className="py-3.5 px-4">{t('admin.honeyHarvested', 'Honey Produced (kg)')}</th>
                      <th className="py-3.5 px-4">{t('lab.purityScore', 'Avg Purity')}</th>
                      <th className="py-3.5 px-4 text-right">{t('admin.activeProducts', 'Marketplace Products')}</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-slate-100">
                    {regionalData.map((r) => (
                      <tr key={r.region} className="hover:bg-slate-50/70 transition-colors">
                        <td className="py-3.5 px-4 font-bold text-slate-900">{r.region}</td>
                        <td className="py-3.5 px-4 font-semibold text-slate-700">{r.beekeepers}</td>
                        <td className="py-3.5 px-4 font-semibold text-slate-700">{r.activeHives}</td>
                        <td className="py-3.5 px-4 font-semibold text-slate-700">{r.batches}</td>
                        <td className="py-3.5 px-4 font-extrabold text-amber-700">{r.honeyProducedKg} kg</td>
                        <td className="py-3.5 px-4">
                          <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-bold bg-emerald-50 text-emerald-700 border border-emerald-200">
                            {r.averagePurity}%
                          </span>
                        </td>
                        <td className="py-3.5 px-4 text-right font-bold text-slate-800">{r.products}</td>
                      </tr>
                    ))}
                    {regionalData.length === 0 && (
                      <tr>
                        <td colSpan="7" className="text-center py-8 text-slate-400">
                          {t('empty.noData', 'No regional harvest data available yet.')}
                        </td>
                      </tr>
                    )}
                  </tbody>
                </table>
              </div>
            </div>

            {/* Production & Sales Trends Grid */}
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
              {/* Monthly Production Trend */}
              <div className="bg-white rounded-2xl border border-slate-200/80 shadow-xs overflow-hidden">
                <div className="p-5 border-b border-slate-100">
                  <h3 className="text-sm font-extrabold text-slate-900 font-['Outfit'] flex items-center gap-2">
                    <span>🍯</span>
                    <span>{t('admin.monthlyHarvest', 'Monthly Honey Harvest Volume')}</span>
                  </h3>
                </div>
                <div className="overflow-x-auto">
                  <table className="w-full text-left border-collapse text-xs sm:text-sm">
                    <thead>
                      <tr className="bg-slate-50/90 border-b border-slate-200 text-[11px] font-bold text-slate-600 uppercase tracking-wider">
                        <th className="py-3 px-4">{t('common.date', 'Month')}</th>
                        <th className="py-3 px-4">{t('batch.quantityKg', 'Harvest Volume')}</th>
                        <th className="py-3 px-4 text-right">{t('navigation.batches', 'Batches')}</th>
                      </tr>
                    </thead>
                    <tbody className="divide-y divide-slate-100">
                      {productionTrend.map((p) => (
                        <tr key={p.month} className="hover:bg-slate-50/70 transition-colors">
                          <td className="py-3 px-4 font-mono font-bold text-slate-800">{p.month}</td>
                          <td className="py-3 px-4 font-extrabold text-amber-700">{p.quantityKg} kg</td>
                          <td className="py-3 px-4 text-right text-slate-600 font-medium">{p.batchCount} {t('navigation.batches', 'batches')}</td>
                        </tr>
                      ))}
                      {productionTrend.length === 0 && (
                        <tr>
                          <td colSpan="3" className="text-center text-slate-400 py-6">
                            {t('empty.noData', 'No harvest trend logged.')}
                          </td>
                        </tr>
                      )}
                    </tbody>
                  </table>
                </div>
              </div>

              {/* Monthly Orders & Revenue */}
              <div className="bg-white rounded-2xl border border-slate-200/80 shadow-xs overflow-hidden">
                <div className="p-5 border-b border-slate-100">
                  <h3 className="text-sm font-extrabold text-slate-900 font-['Outfit'] flex items-center gap-2">
                    <span>🛒</span>
                    <span>{t('admin.marketplaceSales', 'Marketplace Sales & Volume')}</span>
                  </h3>
                </div>
                <div className="overflow-x-auto">
                  <table className="w-full text-left border-collapse text-xs sm:text-sm">
                    <thead>
                      <tr className="bg-slate-50/90 border-b border-slate-200 text-[11px] font-bold text-slate-600 uppercase tracking-wider">
                        <th className="py-3 px-4">{t('common.date', 'Month')}</th>
                        <th className="py-3 px-4">{t('admin.ordersPlaced', 'Orders')}</th>
                        <th className="py-3 px-4">{t('admin.delivered', 'Delivered')}</th>
                        <th className="py-3 px-4 text-right">Revenue</th>
                      </tr>
                    </thead>
                    <tbody className="divide-y divide-slate-100">
                      {salesData.map((s) => (
                        <tr key={s.month} className="hover:bg-slate-50/70 transition-colors">
                          <td className="py-3 px-4 font-mono font-bold text-slate-800">{s.month}</td>
                          <td className="py-3 px-4 text-slate-700 font-semibold">{s.totalOrders}</td>
                          <td className="py-3 px-4 text-emerald-700 font-bold">{s.completedOrders}</td>
                          <td className="py-3 px-4 text-right font-extrabold text-amber-700">₹{s.totalRevenue.toFixed(2)}</td>
                        </tr>
                      ))}
                      {salesData.length === 0 && (
                        <tr>
                          <td colSpan="4" className="text-center text-slate-400 py-6">
                            {t('empty.noData', 'No sales records logged.')}
                          </td>
                        </tr>
                      )}
                    </tbody>
                  </table>
                </div>
              </div>
            </div>
          </div>
        )}
      </div>
    </AdminLayout>
  )
}

export default AdminAnalyticsPage
