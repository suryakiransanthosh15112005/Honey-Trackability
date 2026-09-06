import React, { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import LabLayout from '../../../layouts/LabLayout'
import PendingTestCard from '../components/PendingTestCard'
import LoadingSpinner from '../../../components/feedback/LoadingSpinner'
import Alert from '../../../components/feedback/Alert'
import Card from '../../../components/ui/Card'
import Input from '../../../components/ui/Input'
import VoiceButton from '../../../components/common/VoiceButton'
import { useLabTests } from '../hooks/useLabTests'
import { useLanguage } from '../../../i18n/LanguageContext'

export const PendingTestsPage = () => {
  const { pendingBatches, loading, error, fetchPendingTests, clearError } = useLabTests()
  const { t } = useLanguage()
  const [search, setSearch] = useState('')

  useEffect(() => {
    fetchPendingTests()
  }, [fetchPendingTests])

  const filtered = pendingBatches.filter(
    (b) =>
      b.batchId?.toLowerCase().includes(search.toLowerCase()) ||
      b.beekeeperName?.toLowerCase().includes(search.toLowerCase()) ||
      b.village?.toLowerCase().includes(search.toLowerCase()) ||
      b.hiveCode?.toLowerCase().includes(search.toLowerCase())
  )

  return (
    <LabLayout>
      <div className="space-y-6">
        {/* Header */}
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
          <div>
            <h1 className="text-3xl font-black text-slate-900 font-['Outfit']">
              {t('lab.pendingTitle', 'Pending Lab Tests')}
            </h1>
            <p className="text-slate-500 text-sm mt-1">
              {t('lab.pendingSub', 'Honey batches submitted by beekeepers awaiting lab certificate verification')}
            </p>
          </div>

          <div className="flex items-center gap-3">
            <VoiceButton translationKey="lab.pendingSub" />
            <div className="w-full sm:w-64">
              <Input
                id="search-pending-tests"
                placeholder={t('common.appName', 'Search by batch, beekeeper...')}
                value={search}
                onChange={(e) => setSearch(e.target.value)}
              />
            </div>
          </div>
        </div>

        {error && <Alert type="error" message={error} onClose={clearError} />}

        {loading && pendingBatches.length === 0 ? (
          <LoadingSpinner text={t('loading.loading', 'Loading pending lab tests...')} />
        ) : filtered.length === 0 ? (
          <Card className="text-center py-16 space-y-3">
            <div className="text-5xl">🍯</div>
            <h3 className="text-lg font-bold text-slate-900 font-['Outfit']">
              {t('empty.noTests', 'No Batches Awaiting Testing')}
            </h3>
            <p className="text-slate-500 text-xs max-w-sm mx-auto">
              {t('empty.noData', 'All submitted batches have been analyzed and certified.')}
            </p>
          </Card>
        ) : (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-5">
            {filtered.map((batch) => (
              <PendingTestCard key={batch.batchId} batch={batch} />
            ))}
          </div>
        )}
      </div>
    </LabLayout>
  )
}

export default PendingTestsPage
