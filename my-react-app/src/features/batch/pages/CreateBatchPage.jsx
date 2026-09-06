import React, { useEffect, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import BeekeeperLayout from '../../../layouts/BeekeeperLayout'
import BatchForm from '../components/BatchForm'
import Card from '../../../components/ui/Card'
import Button from '../../../components/ui/Button'
import Alert from '../../../components/feedback/Alert'
import LoadingSpinner from '../../../components/feedback/LoadingSpinner'
import BatchStatusBadge from '../components/BatchStatusBadge'
import SyncStatusBadge from '../components/SyncStatusBadge'
import OfflineBatchIndicator from '../components/OfflineBatchIndicator'
import VoiceButton from '../../../components/common/VoiceButton'
import { useBatches } from '../hooks/useBatches'
import useNetworkStatus from '../hooks/useNetworkStatus'
import { useLanguage } from '../../../i18n/LanguageContext'
import hiveApi from '../../hive/api/hiveApi'
import { cacheHives, getCachedHives } from '../services/offlineBatchStore'

export const CreateBatchPage = () => {
  const { createBatchOfflineAware, loading, error, clearError } = useBatches()
  const { isOnline } = useNetworkStatus()
  const { t } = useLanguage()

  const [hives, setHives] = useState([])
  const [loadingHives, setLoadingHives] = useState(true)
  const [hiveFetchError, setHiveFetchError] = useState(null)
  const [createdResult, setCreatedResult] = useState(null)
  const [submitting, setSubmitting] = useState(false)
  const navigate = useNavigate()

  useEffect(() => {
    const loadHives = async () => {
      setLoadingHives(true)
      setHiveFetchError(null)

      try {
        if (navigator.onLine) {
          const res = await hiveApi.getHives()
          const liveHives = res.data.data || []
          setHives(liveHives)
          await cacheHives(liveHives)
        } else {
          const cached = await getCachedHives()
          setHives(cached.map((h) => ({ id: h.hiveId, hiveCode: h.hiveCode, clusterName: h.clusterName, status: h.status })))
        }
      } catch (err) {
        try {
          const cached = await getCachedHives()
          if (cached.length > 0) {
            setHives(cached.map((h) => ({ id: h.hiveId, hiveCode: h.hiveCode, clusterName: h.clusterName, status: h.status })))
          } else {
            setHiveFetchError('Offline: No cached hive data found. Please connect to internet once.')
          }
        } catch {
          setHiveFetchError('Failed to load hives.')
        }
      } finally {
        setLoadingHives(false)
      }
    }

    loadHives()
  }, [])

  const handleCreate = async (formData) => {
    setSubmitting(true)

    let selectedHiveId = null
    if (formData instanceof FormData) {
      selectedHiveId = Number(formData.get('hiveId'))
    } else {
      selectedHiveId = Number(formData.hiveId)
    }

    const hiveObj = hives.find((h) => h.id === selectedHiveId)
    const hiveInfo = {
      hiveCode: hiveObj?.hiveCode || `Hive #${selectedHiveId}`,
      clusterName: hiveObj?.clusterName || 'Apiary',
    }

    const result = await createBatchOfflineAware(formData, hiveInfo)
    setSubmitting(false)

    if (result.success) {
      setCreatedResult({
        offline: result.offline,
        payload: result.payload,
        localId: result.localId,
      })
    }
  }

  return (
    <BeekeeperLayout>
      <div className="w-full space-y-6">
        {/* Header & Network Indicator */}
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
          <nav className="flex items-center gap-2 text-xs text-slate-500">
            <Link to="/beekeeper/batches" className="hover:text-blue-600 transition-colors font-medium">
              {t('navigation.myBatches', 'My Honey Batches')}
            </Link>
            <span>/</span>
            <span className="text-slate-800 font-semibold">{t('dashboard.newBatch', 'New Batch')}</span>
          </nav>
          <OfflineBatchIndicator />
        </div>

        {/* Title + Read Aloud Voice Button */}
        <div className="flex items-start justify-between flex-wrap gap-4">
          <div>
            <h1 className="text-3xl font-black text-slate-900 font-['Outfit'] flex items-center gap-3">
              <span>🐝</span> {t('batch.createTitle', 'Log New Honey Harvest')}
            </h1>
            <p className="text-slate-500 text-sm mt-1">
              {t('batch.createSub', 'Register a fresh harvest from one of your active hives.')}
            </p>
          </div>
          <VoiceButton translationKey="batch.createSub" size="sm" />
        </div>

        {error && <Alert type="error" message={error} onClose={clearError} />}
        {hiveFetchError && <Alert type="error" message={hiveFetchError} />}

        {/* Success Confirmation Card (Online vs Offline) */}
        {createdResult ? (
          createdResult.offline ? (
            /* 🟡 OFFLINE SUCCESS CARD */
            <Card className="p-8 text-center space-y-6 border border-amber-200 bg-amber-50 shadow-sm max-w-2xl mx-auto">
              <div className="w-16 h-16 rounded-full bg-amber-100 text-amber-800 text-3xl flex items-center justify-center mx-auto border border-amber-200">
                💾
              </div>

              <div>
                <h2 className="text-2xl font-bold text-slate-900 font-['Outfit']">
                  ✅ {t('success.batchCreated', 'Saved Offline')}
                </h2>
                <div className="mt-3 flex justify-center">
                  <VoiceButton translationKey="success.batchCreated" size="xs" />
                </div>
              </div>

              <div className="p-4 rounded-2xl bg-white border border-amber-200 max-w-md mx-auto space-y-3 shadow-sm">
                <div className="flex items-center justify-between">
                  <span className="text-xs text-slate-500 font-medium">Local Reference:</span>
                  <span className="font-mono font-bold text-base text-amber-800">
                    {createdResult.localId || createdResult.payload.batchId}
                  </span>
                </div>
                <div className="flex items-center justify-between">
                  <span className="text-xs text-slate-500 font-medium">{t('common.status', 'Status')}:</span>
                  <SyncStatusBadge status="PENDING" />
                </div>
                <div className="flex items-center justify-between">
                  <span className="text-xs text-slate-500 font-medium">{t('batch.quantityKg', 'Quantity')}:</span>
                  <span className="text-slate-900 font-bold text-sm">
                    {createdResult.payload.quantityKg} {t('units.kg', 'kg')}
                  </span>
                </div>
                <div className="flex items-center justify-between">
                  <span className="text-xs text-slate-500 font-medium">{t('hive.hiveCode', 'Hive')}:</span>
                  <span className="text-slate-900 font-mono text-sm">
                    {createdResult.payload.hiveCode}
                  </span>
                </div>
              </div>

              <div className="flex flex-col sm:flex-row items-center justify-center gap-3 pt-2">
                <Link to="/beekeeper/batches" className="w-full sm:w-auto">
                  <Button variant="primary" className="w-full bg-blue-600 hover:bg-blue-700 border-blue-600 text-white font-bold">
                    {t('dashboard.viewBatches', 'View My Batches')} →
                  </Button>
                </Link>
                <button
                  type="button"
                  className="w-full sm:w-auto btn btn--secondary"
                  onClick={() => setCreatedResult(null)}
                >
                  + {t('dashboard.newHarvest', 'Create Another Batch')}
                </button>
              </div>
            </Card>
          ) : (
            /* 🟢 ONLINE SUCCESS CARD */
            <Card className="p-8 text-center space-y-6 border border-blue-200 bg-blue-50/60 shadow-sm max-w-2xl mx-auto">
              <div className="w-16 h-16 rounded-full bg-blue-100 text-blue-700 text-3xl flex items-center justify-center mx-auto border border-blue-200">
                ✓
              </div>

              <div>
                <h2 className="text-2xl font-bold text-slate-900 font-['Outfit']">
                  {t('success.batchCreated', 'Batch Created Successfully!')}
                </h2>
                <div className="mt-3 flex justify-center">
                  <VoiceButton translationKey="success.batchCreated" size="xs" />
                </div>
              </div>

              <div className="p-4 rounded-2xl bg-white border border-blue-200 max-w-md mx-auto space-y-3 shadow-sm">
                <div className="flex items-center justify-between">
                  <span className="text-xs text-slate-500 font-medium">{t('batch.batchId', 'Batch ID')}:</span>
                  <span className="font-mono font-bold text-base text-amber-800">
                    {createdResult.payload.batchId}
                  </span>
                </div>
                <div className="flex items-center justify-between">
                  <span className="text-xs text-slate-500 font-medium">{t('common.status', 'Status')}:</span>
                  <BatchStatusBadge status={createdResult.payload.status} />
                </div>
                <div className="flex items-center justify-between">
                  <span className="text-xs text-slate-500 font-medium">{t('batch.quantityKg', 'Quantity')}:</span>
                  <span className="text-slate-900 font-bold text-sm">
                    {createdResult.payload.quantityKg} {t('units.kg', 'kg')}
                  </span>
                </div>
              </div>

              <div className="flex flex-col sm:flex-row items-center justify-center gap-3 pt-2">
                <Link
                  to={`/beekeeper/batches/${createdResult.payload.batchId}`}
                  className="w-full sm:w-auto"
                >
                  <Button variant="primary" className="w-full bg-blue-600 hover:bg-blue-700 border-blue-600 text-white font-bold">
                    {t('common.viewDetails', 'View Details')} →
                  </Button>
                </Link>
                <Link to="/beekeeper/batches" className="w-full sm:w-auto">
                  <Button variant="secondary" className="w-full font-semibold">
                    {t('navigation.myBatches', 'Back to My Batches')}
                  </Button>
                </Link>
              </div>
            </Card>
          )
        ) : loadingHives ? (
          <LoadingSpinner text={t('loading.loading', 'Loading your active hives...')} />
        ) : hives.length === 0 ? (
          <Card className="text-center py-12 space-y-4 max-w-xl mx-auto">
            <div className="text-5xl">🐝</div>
            <h2 className="text-xl font-bold text-slate-900 font-['Outfit']">{t('hive.noHives', 'No Hives Registered')}</h2>
            <p className="text-slate-500 text-sm max-w-sm mx-auto">
              {t('validation.selectHive', 'You must register at least one hive before creating a honey batch.')}
            </p>
            <Link to="/hives">
              <Button variant="primary" className="mt-2 bg-blue-600 hover:bg-blue-700 border-blue-600 text-white font-bold">{t('navigation.myHives', 'Go to My Hives')}</Button>
            </Link>
          </Card>
        ) : (
          <div className="grid grid-cols-1 lg:grid-cols-12 gap-6 items-start">
            {/* Main Form Column */}
            <div className="lg:col-span-8">
              <Card className="p-6 bg-white border border-slate-200/90 shadow-sm">
                {!isOnline && (
                  <div className="mb-4 p-3 rounded-xl bg-amber-50 border border-amber-200 text-amber-800 text-xs flex items-center justify-between font-medium">
                    <span>🔴 <strong>Offline Mode:</strong> Form submissions will be saved locally and synced automatically when internet returns.</span>
                  </div>
                )}
                <BatchForm
                  hives={hives}
                  onSubmit={handleCreate}
                  loading={loading || submitting}
                  onCancel={() => navigate('/beekeeper/batches')}
                  submitLabel={isOnline ? t('batch.submitBatch', 'Create Harvest Batch →') : t('batch.submitBatch', 'Save Batch Offline 💾')}
                />
              </Card>
            </div>

            {/* Sidebar Guidance & Standards Column */}
            <div className="lg:col-span-4 space-y-6">
              <Card className="p-5 border border-amber-200/70 bg-gradient-to-br from-amber-50/50 to-white space-y-3">
                <div className="flex items-center gap-2">
                  <span className="text-xl">📋</span>
                  <h3 className="font-bold text-slate-900 font-['Outfit'] text-sm">Harvest Protocol Guidelines</h3>
                </div>
                <ul className="text-xs text-slate-600 space-y-2 list-disc list-inside leading-relaxed">
                  <li>Ensure honeycomb frames are at least 75% capped for optimal moisture content.</li>
                  <li>Record accurate harvest weights directly from apiary scales.</li>
                  <li>After creation, your batch will generate a unique cryptographic ID on HoneyChain.</li>
                  <li>Submit the batch for Laboratory Purity Testing to receive KVIC authenticity certification.</li>
                </ul>
              </Card>

              <Card className="p-5 border border-blue-200/70 bg-gradient-to-br from-blue-50/40 to-white space-y-2">
                <div className="flex items-center gap-2">
                  <span className="text-xl">🛡️</span>
                  <h3 className="font-bold text-slate-900 font-['Outfit'] text-sm">Blockchain Verification</h3>
                </div>
                <p className="text-xs text-slate-600 leading-relaxed">
                  Every batch log is signed and stamped on-chain. Customers will scan your QR code to verify floral origin and apiary coordinates.
                </p>
              </Card>
            </div>
          </div>
        )}
      </div>
    </BeekeeperLayout>
  )
}

export default CreateBatchPage
