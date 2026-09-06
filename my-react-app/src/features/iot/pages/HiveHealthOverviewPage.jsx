import React from 'react'
import { Link } from 'react-router-dom'
import BeekeeperLayout from '../../../layouts/BeekeeperLayout'
import HiveHealthGrid from '../components/HiveHealthGrid'
import Button from '../../../components/ui/Button'
import Alert from '../../../components/feedback/Alert'
import LoadingSpinner from '../../../components/feedback/LoadingSpinner'
import { useHiveHealth } from '../hooks/useHiveHealth'
import { useLanguage } from '../../../i18n/LanguageContext'
import VoiceButton from '../../../components/common/VoiceButton'

export const HiveHealthOverviewPage = () => {
  const { t } = useLanguage()
  const { data: hivesHealth, loading, error, refresh } = useHiveHealth(null, 45000) // 45s auto-refresh

  const healthyCount = hivesHealth?.filter((h) => h.status === 'HEALTHY').length || 0
  const watchCount = hivesHealth?.filter((h) => h.status === 'WATCH').length || 0
  const alertCount = hivesHealth?.filter((h) => h.status === 'ALERT').length || 0

  const voiceInstructions = `${t('iot.dashboardTitle', 'IoT Sensor Telemetry')}. ${t('iot.msgHealthy', 'Hive telemetry is within optimal parameters.')} ${healthyCount} ${t('iot.healthy', 'Healthy')}, ${watchCount} ${t('iot.watch', 'Watch')}, ${alertCount} ${t('iot.alert', 'Alert')}.`

  return (
    <BeekeeperLayout>
      <div className="max-w-6xl mx-auto space-y-6">
        {/* Page Header */}
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
          <div>
            <div className="flex items-center gap-3">
              <h1 className="text-2xl font-black text-slate-900 font-['Outfit'] tracking-tight flex items-center gap-2.5">
                <span>📡</span> {t('iot.dashboardTitle', 'IoT Hive Health Monitoring')}
              </h1>
              <VoiceButton textToSpeak={voiceInstructions} size="sm" />
              <span className="badge badge--warning text-[11px] font-mono">
                📡 Simulated Sensor Telemetry
              </span>
            </div>
            <p className="text-xs text-slate-500 mt-1">
              {t('dashboard.iotSub', 'Real-time telemetry and explainable health analysis for all your registered apiary hives.')} (Simulation Engine)
            </p>
          </div>
          <div className="flex items-center gap-3">
            <Button variant="secondary" size="sm" onClick={refresh} loading={loading}>
              ↻ {t('common.refresh', 'Refresh')}
            </Button>
            <Link to="/beekeeper/hives">
              <Button variant="primary" size="sm">
                + {t('dashboard.manageHives', 'Manage Hives')}
              </Button>
            </Link>
          </div>
        </div>

        {error && <Alert type="error" message={error} />}

        {/* Health Summary Stat Cards */}
        <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
          {/* Healthy Card */}
          <div className="p-4 rounded-2xl bg-blue-50 border border-blue-200 flex items-center justify-between shadow-sm">
            <div className="space-y-0.5">
              <p className="text-xs font-semibold text-blue-700 font-['Outfit'] uppercase tracking-wider">
                🟢 Healthy Hives
              </p>
              <p className="text-2xl font-black text-slate-900 font-mono">{healthyCount}</p>
            </div>
            <span className="text-3xl">🌿</span>
          </div>

          {/* Watch Card */}
          <div className="p-4 rounded-2xl bg-amber-50 border border-amber-200 flex items-center justify-between shadow-sm">
            <div className="space-y-0.5">
              <p className="text-xs font-semibold text-amber-800 font-['Outfit'] uppercase tracking-wider">
                🟡 Watch Attention
              </p>
              <p className="text-2xl font-black text-slate-900 font-mono">{watchCount}</p>
            </div>
            <span className="text-3xl">⚠️</span>
          </div>

          {/* Alert Card */}
          <div className="p-4 rounded-2xl bg-blue-100/60 border border-blue-300 flex items-center justify-between shadow-sm">
            <div className="space-y-0.5">
              <p className="text-xs font-semibold text-blue-900 font-['Outfit'] uppercase tracking-wider">
                🔵 Alert Required
              </p>
              <p className="text-2xl font-black text-slate-900 font-mono">{alertCount}</p>
            </div>
            <span className="text-3xl">📡</span>
          </div>
        </div>

        {/* Grid of Hives */}
        {loading && !hivesHealth ? (
          <div className="py-20 text-center">
            <LoadingSpinner text="Connecting to IoT telemetry streams and evaluating hive health..." />
          </div>
        ) : (
          <HiveHealthGrid hivesHealth={hivesHealth} />
        )}
      </div>
    </BeekeeperLayout>
  )
}

export default HiveHealthOverviewPage
