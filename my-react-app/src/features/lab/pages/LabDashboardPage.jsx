import React, { useEffect } from 'react'
import { Link } from 'react-router-dom'
import LabLayout from '../../../layouts/LabLayout'
import PendingTestCard from '../components/PendingTestCard'
import Card from '../../../components/ui/Card'
import Button from '../../../components/ui/Button'
import PageHeader from '../../../components/layout/PageHeader'
import MetricCard from '../../../components/ui/MetricCard'
import LoadingSpinner from '../../../components/feedback/LoadingSpinner'
import Alert from '../../../components/feedback/Alert'
import { useLabTests } from '../hooks/useLabTests'
import { useLanguage } from '../../../i18n/LanguageContext'
import VoiceButton from '../../../components/common/VoiceButton'

export const LabDashboardPage = () => {
  const { t } = useLanguage()
  const { pendingBatches, stats, loading, error, fetchPendingTests, fetchLabStats, clearError } =
    useLabTests()

  useEffect(() => {
    fetchPendingTests()
    fetchLabStats()
  }, [fetchPendingTests, fetchLabStats])

  const voiceInstructions = `${t('lab.dashboardTitle', 'Lab Testing Portal')}. ${t('lab.pendingTitle', 'Pending Purity Tests')}: ${stats.pending}. ${t('lab.pure', 'Pure Honey')}: ${stats.pure}.`

  return (
    <LabLayout>
      <div className="space-y-6">
        {/* Page Header */}
        <PageHeader
          title={t('lab.dashboardTitle', 'Laboratory Dashboard')}
          subtitle={t('lab.pendingSub', 'Analyze incoming honey harvest batches, issue purity scores, and register immutable test results.')}
          actions={
            <>
              <VoiceButton textToSpeak={voiceInstructions} size="sm" />
              <Link to="/lab/tests/pending">
                <Button variant="primary" size="sm" className="font-bold">
                  {t('common.viewDetails', 'View All Pending')} ({stats.pending}) →
                </Button>
              </Link>
            </>
          }
        />

        {error && <Alert type="error" message={error} onClose={clearError} />}

        {/* Stats Grid */}
        <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-5 gap-4 align-stretch">
          {[
            { label: 'Pending Tests', val: stats.pending, icon: '⏳' },
            { label: 'Completed', val: stats.completed, icon: '📊' },
            { label: 'Pure Honey', val: stats.pure, icon: '✅' },
            { label: 'Under Review', val: stats.underReview, icon: '⚠️' },
            { label: 'Failed Tests', val: stats.failed, icon: '❌' },
          ].map(({ label, val, icon }) => (
            <MetricCard key={label} icon={icon} label={label} value={val} />
          ))}
        </div>

        {/* Pending Batches Queue */}
        <div className="space-y-4 pt-2">
          <div className="flex items-center justify-between border-b border-slate-200 pb-3">
            <h2 className="text-xl font-bold text-slate-900 font-['Outfit'] flex items-center gap-2">
              <span>🧪</span> Incoming Batches Awaiting Analysis
            </h2>
            <span className="text-xs text-slate-500 font-medium">
              {pendingBatches.length} batch{pendingBatches.length !== 1 ? 'es' : ''} in queue
            </span>
          </div>

          {loading && pendingBatches.length === 0 ? (
            <LoadingSpinner text="Loading pending honey batches..." />
          ) : pendingBatches.length === 0 ? (
            <Card className="text-center py-12 space-y-3">
              <div className="text-5xl">✨</div>
              <h3 className="text-lg font-bold text-slate-900 font-['Outfit']">All Caught Up!</h3>
              <p className="text-slate-500 text-xs max-w-sm mx-auto">
                No honey batches are currently waiting for laboratory testing.
              </p>
            </Card>
          ) : (
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-5">
              {pendingBatches.slice(0, 6).map((batch) => (
                <PendingTestCard key={batch.batchId} batch={batch} />
              ))}
            </div>
          )}
        </div>
      </div>
    </LabLayout>
  )
}

export default LabDashboardPage
