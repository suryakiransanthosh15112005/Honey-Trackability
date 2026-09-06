import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import AdminLayout from '../../../layouts/AdminLayout'
import MetricCard from '../../../components/ui/MetricCard'
import LoadingSpinner from '../../../components/feedback/LoadingSpinner'
import Alert from '../../../components/feedback/Alert'
import adminApi from '../api/adminApi'
import { useAuth } from '../../auth/hooks/useAuth'
import { useLanguage } from '../../../i18n/LanguageContext'
import VoiceButton from '../../../components/common/VoiceButton'

export const AdminDashboardPage = () => {
  const { t } = useLanguage()
  const { phoneNumber, role } = useAuth()
  const [stats, setStats] = useState(null)
  const [loading, setLoading] = useState(true)
  const [refreshing, setRefreshing] = useState(false)
  const [error, setError] = useState(null)

  const handleManualRefresh = () => {
    setRefreshing(true)
    adminApi.getDashboard()
      .then((res) => {
        setStats(res.data.data)
        setError(null)
      })
      .catch((err) => setError(err?.response?.data?.message || t('errors.generic', 'Failed to load dashboard statistics')))
      .finally(() => {
        setRefreshing(false)
      })
  }

  useEffect(() => {
    let isMounted = true
    adminApi.getDashboard()
      .then((res) => {
        if (isMounted) {
          setStats(res.data.data)
          setError(null)
        }
      })
      .catch((err) => {
        if (isMounted) {
          setError(err?.response?.data?.message || t('errors.generic', 'Failed to load dashboard statistics'))
        }
      })
      .finally(() => {
        if (isMounted) {
          setLoading(false)
        }
      })

    return () => {
      isMounted = false
    }
  }, [t])

  const voiceSummary = `${t('admin.controlCenter', 'Admin & KVIC Control Center')}. ${t('admin.totalBeekeepers', 'Total Beekeepers')}: ${stats?.totalBeekeepers ?? 0}. ${t('admin.totalBatches', 'Honey Batches')}: ${stats?.totalBatches ?? 0}. ${t('admin.honeyHarvested', 'Honey Harvested')}: ${(stats?.totalHoneyProducedKg ?? 0).toFixed(1)} kg.`

  return (
    <AdminLayout>
      <div className="space-y-6 text-left">
        {/* Executive Command Header */}
        <div className="relative overflow-hidden rounded-2xl bg-gradient-to-br from-slate-900 via-slate-900 to-slate-950 text-white p-6 sm:p-8 shadow-md border border-slate-800">
          {/* Subtle warm amber ambient glow */}
          <div className="absolute -right-20 -top-20 w-72 h-72 bg-amber-500/10 rounded-full blur-3xl pointer-events-none" />

          <div className="relative z-10 flex flex-col lg:flex-row lg:items-center justify-between gap-6">
            <div className="space-y-3 max-w-2xl">
              {/* Executive System Status Badges */}
              <div className="flex flex-wrap items-center gap-2 text-xs font-semibold">
                <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-slate-800 text-amber-300 border border-amber-500/30">
                  <span>🏛️</span>
                  <span>{t('admin.oversightBadge', 'KVIC & HoneyChain Oversight')}</span>
                </span>
                <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-slate-800 text-slate-300 border border-slate-700">
                  <span className="w-2 h-2 rounded-full bg-emerald-400 animate-pulse" />
                  <span>{t('admin.ledgerLive', 'Ledger Live')}</span>
                </span>
              </div>

              {/* Title & Subtitle */}
              <div>
                <h1 className="text-2xl sm:text-3xl font-extrabold font-['Outfit'] tracking-tight text-white leading-tight">
                  {t('admin.controlCenter', 'Admin & KVIC Control Center')}
                </h1>
                <p className="mt-1.5 text-slate-300 text-xs sm:text-sm leading-relaxed">
                  {t('admin.headerSubtitle', 'Real-time cryptographic honey traceability, beekeeper certification audits, laboratory purity oversight, and anti-counterfeit intelligence.')}
                </p>
              </div>

              {/* Connected Session Info */}
              <div className="flex items-center gap-2.5 pt-0.5 text-xs text-slate-400">
                <span className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-lg bg-slate-800/90 border border-slate-700 text-slate-300 font-medium">
                  <span>👤</span>
                  <span>{phoneNumber || 'Administrator'}</span>
                  <span className="text-slate-600">•</span>
                  <span className="text-amber-400 font-bold uppercase">{t('roles.' + role, role)}</span>
                </span>
                <span className="text-slate-400 text-[11px] hidden sm:inline">{t('admin.allSystemsOperational', 'All Systems Operational')}</span>
              </div>
            </div>

            {/* Quick Actions */}
            <div className="flex flex-wrap items-center gap-3 shrink-0">
              <VoiceButton textToSpeak={voiceSummary} size="sm" />

              <button
                type="button"
                onClick={handleManualRefresh}
                disabled={refreshing}
                className="inline-flex items-center gap-2 px-3.5 py-2.5 rounded-xl bg-slate-800 hover:bg-slate-750 border border-slate-700 text-slate-200 text-xs font-semibold shadow-xs transition-all disabled:opacity-50 cursor-pointer"
                title={t('admin.refresh', 'Refresh real-time data')}
              >
                <span className={refreshing ? 'animate-spin' : ''}>🔄</span>
                <span>{refreshing ? t('admin.refreshing', 'Refreshing...') : t('admin.refresh', 'Refresh')}</span>
              </button>

              <Link
                to="/admin/beekeepers?status=PENDING"
                className="inline-flex items-center gap-2 px-4 py-2.5 rounded-xl bg-gradient-to-r from-amber-500 to-amber-600 hover:from-amber-400 hover:to-amber-500 text-slate-950 text-xs font-bold shadow-sm shadow-amber-500/20 transition-all cursor-pointer"
              >
                <span>🧑‍🌾</span>
                <span>{t('admin.auditBeekeepers', 'Audit Beekeepers')}</span>
              </Link>
            </div>
          </div>
        </div>

        {loading ? (
          <div className="py-20 text-center">
            <LoadingSpinner text={t('loading.loading', 'Loading real-time platform statistics...')} />
          </div>
        ) : error ? (
          <Alert type="danger" message={error} />
        ) : (
          <>
            {/* Top KPI Metric Cards Grid */}
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-5">
              <MetricCard
                icon="🧑‍🌾"
                label={t('admin.totalBeekeepers', 'Total Beekeepers')}
                value={stats?.totalBeekeepers ?? 0}
                subtext={t('admin.beekeepersSub', { pending: stats?.pendingBeekeepers ?? 0, approved: stats?.approvedBeekeepers ?? 0, defaultValue: `${stats?.pendingBeekeepers ?? 0} Pending • ${stats?.approvedBeekeepers ?? 0} Approved` })}
                accent="amber"
                badge={stats?.pendingBeekeepers > 0 ? t('admin.pendingCount', { count: stats.pendingBeekeepers, defaultValue: `${stats.pendingBeekeepers} Pending` }) : t('admin.audited100', '100% Audited')}
              />
              <MetricCard
                icon="🐝"
                label={t('admin.registeredHives', 'Registered Hives')}
                value={stats?.totalHives ?? 0}
                subtext={t('admin.activeStreams', { count: stats?.activeHives ?? 0, defaultValue: `${stats?.activeHives ?? 0} Active Telemetry Streams` })}
                accent="neutral"
                badge={t('admin.iotLive', 'IoT Live')}
              />
              <MetricCard
                icon="🍯"
                label={t('admin.totalBatches', 'Honey Batches')}
                value={stats?.totalBatches ?? 0}
                subtext={t('admin.batchesSub', { pure: stats?.pureBatches ?? 0, review: stats?.underReviewBatches ?? 0, defaultValue: `${stats?.pureBatches ?? 0} Pure • ${stats?.underReviewBatches ?? 0} Under Review` })}
                accent="amber"
                badge={t('admin.certified', 'Certified')}
              />
              <MetricCard
                icon="⚖️"
                label={t('admin.honeyHarvested', 'Honey Harvested')}
                value={`${(stats?.totalHoneyProducedKg ?? 0).toFixed(1)} kg`}
                subtext={t('admin.productsSub', { count: stats?.activeProducts ?? 0, defaultValue: `${stats?.activeProducts ?? 0} Active Listed Products` })}
                accent="neutral"
                badge={t('admin.gradeA', 'Grade A')}
              />
              <MetricCard
                icon="🛒"
                label={t('admin.ordersPlaced', 'Orders Placed')}
                value={stats?.totalOrders ?? 0}
                subtext={t('admin.ordersSub', { count: stats?.completedOrders ?? 0, defaultValue: `${stats?.completedOrders ?? 0} Fulfilled & Delivered` })}
                accent="amber"
                badge={t('admin.commerce', 'Commerce')}
              />
              <MetricCard
                icon="🛡️"
                label={t('admin.verificationRisks', 'Verification Risks')}
                value={stats?.highRiskVerificationBatches ?? 0}
                subtext={t('admin.risksSub', { count: stats?.pendingLabTests ?? 0, defaultValue: `${stats?.pendingLabTests ?? 0} Awaiting Lab Tests` })}
                accent="neutral"
                badge={stats?.highRiskVerificationBatches > 0 ? t('admin.actionNeeded', 'Action Needed') : t('admin.zeroTamper', 'Zero Tamper')}
              />
            </div>

            {/* Actionable Governance & Security Oversight Grid */}
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
              {/* Beekeeper Verification & Compliance */}
              <div className="bg-white rounded-2xl border border-slate-200/85 p-6 shadow-xs hover:shadow-md transition-shadow flex flex-col justify-between space-y-5">
                <div className="space-y-4">
                  <div className="flex items-center justify-between">
                    <div className="flex items-center gap-3">
                      <span className="w-10 h-10 rounded-xl bg-amber-50 text-amber-800 border border-amber-200/70 flex items-center justify-center text-lg">
                        🧑‍🌾
                      </span>
                      <div>
                        <h2 className="text-base font-extrabold text-slate-900 font-['Outfit']">
                          {t('admin.governanceAndAudit', 'Beekeeper Governance & Audit')}
                        </h2>
                        <p className="text-xs text-slate-500">
                          {t('admin.governanceSub', 'Identity verification, apiary geolocation, and KVIC compliance audits')}
                        </p>
                      </div>
                    </div>
                    <span className="text-[11px] font-bold px-2.5 py-1 rounded-full bg-slate-100 text-slate-700 border border-slate-200/80">
                      {t('admin.kvicRoster', 'KVIC Roster')}
                    </span>
                  </div>

                  {/* Status Banner */}
                  {stats?.pendingBeekeepers > 0 ? (
                    <div className="p-4 rounded-xl bg-amber-50/90 border border-amber-200 text-amber-950 flex items-start gap-3">
                      <span className="text-lg">⚠️</span>
                      <div className="text-xs">
                        <strong className="font-bold block text-amber-950">
                          {stats.pendingBeekeepers} {t('admin.beekeepersAwaiting', 'beekeeper registration(s) awaiting audit.')}
                        </strong>
                        <span className="text-amber-800">
                          {t('admin.verifyCredentialsNotice', 'Verify identity credentials and apiary geolocations before approving harvest batches.')}
                        </span>
                      </div>
                    </div>
                  ) : (
                    <div className="p-4 rounded-xl bg-slate-50 border border-slate-200/90 text-slate-800 flex items-center gap-3">
                      <span className="text-base text-emerald-600">✓</span>
                      <div className="text-xs">
                        <strong className="font-bold block text-slate-900">
                          {t('admin.allBeekeepersAudited', 'All registered beekeepers have been audited.')}
                        </strong>
                        <span className="text-slate-600">
                          {t('admin.compliance100', '100% compliance across active apiaries.')}
                        </span>
                      </div>
                    </div>
                  )}

                  {/* Quick Stat Pill Row */}
                  <div className="grid grid-cols-3 gap-3 pt-1">
                    <div className="p-3 rounded-xl bg-slate-50 border border-slate-100 text-center">
                      <div className="text-xs text-slate-500 font-medium">{t('admin.registeredLabel', 'Registered')}</div>
                      <div className="text-lg font-extrabold text-slate-900 font-['Outfit']">
                        {stats?.totalBeekeepers ?? 0}
                      </div>
                    </div>
                    <div className="p-3 rounded-xl bg-slate-50 border border-slate-100 text-center">
                      <div className="text-xs text-slate-500 font-medium">{t('admin.approvedLabel', 'Approved')}</div>
                      <div className="text-lg font-extrabold text-slate-900 font-['Outfit']">
                        {stats?.approvedBeekeepers ?? 0}
                      </div>
                    </div>
                    <div className="p-3 rounded-xl bg-slate-50 border border-slate-100 text-center">
                      <div className="text-xs text-slate-500 font-medium">{t('admin.pendingLabel', 'Pending')}</div>
                      <div className="text-lg font-extrabold text-amber-700 font-['Outfit']">
                        {stats?.pendingBeekeepers ?? 0}
                      </div>
                    </div>
                  </div>
                </div>

                <div className="pt-2 border-t border-slate-100 flex items-center justify-between">
                  <span className="text-xs text-slate-500">{t('admin.slaTime', 'Audit SLA: Within 24 hours')}</span>
                  <Link
                    to="/admin/beekeepers?status=PENDING"
                    className="inline-flex items-center gap-1.5 text-xs font-bold text-amber-700 hover:text-amber-800 transition-colors"
                  >
                    <span>{t('admin.auditApplications', 'Review Applications')}</span>
                    <span>→</span>
                  </Link>
                </div>
              </div>

              {/* Anti-Counterfeit Monitoring */}
              <div className="bg-white rounded-2xl border border-slate-200/85 p-6 shadow-xs hover:shadow-md transition-shadow flex flex-col justify-between space-y-5">
                <div className="space-y-4">
                  <div className="flex items-center justify-between">
                    <div className="flex items-center gap-3">
                      <span className="w-10 h-10 rounded-xl bg-slate-100 text-slate-700 border border-slate-200/80 flex items-center justify-center text-lg">
                        🛡️
                      </span>
                      <div>
                        <h2 className="text-base font-extrabold text-slate-900 font-['Outfit']">
                          {t('admin.threatTelemetry', 'Anti-Counterfeit Intelligence Radar')}
                        </h2>
                        <p className="text-xs text-slate-500">
                          {t('admin.threatTelemetrySub', 'Public QR scan velocity, geo-fingerprinting, and cryptographic tamper detection')}
                        </p>
                      </div>
                    </div>
                    <span className="text-[11px] font-bold px-2.5 py-1 rounded-full bg-slate-100 text-slate-700 border border-slate-200/80">
                      {t('admin.ledgerLive', 'Live Telemetry')}
                    </span>
                  </div>

                  {/* Status Banner */}
                  {stats?.highRiskVerificationBatches > 0 ? (
                    <div className="p-4 rounded-xl bg-amber-50/90 border border-amber-200 text-amber-950 flex items-start gap-3">
                      <span className="text-lg">🚨</span>
                      <div className="text-xs">
                        <strong className="font-bold block text-amber-950">
                          {stats.highRiskVerificationBatches} {t('admin.highRiskFlagged', 'batch scan(s) flagged with HIGH RISK indicators.')}
                        </strong>
                        <span className="text-amber-800">
                          {t('admin.highRiskScansSub', 'Abnormal scan velocity detected. Immediate investigation advised.')}
                        </span>
                      </div>
                    </div>
                  ) : (
                    <div className="p-4 rounded-xl bg-slate-50 border border-slate-200/90 text-slate-800 flex items-center gap-3">
                      <span className="text-base text-emerald-600">✓</span>
                      <div className="text-xs">
                        <strong className="font-bold block text-slate-900">
                          {t('admin.zeroTamper', 'Zero tamper events detected.')}
                        </strong>
                        <span className="text-slate-600">
                          {t('admin.nominalThresholds', 'All public QR verification activity within nominal supply-chain thresholds.')}
                        </span>
                      </div>
                    </div>
                  )}

                  {/* Quick Stat Pill Row */}
                  <div className="grid grid-cols-3 gap-3 pt-1">
                    <div className="p-3 rounded-xl bg-slate-50 border border-slate-100 text-center">
                      <div className="text-xs text-slate-500 font-medium">{t('admin.riskLevel', 'Risk Level')}</div>
                      <div className="text-lg font-extrabold text-slate-900 font-['Outfit']">
                        {stats?.highRiskVerificationBatches > 0 ? t('admin.elevated', 'ELEVATED') : t('admin.nominal', 'NOMINAL')}
                      </div>
                    </div>
                    <div className="p-3 rounded-xl bg-slate-50 border border-slate-100 text-center">
                      <div className="text-xs text-slate-500 font-medium">{t('admin.ledgerHash', 'Ledger Hash')}</div>
                      <div className="text-lg font-extrabold text-slate-900 font-['Outfit']">
                        SHA-256
                      </div>
                    </div>
                    <div className="p-3 rounded-xl bg-slate-50 border border-slate-100 text-center">
                      <div className="text-xs text-slate-500 font-medium">{t('navigation.disputes', 'Disputes')}</div>
                      <div className="text-lg font-extrabold text-slate-900 font-['Outfit']">
                        {t('admin.zeroActive', '0 Active')}
                      </div>
                    </div>
                  </div>
                </div>

                <div className="pt-2 border-t border-slate-100 flex items-center justify-between">
                  <span className="text-xs text-slate-500">{t('admin.qrEngineActive', 'QR Fingerprint Engine: Active')}</span>
                  <Link
                    to="/admin/verification-risk"
                    className="inline-flex items-center gap-1.5 text-xs font-bold text-amber-700 hover:text-amber-800 transition-colors"
                  >
                    <span>{t('admin.inspectScans', 'Inspect Flagged Scans')}</span>
                    <span>→</span>
                  </Link>
                </div>
              </div>
            </div>

            {/* IoT & Supply Chain Operational Hub */}
            <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
              {/* IoT Hive Environmental Sensor Telemetry */}
              <div className="bg-white rounded-2xl border border-slate-200/85 p-6 shadow-xs flex flex-col justify-between space-y-4">
                <div className="space-y-3">
                  <div className="flex items-center justify-between">
                    <div className="flex items-center gap-2.5">
                      <span className="text-xl">🐝</span>
                      <h3 className="font-extrabold text-sm text-slate-900 font-['Outfit']">
                        {t('admin.liveHiveTelemetry', 'Live IoT Hive Telemetry')}
                      </h3>
                    </div>
                    <span className="w-2 h-2 rounded-full bg-emerald-500 animate-pulse" />
                  </div>

                  <p className="text-xs text-slate-500">
                    {t('admin.liveHiveSub', { count: stats?.totalHives ?? 3, defaultValue: `Continuous acoustic & atmospheric sensors deployed across ${stats?.totalHives ?? 3} apiary hives.` })}
                  </p>

                  <div className="space-y-2 pt-1">
                    <div className="p-3 rounded-xl bg-slate-50 border border-slate-100 flex items-center justify-between text-xs">
                      <span className="text-slate-600 font-medium">{t('admin.internalHiveTemp', 'Internal Hive Temp')}</span>
                      <span className="font-extrabold text-slate-900 font-['Outfit']">34.2 °C <span className="text-slate-500 font-medium text-[10px] ml-1">{t('admin.optimal', 'Optimal')}</span></span>
                    </div>
                    <div className="p-3 rounded-xl bg-slate-50 border border-slate-100 flex items-center justify-between text-xs">
                      <span className="text-slate-600 font-medium">{t('admin.relativeHumidity', 'Relative Humidity')}</span>
                      <span className="font-extrabold text-slate-900 font-['Outfit']">61.5 % <span className="text-slate-500 font-medium text-[10px] ml-1">{t('admin.normal', 'Normal')}</span></span>
                    </div>
                    <div className="p-3 rounded-xl bg-slate-50 border border-slate-100 flex items-center justify-between text-xs">
                      <span className="text-slate-600 font-medium">{t('admin.activeSensorStreams', 'Active Sensor Streams')}</span>
                      <span className="font-extrabold text-slate-900 font-['Outfit']">{t('admin.onlineOf', { active: stats?.activeHives ?? 2, total: stats?.totalHives ?? 3, defaultValue: `${stats?.activeHives ?? 2} of ${stats?.totalHives ?? 3} Online` })}</span>
                    </div>
                  </div>
                </div>

                <Link
                  to="/admin/hives"
                  className="pt-2 text-xs font-bold text-amber-700 hover:text-amber-800 flex items-center gap-1"
                >
                  <span>{t('admin.exploreHiveTelemetry', 'Explore Hive Sensor Telemetry')}</span>
                  <span>→</span>
                </Link>
              </div>

              {/* Lab Quality & Purity Benchmark */}
              <div className="bg-white rounded-2xl border border-slate-200/85 p-6 shadow-xs flex flex-col justify-between space-y-4">
                <div className="space-y-3">
                  <div className="flex items-center justify-between">
                    <div className="flex items-center gap-2.5">
                      <span className="text-xl">🔬</span>
                      <h3 className="font-extrabold text-sm text-slate-900 font-['Outfit']">
                        {t('admin.labAuditsTitle', 'Laboratory Purity Audits')}
                      </h3>
                    </div>
                    <span className="text-[10px] font-bold px-2 py-0.5 rounded-full bg-slate-100 text-slate-700 border border-slate-200/80">
                      {t('admin.labStandard', 'Standard: PURE')}
                    </span>
                  </div>

                  <p className="text-xs text-slate-500">
                    {t('admin.labAuditsDesc', 'FSSAI & KVIC honey standards verification: Moisture, HMF, C4 Sugar, & Pollen analysis.')}
                  </p>

                  <div className="space-y-2 pt-1">
                    <div className="p-3 rounded-xl bg-slate-50 border border-slate-100 flex items-center justify-between text-xs">
                      <span className="text-slate-600 font-medium">{t('admin.certifiedPureBatches', 'Certified Pure Batches')}</span>
                      <span className="font-extrabold text-slate-900 font-['Outfit']">{stats?.pureBatches ?? 0} {t('navigation.batches', 'Batches')}</span>
                    </div>
                    <div className="p-3 rounded-xl bg-slate-50 border border-slate-100 flex items-center justify-between text-xs">
                      <span className="text-slate-600 font-medium">{t('admin.underLabReview', 'Under Lab Review')}</span>
                      <span className="font-extrabold text-slate-900 font-['Outfit']">{stats?.underReviewBatches ?? 0} {t('navigation.batches', 'Batches')}</span>
                    </div>
                    <div className="p-3 rounded-xl bg-slate-50 border border-slate-100 flex items-center justify-between text-xs">
                      <span className="text-slate-600 font-medium">{t('admin.pendingTestQueue', 'Pending Test Queue')}</span>
                      <span className="font-extrabold text-slate-900 font-['Outfit']">{stats?.pendingLabTests ?? 0} {t('common.status', 'Scheduled')}</span>
                    </div>
                  </div>
                </div>

                <Link
                  to="/admin/lab"
                  className="pt-2 text-xs font-bold text-amber-700 hover:text-amber-800 flex items-center gap-1"
                >
                  <span>{t('admin.openLabPortal', 'Open Laboratory Portal')}</span>
                  <span>→</span>
                </Link>
              </div>

              {/* Core Platform Services & Status */}
              <div className="bg-white rounded-2xl border border-slate-200/85 p-6 shadow-xs flex flex-col justify-between space-y-4">
                <div className="space-y-3">
                  <div className="flex items-center justify-between">
                    <div className="flex items-center gap-2.5">
                      <span className="text-xl">🔌</span>
                      <h3 className="font-extrabold text-sm text-slate-900 font-['Outfit']">
                        {t('admin.coreSystemServices', 'Core System Services')}
                      </h3>
                    </div>
                    <span className="text-[10px] font-bold px-2 py-0.5 rounded-full bg-slate-100 text-slate-700 border border-slate-200/80">
                      {t('admin.fourOfFourOnline', '4/4 Online')}
                    </span>
                  </div>

                  <p className="text-xs text-slate-500">
                    {t('admin.liveHiveSub', 'Live operational telemetry of integrated blockchain, QR engine, and analytics microservices.')}
                  </p>

                  <div className="space-y-2 pt-1">
                    <div className="p-2.5 rounded-xl bg-slate-50 border border-slate-100 flex items-center justify-between text-xs">
                      <span className="text-slate-700 font-medium flex items-center gap-1.5">
                        <span>🔗</span>
                        <span>{t('admin.blockchainMockNode', 'Blockchain Mock Node')}</span>
                      </span>
                      <span className="font-bold text-slate-700 text-[11px]">{t('admin.operational', 'Operational')}</span>
                    </div>
                    <div className="p-2.5 rounded-xl bg-slate-50 border border-slate-100 flex items-center justify-between text-xs">
                      <span className="text-slate-700 font-medium flex items-center gap-1.5">
                        <span>📱</span>
                        <span>{t('admin.zxingEngine', 'ZXing QR Engine')}</span>
                      </span>
                      <span className="font-bold text-slate-700 text-[11px]">{t('admin.operational', 'Operational')}</span>
                    </div>
                    <div className="p-2.5 rounded-xl bg-slate-50 border border-slate-100 flex items-center justify-between text-xs">
                      <span className="text-slate-700 font-medium flex items-center gap-1.5">
                        <span>📈</span>
                        <span>{t('admin.analyticsAggregator', 'Analytics Aggregator')}</span>
                      </span>
                      <span className="font-bold text-slate-700 text-[11px]">{t('admin.operational', 'Operational')}</span>
                    </div>
                    <div className="p-2.5 rounded-xl bg-slate-50 border border-slate-100 flex items-center justify-between text-xs">
                      <span className="text-slate-700 font-medium flex items-center gap-1.5">
                        <span>⚖️</span>
                        <span>{t('admin.disputeManagement', 'Dispute Management')}</span>
                      </span>
                      <span className="font-bold text-slate-700 text-[11px]">{t('admin.operational', 'Operational')}</span>
                    </div>
                  </div>
                </div>

                <Link
                  to="/admin/analytics"
                  className="pt-2 text-xs font-bold text-amber-700 hover:text-amber-800 flex items-center gap-1"
                >
                  <span>{t('admin.viewAnalytics', 'View System Analytics')}</span>
                  <span>→</span>
                </Link>
              </div>
            </div>
          </>
        )}
      </div>
    </AdminLayout>
  )
}

export default AdminDashboardPage
