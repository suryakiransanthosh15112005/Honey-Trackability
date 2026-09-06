import React, { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import BeekeeperLayout from '../../../layouts/BeekeeperLayout'
import Button from '../../../components/ui/Button'
import PageHeader from '../../../components/layout/PageHeader'
import Card from '../../../components/ui/Card'
import VoiceButton from '../../../components/common/VoiceButton'
import { useAuth } from '../../auth/hooks/useAuth'
import { useBeekeeperProfile } from '../hooks/useBeekeeperProfile'
import { useLanguage } from '../../../i18n/LanguageContext'
import hiveApi from '../../hive/api/hiveApi'
import batchApi from '../../batch/api/batchApi'
import iotApi from '../../iot/api/iotApi'
import yieldPredictionApi from '../../ai/api/yieldPredictionApi'

export const BeekeeperDashboard = () => {
  const { phoneNumber } = useAuth()
  const { profile, status, fetchStatus, fetchProfile } = useBeekeeperProfile()
  const { t } = useLanguage()
  const [hiveCount, setHiveCount] = useState('0')
  const [batchStats, setBatchStats] = useState({ total: 0, created: 0, sentForTesting: 0 })
  const [healthStats, setHealthStats] = useState({ healthy: 0, watch: 0, alert: 0, total: 0 })
  const [predictions, setPredictions] = useState([])

  useEffect(() => {
    fetchStatus()
    fetchProfile()

    hiveApi
      .getHiveCount()
      .then((res) => setHiveCount(res.data?.data?.total ?? 0))
      .catch(() => setHiveCount(0))

    batchApi
      .getBatchStats()
      .then((res) => setBatchStats(res.data?.data || { total: 0, created: 0, sentForTesting: 0 }))
      .catch(() => setBatchStats({ total: 0, created: 0, sentForTesting: 0 }))

    iotApi
      .getAllHivesHealth()
      .then((res) => {
        const list = res.data?.data || []
        const healthy = list.filter((h) => h.status === 'HEALTHY').length
        const watch = list.filter((h) => h.status === 'WATCH').length
        const alert = list.filter((h) => h.status === 'ALERT').length
        setHealthStats({ healthy, watch, alert, total: list.length })
      })
      .catch(() => setHealthStats({ healthy: 0, watch: 0, alert: 0, total: 0 }))

    yieldPredictionApi
      .getAllYieldPredictions()
      .then((res) => setPredictions(res.data?.data || []))
      .catch(() => setPredictions([]))
  }, [])

  const hasCompletedProfile = status?.completed || !!profile
  const displayName = profile?.name || phoneNumber || t('auth.beekeeperRole', 'Beekeeper')
  const primaryPrediction = predictions.length > 0 ? predictions[0] : null

  const voiceInstructions = `${t('navigation.beekeepers', 'Beekeeper Dashboard')}. ${t('dashboard.registeredHives', 'Registered Hives')}: ${hiveCount}. ${t('dashboard.totalBatches', 'Total Batches')}: ${batchStats.total}.`

  return (
    <BeekeeperLayout>
      <div className="space-y-6">
        {/* Profile Warning Banner */}
        {!hasCompletedProfile && (
          <div className="p-4 rounded-xl bg-amber-50 border-l-4 border-amber-500 border border-amber-200/80 flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4 shadow-xs">
            <div className="flex items-center gap-3 min-w-0">
              <span className="text-2xl shrink-0">⚠️</span>
              <div>
                <h3 className="font-bold text-slate-900 text-sm">
                  {t('onboarding.warningTitle', 'Profile Setup Incomplete')}
                </h3>
                <p className="text-xs text-slate-600 mt-0.5">
                  {t(
                    'onboarding.warningSub',
                    'Please complete your KVIC beekeeper profile to enable harvest logging and certificates.'
                  )}
                </p>
              </div>
            </div>
            <Link to="/beekeeper/onboarding" className="shrink-0 w-full sm:w-auto">
              <Button variant="primary" size="sm" className="w-full sm:w-auto">
                {t('onboarding.completeSetup', 'Complete Setup →')}
              </Button>
            </Link>
          </div>
        )}

        {/* Dashboard Header */}
        <PageHeader
          title={
            <span>
              {t('navigation.beekeepers', 'Beekeeper')}{' '}
              <span className="text-blue-600 font-extrabold">{t('navigation.dashboard', 'Dashboard')}</span>
            </span>
          }
          subtitle={
            <span>
              {t('dashboard.welcome', 'Welcome back,')} <strong className="text-slate-900 font-bold">{displayName}</strong>
              {profile?.kvicId && (
                <span className="font-mono text-xs px-2.5 py-0.5 ml-2 rounded-full bg-amber-100 text-amber-900 border border-amber-300 font-semibold inline-block">
                  {profile.kvicId}
                </span>
              )}
            </span>
          }
          actions={
            <div className="flex items-center gap-2.5 flex-wrap">
              <VoiceButton textToSpeak={voiceInstructions} size="sm" />
              <Link to="/beekeeper/batches/new">
                <Button variant="primary" size="sm">
                  <span>+</span> {t('dashboard.newBatch', 'New Batch')}
                </Button>
              </Link>
              <Link to="/beekeeper/earnings">
                <Button variant="secondary" size="sm">
                  <span>💰</span> Earnings
                </Button>
              </Link>
              <Link to="/beekeeper/profile">
                <Button variant="secondary" size="sm">
                  <span>👤</span> {t('navigation.portalLabel', 'Profile')}
                </Button>
              </Link>
            </div>
          }
        />

        {/* 4 Live KPI Metric Cards */}
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 align-stretch">
          <Link to="/beekeeper/hives" className="block h-full no-underline group">
            <div className="h-full rounded-2xl bg-white border border-slate-200 p-5 shadow-xs hover:shadow-md hover:border-blue-400 transition-all duration-200 flex flex-col justify-between">
              <div className="flex items-start justify-between">
                <div>
                  <span className="text-xs font-bold text-slate-600 block uppercase tracking-wider">
                    {t('dashboard.registeredHives', 'Registered Hives')}
                  </span>
                  <div className="text-2xl font-black font-mono text-slate-900 mt-1">
                    {hiveCount} {t('units.hives', 'Hives')}
                  </div>
                </div>
                <span className="w-10 h-10 rounded-xl bg-amber-50 border border-amber-200 flex items-center justify-center text-xl shrink-0 group-hover:scale-110 transition-transform">
                  🐝
                </span>
              </div>
              <p className="text-xs text-slate-600 mt-3 font-medium">Active apiary units in field</p>
            </div>
          </Link>

          <Link to="/beekeeper/batches" className="block h-full no-underline group">
            <div className="h-full rounded-2xl bg-white border border-slate-200 p-5 shadow-xs hover:shadow-md hover:border-blue-400 transition-all duration-200 flex flex-col justify-between">
              <div className="flex items-start justify-between">
                <div>
                  <span className="text-xs font-bold text-slate-600 block uppercase tracking-wider">
                    {t('dashboard.totalBatches', 'Total Batches')}
                  </span>
                  <div className="text-2xl font-black font-mono text-slate-900 mt-1">
                    {batchStats.total} {t('navigation.batches', 'Batches')}
                  </div>
                </div>
                <span className="w-10 h-10 rounded-xl bg-amber-50 border border-amber-200 flex items-center justify-center text-xl shrink-0 group-hover:scale-110 transition-transform">
                  🍯
                </span>
              </div>
              <p className="text-xs text-slate-600 mt-3 font-medium">Logged pure honey harvests</p>
            </div>
          </Link>

          <Link to="/beekeeper/batches" className="block h-full no-underline group">
            <div className="h-full rounded-2xl bg-white border border-slate-200 p-5 shadow-xs hover:shadow-md hover:border-blue-400 transition-all duration-200 flex flex-col justify-between">
              <div className="flex items-start justify-between">
                <div>
                  <span className="text-xs font-bold text-slate-600 block uppercase tracking-wider">
                    {t('dashboard.testingProgress', 'Testing in Progress')}
                  </span>
                  <div className="text-2xl font-black font-mono text-blue-600 mt-1">
                    {batchStats.sentForTesting} {t('navigation.batches', 'Batches')}
                  </div>
                </div>
                <span className="w-10 h-10 rounded-xl bg-blue-50 border border-blue-200 flex items-center justify-center text-xl shrink-0 group-hover:scale-110 transition-transform">
                  🧪
                </span>
              </div>
              <p className="text-xs text-slate-600 mt-3 font-medium">Under active laboratory analysis</p>
            </div>
          </Link>

          <Link to="/beekeeper/profile" className="block h-full no-underline group">
            <div className="h-full rounded-2xl bg-white border border-slate-200 p-5 shadow-xs hover:shadow-md hover:border-blue-400 transition-all duration-200 flex flex-col justify-between">
              <div className="flex items-start justify-between">
                <div>
                  <span className="text-xs font-bold text-slate-600 block uppercase tracking-wider">
                    {t('dashboard.kvicVerification', 'KVIC Verification')}
                  </span>
                  <div className="text-xl font-black font-mono text-slate-900 mt-1">
                    {profile?.verificationStatus
                      ? t(
                          `profile.status${
                            profile.verificationStatus === 'APPROVED'
                              ? 'Approved'
                              : profile.verificationStatus === 'REJECTED'
                                ? 'Rejected'
                                : 'Pending'
                          }`,
                          profile.verificationStatus
                        )
                      : hasCompletedProfile
                        ? t('profile.statusApproved', 'APPROVED')
                        : t('profile.statusPending', 'PENDING')}
                  </div>
                </div>
                <span className="w-10 h-10 rounded-xl bg-slate-100 border border-slate-200 flex items-center justify-center text-xl shrink-0 group-hover:scale-110 transition-transform">
                  📜
                </span>
              </div>
              <p className="text-xs text-slate-600 mt-3 font-medium">
                {hasCompletedProfile ? 'Certified artisan beekeeper' : 'Profile action required'}
              </p>
            </div>
          </Link>
        </div>

        {/* 2-Column Responsive Dashboard Grid: Telemetry & Operational Hub */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          {/* Left Column: AI Forecast & IoT Health */}
          <div className="space-y-6">
            {/* AI Yield Prediction Card */}
            <Card className="p-6 space-y-4 hover:border-amber-400 transition-all duration-200">
              <div className="flex items-center justify-between gap-2 border-b border-slate-100 pb-3">
                <div className="flex items-center gap-2.5">
                  <span className="w-10 h-10 rounded-xl bg-amber-50 border border-amber-200 flex items-center justify-center text-xl">
                    🤖
                  </span>
                  <div>
                    <h2 className="font-bold text-base text-slate-900 font-['Outfit']">
                      {t('dashboard.aiPredictionHeading', 'Expected Harvest Prediction')}
                    </h2>
                    <p className="text-xs text-slate-600">AI-assisted yield forecast & optimal harvest window</p>
                  </div>
                </div>
                <Link to="/beekeeper/hive-health">
                  <Button variant="ghost" size="xs">
                    {t('dashboard.viewPredictions', 'Details →')}
                  </Button>
                </Link>
              </div>

              {primaryPrediction ? (
                <div className="space-y-4">
                  <p className="text-xs text-slate-600 leading-relaxed">
                    {t('dashboard.aiPredictionText', {
                      code: primaryPrediction.hiveCode,
                      min: primaryPrediction.minimumKg,
                      max: primaryPrediction.maximumKg,
                      days: primaryPrediction.daysUntilHarvest,
                      confidence: primaryPrediction.confidence,
                    })}
                  </p>

                  {/* Harvest Timing Progress Gauge */}
                  <div className="space-y-1.5">
                    <div className="flex items-center justify-between text-xs font-semibold text-slate-700">
                      <span>Harvest Readiness Window</span>
                      <span className="font-mono text-amber-900 font-bold">~{primaryPrediction.daysUntilHarvest} days remaining</span>
                    </div>
                    <div className="w-full h-2.5 bg-slate-100 rounded-full overflow-hidden">
                      <div
                        className="h-full bg-amber-500 rounded-full transition-all duration-500"
                        style={{
                          width: `${Math.min(
                            Math.max(100 - (Number(primaryPrediction.daysUntilHarvest) || 10) * 3, 20),
                            95
                          )}%`,
                        }}
                      />
                    </div>
                  </div>

                  <div className="p-3 rounded-xl bg-amber-50/70 border border-amber-200 flex items-center justify-between text-xs font-mono">
                    <span className="text-amber-900 font-bold">
                      Yield: {primaryPrediction.minimumKg}–{primaryPrediction.maximumKg} kg
                    </span>
                    <span className="text-blue-700 font-bold">
                      {primaryPrediction.confidence}% Confidence
                    </span>
                  </div>
                </div>
              ) : (
                <div className="py-8 text-center text-slate-600 text-xs space-y-1">
                  <span className="text-2xl block">🍯</span>
                  <p className="font-medium text-slate-800">AI Yield Telemetry Initializing</p>
                  <p>Predictions calculate automatically as acoustic and sensor logs stream from registered hives.</p>
                </div>
              )}
            </Card>

            {/* IoT Hive Health Live Summary */}
            <Card className="p-6 space-y-4 hover:border-blue-400 transition-all duration-200">
              <div className="flex items-center justify-between gap-2 border-b border-slate-100 pb-3">
                <div className="flex items-center gap-2.5">
                  <span className="w-10 h-10 rounded-xl bg-blue-50 border border-blue-200 flex items-center justify-center text-xl">
                    📡
                  </span>
                  <div>
                    <h2 className="font-bold text-base text-slate-900 font-['Outfit']">
                      {t('dashboard.iotHeading', 'IoT Hive Health Telemetry')}
                    </h2>
                    <p className="text-xs text-slate-600">Acoustic frequency, core temperature & humidity</p>
                  </div>
                </div>
                <Link to="/beekeeper/hive-health">
                  <Button variant="ghost" size="xs">
                    {t('dashboard.viewHiveHealth', 'Inspect →')}
                  </Button>
                </Link>
              </div>

              <div className="space-y-4">
                <div className="grid grid-cols-3 gap-2.5">
                  <div className="p-3 rounded-xl bg-blue-50/80 border border-blue-200 text-center">
                    <span className="text-[11px] font-bold text-blue-900 block uppercase">
                      {t('iot.healthy', 'Healthy')}
                    </span>
                    <span className="text-xl font-black font-mono text-blue-700">
                      {healthStats.healthy}
                    </span>
                  </div>
                  <div className="p-3 rounded-xl bg-amber-50/80 border border-amber-200 text-center">
                    <span className="text-[11px] font-bold text-amber-900 block uppercase">
                      {t('iot.watch', 'Watch')}
                    </span>
                    <span className="text-xl font-black font-mono text-amber-600">
                      {healthStats.watch}
                    </span>
                  </div>
                  <div className="p-3 rounded-xl bg-slate-100 border border-slate-300 text-center">
                    <span className="text-[11px] font-bold text-slate-800 block uppercase">
                      {t('iot.alert', 'Alert')}
                    </span>
                    <span className="text-xl font-black font-mono text-slate-900">
                      {healthStats.alert}
                    </span>
                  </div>
                </div>

                <div className="pt-2 flex items-center justify-between border-t border-slate-100">
                  <span className="text-xs text-slate-600">
                    <strong className="text-slate-900">{healthStats.total}</strong> sensor nodes linked
                  </span>
                  <Link to="/beekeeper/hive-health">
                    <Button variant="secondary" size="sm">
                      View Health Map →
                    </Button>
                  </Link>
                </div>
              </div>
            </Card>
          </div>

          {/* Right Column: Operational Quick Actions & Marketplace Hub */}
          <div className="space-y-6">
            {/* Hive Management Card */}
            <Card className="p-6 flex flex-col justify-between space-y-4 hover:border-slate-300 transition-all duration-200">
              <div className="space-y-2">
                <div className="flex items-center gap-3">
                  <span className="w-10 h-10 rounded-xl bg-amber-50 border border-amber-200 flex items-center justify-center text-xl">
                    🐝
                  </span>
                  <div>
                    <h3 className="font-bold text-lg text-slate-900 font-['Outfit']">
                      {t('dashboard.quickHiveTitle', 'Hive Management')}
                    </h3>
                    <p className="text-xs text-slate-600">
                      {t(
                        'dashboard.quickHiveDesc',
                        'Manage apiary locations, monitor active/inactive statuses, and register new hives.'
                      )}
                    </p>
                  </div>
                </div>
              </div>
              <div className="pt-3 flex items-center justify-between border-t border-slate-100">
                <span className="text-xs text-slate-600 font-mono font-medium">
                  {hiveCount} Registered Colonies
                </span>
                <Link to="/beekeeper/hives">
                  <Button variant="secondary" size="sm">
                    {t('dashboard.manageHives', 'Manage Hives →')}
                  </Button>
                </Link>
              </div>
            </Card>

            {/* Honey Batches Card */}
            <Card className="p-6 flex flex-col justify-between space-y-4 hover:border-slate-300 transition-all duration-200">
              <div className="space-y-2">
                <div className="flex items-center gap-3">
                  <span className="w-10 h-10 rounded-xl bg-amber-50 border border-amber-200 flex items-center justify-center text-xl">
                    🍯
                  </span>
                  <div>
                    <h3 className="font-bold text-lg text-slate-900 font-['Outfit']">
                      {t('dashboard.quickBatchTitle', 'Honey Batches & Harvests')}
                    </h3>
                    <p className="text-xs text-slate-600">
                      {t(
                        'dashboard.quickBatchDesc',
                        'Log harvest yields from hives, capture batch photos, and submit for lab testing.'
                      )}
                    </p>
                  </div>
                </div>
              </div>
              <div className="pt-3 flex items-center justify-between gap-3 border-t border-slate-100 flex-wrap">
                <Link to="/beekeeper/batches">
                  <Button variant="secondary" size="sm">
                    {t('dashboard.viewBatches', 'View Batches')}
                  </Button>
                </Link>
                <Link to="/beekeeper/batches/new">
                  <Button variant="primary" size="sm">
                    {t('dashboard.newHarvest', '+ New Harvest')}
                  </Button>
                </Link>
              </div>
            </Card>

            {/* Marketplace Listings & Financial Hub Shortcut Card */}
            <Card className="p-6 flex flex-col justify-between space-y-4 hover:border-blue-300 transition-all duration-200">
              <div className="space-y-2">
                <div className="flex items-center gap-3">
                  <span className="w-10 h-10 rounded-xl bg-blue-50 border border-blue-200 flex items-center justify-center text-xl">
                    💰
                  </span>
                  <div>
                    <h3 className="font-bold text-lg text-slate-900 font-['Outfit']">
                      Marketplace Listings & Revenue
                    </h3>
                    <p className="text-xs text-slate-600">
                      Track active jar listings, fulfill customer purchases, and inspect settled bank payouts.
                    </p>
                  </div>
                </div>
              </div>
              <div className="pt-3 flex items-center justify-between gap-3 border-t border-slate-100 flex-wrap">
                <Link to="/beekeeper/products">
                  <Button variant="secondary" size="sm">
                    📦 Manage Listings
                  </Button>
                </Link>
                <Link to="/beekeeper/earnings">
                  <Button variant="primary" size="sm">
                    💵 View Earnings & Payouts →
                  </Button>
                </Link>
              </div>
            </Card>
          </div>
        </div>
      </div>
    </BeekeeperLayout>
  )
}

export default BeekeeperDashboard
