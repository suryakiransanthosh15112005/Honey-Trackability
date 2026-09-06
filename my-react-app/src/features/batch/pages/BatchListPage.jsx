import React, { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import BeekeeperLayout from '../../../layouts/BeekeeperLayout'
import BatchCard from '../components/BatchCard'
import OfflineBatchIndicator from '../components/OfflineBatchIndicator'
import PageHeader from '../../../components/layout/PageHeader'
import MetricCard from '../../../components/ui/MetricCard'
import LoadingSpinner from '../../../components/feedback/LoadingSpinner'
import Alert from '../../../components/feedback/Alert'
import Card from '../../../components/ui/Card'
import Button from '../../../components/ui/Button'
import VoiceButton from '../../../components/common/VoiceButton'
import { useBatches } from '../hooks/useBatches'
import useBatchSync from '../hooks/useBatchSync'
import { useLanguage } from '../../../i18n/LanguageContext'

export const BatchListPage = () => {
  const {
    batches = [],
    stats = { total: 0, created: 0, sentForTesting: 0 },
    pageInfo = { totalPages: 1, isLast: true },
    loading,
    error,
    fetchBatches,
    fetchStats,
    clearError,
  } = useBatches()

  const { localDrafts = [] } = useBatchSync()
  const { t } = useLanguage()
  const [statusFilter, setStatusFilter] = useState('')
  const [currentPage, setCurrentPage] = useState(0)

  useEffect(() => {
    fetchBatches({ status: statusFilter || undefined, page: currentPage, size: 12 })
    if (fetchStats) {
      fetchStats()
    }
  }, [statusFilter, currentPage, fetchBatches, fetchStats])

  const safeDrafts = Array.isArray(localDrafts) ? localDrafts : []
  const safeBatches = Array.isArray(batches) ? batches : []
  const combinedBatches = [...safeDrafts, ...safeBatches]

  const totalBatchesCount = (stats?.total || 0) + safeDrafts.length
  const createdCount = stats?.created || 0
  const sentTestingCount = stats?.sentForTesting || 0
  const pendingSyncCount = safeDrafts.length

  return (
    <BeekeeperLayout>
      <div className="space-y-6">
        {/* Page Header */}
        <PageHeader
          title={t('navigation.myBatches', 'My Honey Batches')}
          subtitle={t('batch.myBatchesSub', 'Track your honey harvests, quality validation, and batch statuses.')}
          actions={
            <div className="flex items-center gap-2.5 flex-wrap">
              <VoiceButton translationKey="batch.myBatchesSub" />
              <OfflineBatchIndicator />
              <Link to="/beekeeper/batches/new">
                <Button id="new-batch-btn" variant="primary">
                  <span>+</span> {t('dashboard.newBatch', 'New Batch')}
                </Button>
              </Link>
            </div>
          }
        />

        {/* Stats Strip */}
        <div className="grid grid-cols-2 sm:grid-cols-4 gap-4 align-stretch">
          <MetricCard
            label={t('dashboard.totalBatches', 'Total Batches')}
            value={totalBatchesCount.toString()}
            icon="🍯"
            subtext="All recorded batches"
          />
          <MetricCard
            label={t('batch.statusCreated', 'Created')}
            value={createdCount.toString()}
            icon="🟡"
            subtext="Ready for lab submission"
          />
          <MetricCard
            label={t('batch.statusSentForTesting', 'Sent Testing')}
            value={sentTestingCount.toString()}
            icon="🔵"
            subtext="Under lab review"
          />
          <MetricCard
            label={t('profile.statusPending', 'Pending Sync')}
            value={pendingSyncCount.toString()}
            icon="💾"
            subtext="Stored in offline cache"
          />
        </div>

        {/* Error notification */}
        {error && <Alert type="error" message={error} onClose={clearError} />}

        {/* Batch Content Grid */}
        {loading && combinedBatches.length === 0 ? (
          <LoadingSpinner text={t('loading.loading', 'Loading your honey batches...')} />
        ) : combinedBatches.length === 0 ? (
          <Card className="text-center py-16 space-y-4 bg-white border border-slate-200 shadow-sm">
            <div className="text-6xl">🍯</div>
            <h2 className="text-2xl font-bold text-slate-900 font-['Outfit']">
              {t('batch.noBatches', 'No Honey Batches Yet')}
            </h2>
            <p className="text-slate-500 text-sm max-w-sm mx-auto">
              {t('batch.createSub', 'Record your first honey harvest against an active hive to begin the purity tracking process.')}
            </p>
            <div className="pt-2">
              <Link to="/beekeeper/batches/new">
                <Button variant="primary">{t('dashboard.newHarvest', '+ Create First Batch')}</Button>
              </Link>
            </div>
          </Card>
        ) : (
          <>
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-5">
              {combinedBatches.map((batch) => (
                <BatchCard key={batch.batchId || batch.localId} batch={batch} />
              ))}
            </div>

            {/* Pagination Controls */}
            {pageInfo?.totalPages > 1 && (
              <div className="flex items-center justify-center gap-3 pt-6">
                <Button
                  variant="secondary"
                  size="sm"
                  disabled={currentPage === 0}
                  onClick={() => setCurrentPage((p) => Math.max(0, p - 1))}
                >
                  {t('common.back', '← Previous')}
                </Button>
                <span className="text-xs text-slate-500">
                  Page <span className="text-slate-900 font-bold">{currentPage + 1}</span> of{' '}
                  <span className="text-slate-900 font-bold">{pageInfo.totalPages}</span>
                </span>
                <Button
                  variant="secondary"
                  size="sm"
                  disabled={pageInfo?.isLast}
                  onClick={() => setCurrentPage((p) => p + 1)}
                >
                  {t('common.next', 'Next →')}
                </Button>
              </div>
            )}
          </>
        )}
      </div>
    </BeekeeperLayout>
  )
}

export default BatchListPage
