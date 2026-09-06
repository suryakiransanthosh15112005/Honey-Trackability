import React, { useState, useEffect, useCallback } from 'react'
import { useParams, Link } from 'react-router-dom'
import BeekeeperLayout from '../../../layouts/BeekeeperLayout'
import Card from '../../../components/ui/Card'
import Button from '../../../components/ui/Button'
import Alert from '../../../components/feedback/Alert'
import LoadingSpinner from '../../../components/feedback/LoadingSpinner'
import HealthStatusBadge from '../components/HealthStatusBadge'
import SensorSummary from '../components/SensorSummary'
import SensorHistoryChart from '../components/SensorHistoryChart'
import YieldPredictionCard from '../../ai/components/YieldPredictionCard'
import useYieldPrediction from '../../ai/hooks/useYieldPrediction'
import iotApi from '../api/iotApi'

export const HiveHealthDetailsPage = () => {
  const { hiveId } = useParams()
  const [health, setHealth] = useState(null)
  const [history, setHistory] = useState([])
  const [loading, setLoading] = useState(true)
  const [refreshing, setRefreshing] = useState(false)
  const [error, setError] = useState(null)

  const {
    prediction,
    loading: predictionLoading,
    refreshing: predictionRefreshing,
    refreshPrediction,
  } = useYieldPrediction(hiveId)

  const loadData = useCallback(async (isRefresh = false) => {
    try {
      if (isRefresh) setRefreshing(true)
      else setLoading(true)
      setError(null)

      const [healthRes, historyRes] = await Promise.all([
        iotApi.getHiveHealth(hiveId),
        iotApi.getSensorHistory(hiveId, 0, 20),
      ])

      setHealth(healthRes.data.data)
      setHistory(historyRes.data.data?.readings || [])
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to load hive health telemetry.')
    } finally {
      setLoading(false)
      setRefreshing(false)
    }
  }, [hiveId])

  useEffect(() => {
    loadData()
  }, [loadData])

  return (
    <BeekeeperLayout>
      <div className="w-full space-y-6">
        {/* Breadcrumb */}
        <nav className="flex items-center gap-2 text-xs text-slate-500">
          <Link to="/beekeeper/hives" className="hover:text-blue-600 transition-colors font-medium">
            My Hives
          </Link>
          <span>/</span>
          <span className="text-slate-800 font-mono font-semibold">{health?.hiveCode || `Hive #${hiveId}`}</span>
        </nav>

        {error && <Alert type="error" message={error} />}

        {loading && !health ? (
          <div className="py-20 text-center">
            <LoadingSpinner text="Reading IoT sensor stream and calculating health state..." />
          </div>
        ) : !health ? (
          <Card className="p-12 text-center max-w-xl mx-auto">
            <p className="text-3xl mb-2">🐝</p>
            <p className="text-slate-900 font-bold">Hive Telemetry Not Available</p>
            <Link to="/beekeeper/hives">
              <Button variant="secondary" size="sm" className="mt-4">
                ← Back to Hives
              </Button>
            </Link>
          </Card>
        ) : (
          <div className="space-y-6">
            {/* Main Health Status Overview Card */}
            <Card
              className={`p-6 space-y-4 border ${health.status === 'ALERT'
                  ? 'border-blue-300 bg-blue-50/40 text-blue-950 shadow-sm'
                  : health.status === 'WATCH'
                    ? 'border-amber-300 bg-amber-50/40 text-amber-950 shadow-sm'
                    : 'border-slate-200 bg-white text-slate-900 shadow-sm'
                }`}
            >
              <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
                <div className="flex items-center gap-3">
                  <div className="w-14 h-14 rounded-2xl bg-amber-100 border border-amber-200 flex items-center justify-center text-3xl shadow-sm">
                    🐝
                  </div>
                  <div>
                    <h1 className="text-2xl font-black text-slate-900 font-['Outfit'] tracking-tight">
                      {health.hiveCode || `Hive #${hiveId}`}
                    </h1>
                    <p className="text-xs text-slate-500 flex items-center gap-2 mt-0.5">
                      <span>{health.clusterName ? `Cluster: ${health.clusterName}` : 'Registered Apiary'}</span>
                      <span className="badge badge--warning text-[10px] font-mono">Simulated IoT Stream</span>
                    </p>
                  </div>
                </div>

                <div className="flex items-center gap-3">
                  <HealthStatusBadge status={health.status} size="lg" />
                  <Button
                    variant="secondary"
                    size="sm"
                    onClick={() => loadData(true)}
                    loading={refreshing}
                  >
                    ↻ Live Telemetry
                  </Button>
                </div>
              </div>

              {/* Explainable Health Message */}
              <div className="p-4 rounded-xl bg-slate-50 border border-slate-200 space-y-1">
                <p className="text-xs text-slate-500 font-medium uppercase tracking-wider">
                  Health Analysis & Diagnostic Insight
                </p>
                <p className="text-base font-medium text-slate-900 leading-relaxed">
                  {health.message}
                </p>
                <p className="text-[11px] text-slate-500 pt-1">
                  * Based on deterministic environmental thresholds for temperature, humidity, and acoustic bee activity.
                </p>
              </div>
            </Card>

            {/* Live Sensor Telemetry Readout */}
            <div className="space-y-3">
              <h2 className="text-lg font-bold text-slate-900 font-['Outfit'] flex items-center gap-2">
                <span>📊</span> Live Sensor Telemetry
              </h2>
              <SensorSummary
                temperature={health.temperature}
                humidity={health.humidity}
                beeActivity={health.beeActivity}
                checkedAt={health.checkedAt}
              />
            </div>

            {/* Multi-Column Section: Historical Chart & AI Prediction */}
            <div className="grid grid-cols-1 lg:grid-cols-12 gap-6 items-start">
              {/* Historical Sensor Chart */}
              <div className="lg:col-span-7 xl:col-span-8">
                <Card className="p-6 space-y-4 bg-white border border-slate-200/90 shadow-sm">
                  <div className="flex items-center justify-between">
                    <h2 className="text-lg font-bold text-slate-900 font-['Outfit'] flex items-center gap-2">
                      <span>📈</span> Sensor Trends & Historical Telemetry
                    </h2>
                    <span className="text-xs text-slate-500 font-mono">
                      {history.length} data points
                    </span>
                  </div>
                  <SensorHistoryChart readings={history} />
                </Card>
              </div>

              {/* AI Yield Prediction Section (Phase 11) */}
              <div className="lg:col-span-5 xl:col-span-4">
                <YieldPredictionCard
                  prediction={prediction}
                  loading={predictionLoading}
                  refreshing={predictionRefreshing}
                  onRefresh={refreshPrediction}
                />
              </div>
            </div>
          </div>
        )}
      </div>
    </BeekeeperLayout>
  )
}

export default HiveHealthDetailsPage
