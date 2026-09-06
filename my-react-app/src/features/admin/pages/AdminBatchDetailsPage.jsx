import React, { useState, useEffect } from 'react'
import { useParams, Link } from 'react-router-dom'
import AdminLayout from '../../../layouts/AdminLayout'
import LoadingSpinner from '../../../components/feedback/LoadingSpinner'
import Alert from '../../../components/feedback/Alert'
import adminApi from '../api/adminApi'
import { useLanguage } from '../../../i18n/LanguageContext'

export const AdminBatchDetailsPage = () => {
  const { batchId } = useParams()
  const { t } = useLanguage()
  const [batch, setBatch] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
    adminApi.getBatchDetails(batchId)
      .then((res) => setBatch(res.data?.data))
      .catch((err) => setError(err?.response?.data?.message || t('errors.generic', 'Failed to load batch details')))
      .finally(() => setLoading(false))
  }, [batchId])

  if (loading) {
    return (
      <AdminLayout>
        <div className="py-20 text-center">
          <LoadingSpinner text={t('loading.loading', 'Loading batch audit details...')} />
        </div>
      </AdminLayout>
    )
  }

  if (error || !batch) {
    return (
      <AdminLayout>
        <div className="space-y-4 text-left">
          <Alert type="danger" message={error || t('errors.batchNotFound', 'Batch not found')} />
          <Link to="/admin/batches" className="inline-flex items-center gap-2 px-4 py-2 rounded-xl bg-slate-100 hover:bg-slate-200 text-slate-700 text-xs font-bold transition-colors">
            ← {t('common.back', 'Back to Batches List')}
          </Link>
        </div>
      </AdminLayout>
    )
  }

  return (
    <AdminLayout>
      <div className="space-y-6 text-left">
        {/* Navigation Breadcrumb */}
        <nav className="flex items-center gap-2 text-xs font-semibold text-slate-500">
          <Link to="/admin/dashboard" className="hover:text-slate-900 transition-colors">{t('admin.controlCenter', 'Admin')}</Link>
          <span>/</span>
          <Link to="/admin/batches" className="hover:text-slate-900 transition-colors">{t('navigation.batches', 'Batches')}</Link>
          <span>/</span>
          <span className="text-slate-900 font-bold font-mono">{batchId}</span>
        </nav>

        {/* Batch Overview Header Card */}
        <div className="bg-white rounded-2xl border border-slate-200/80 p-6 shadow-xs flex items-start justify-between flex-wrap gap-4">
          <div>
            <div className="flex items-center gap-2 mb-1.5">
              <span className="px-2.5 py-0.5 rounded-full bg-slate-100 border border-slate-200 text-slate-700 text-xs font-bold uppercase tracking-wider">
                Honey Batch Audit
              </span>
              <span className="text-xs text-slate-400 font-medium">• {batch.harvestDate}</span>
            </div>
            <h1 className="text-2xl font-black font-mono text-slate-900">{batch.batchId}</h1>
            <p className="text-slate-500 text-xs mt-1">
              Quantity: <strong className="text-amber-700 font-bold">{batch.quantityKg?.toFixed(1)} kg</strong>
            </p>
          </div>
          <div>
            <span className={`inline-flex items-center px-3.5 py-1.5 rounded-full text-xs font-extrabold ${
              batch.status === 'PURE'
                ? 'bg-emerald-50 text-emerald-700 border border-emerald-200'
                : batch.status === 'FAILED'
                ? 'bg-red-50 text-red-700 border border-red-200'
                : 'bg-amber-50 text-amber-800 border border-amber-200'
            }`}>
              {batch.status}
            </span>
          </div>
        </div>

        {/* 4 Pillars of HoneyChain Audit Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          {/* 1. Apiary & Beekeeper */}
          <div className="bg-white rounded-2xl border border-slate-200/80 p-6 shadow-xs space-y-4">
            <h3 className="text-sm font-extrabold text-slate-900 font-['Outfit'] flex items-center gap-2">
              <span>🧑‍🌾</span>
              <span>{t('admin.governanceAndAudit', 'Apiary & Beekeeper')}</span>
            </h3>
            <div className="text-xs space-y-2.5 divide-y divide-slate-100">
              <div className="flex justify-between items-center pt-2">
                <span className="text-slate-500 font-medium">{t('auth.beekeeperRole', 'Beekeeper')}:</span>
                <strong className="text-slate-900 font-bold">{batch.beekeeperName || 'N/A'}</strong>
              </div>
              <div className="flex justify-between items-center pt-2">
                <span className="text-slate-500 font-medium">{t('onboarding.village', 'Village')}:</span>
                <span className="text-slate-800">{batch.beekeeperVillage || 'N/A'}</span>
              </div>
              <div className="flex justify-between items-center pt-2">
                <span className="text-slate-500 font-medium">{t('onboarding.kvicId', 'KVIC ID')}:</span>
                <code className="px-1.5 py-0.5 rounded bg-slate-100 text-slate-800 font-mono border border-slate-200">{batch.beekeeperKvicId || 'N/A'}</code>
              </div>
              <div className="flex justify-between items-center pt-2">
                <span className="text-slate-500 font-medium">{t('hive.hiveCode', 'Hive Source')}:</span>
                <span className="text-slate-800 font-mono">{batch.hiveCode || `Hive #${batch.hiveId}`} ({batch.clusterName || 'Apiary'})</span>
              </div>
            </div>
          </div>

          {/* 2. Lab Testing & Purity */}
          <div className="bg-white rounded-2xl border border-slate-200/80 p-6 shadow-xs space-y-4">
            <h3 className="text-sm font-extrabold text-slate-900 font-['Outfit'] flex items-center gap-2">
              <span>🔬</span>
              <span>{t('admin.labAuditsTitle', 'Laboratory Analysis')}</span>
            </h3>
            {batch.labTested ? (
              <div className="text-xs space-y-2.5 divide-y divide-slate-100">
                <div className="flex justify-between items-center pt-2">
                  <span className="text-slate-500 font-medium">{t('lab.purityScore', 'Purity Score')}:</span>
                  <strong className="text-emerald-600 font-extrabold text-base">{batch.purityScore}%</strong>
                </div>
                <div className="flex justify-between items-center pt-2">
                  <span className="text-slate-500 font-medium">{t('lab.testResult', 'Test Outcome')}:</span>
                  <span className={`inline-flex items-center px-2 py-0.5 rounded-full text-xs font-bold ${
                    batch.labResult === 'PURE' ? 'bg-emerald-50 text-emerald-700 border border-emerald-200' : 'bg-red-50 text-red-700 border border-red-200'
                  }`}>
                    {batch.labResult}
                  </span>
                </div>
                <div className="flex justify-between items-center pt-2">
                  <span className="text-slate-500 font-medium">{t('lab.testingFacility', 'Facility')}:</span>
                  <span className="text-slate-800 font-medium">{batch.labName}</span>
                </div>
                {batch.testDate && (
                  <div className="flex justify-between items-center pt-2">
                    <span className="text-slate-500 font-medium">{t('lab.testedAt', 'Tested On')}:</span>
                    <span className="text-slate-800 font-mono">{new Date(batch.testDate).toLocaleDateString('en-IN')}</span>
                  </div>
                )}
              </div>
            ) : (
              <p className="text-slate-400 text-xs py-4 text-center">Laboratory analysis not yet conducted for this batch.</p>
            )}
          </div>

          {/* 3. Blockchain Ledger */}
          <div className="bg-white rounded-2xl border border-slate-200/80 p-6 shadow-xs space-y-4">
            <h3 className="text-sm font-extrabold text-slate-900 font-['Outfit'] flex items-center gap-2">
              <span>⛓️</span>
              <span>{t('blockchain.title', 'Blockchain Immutable Ledger')}</span>
            </h3>
            {batch.blockchainRecorded ? (
              <div className="text-xs space-y-2.5 divide-y divide-slate-100">
                <div className="flex justify-between items-center pt-2">
                  <span className="text-slate-500 font-medium">Ledger Status:</span>
                  <span className="inline-flex items-center gap-1 px-2 py-0.5 rounded-full text-xs font-bold bg-emerald-50 text-emerald-700 border border-emerald-200">
                    ✓ {t('blockchain.integrityVerified', 'Anchored on Chain')}
                  </span>
                </div>
                <div className="pt-2">
                  <span className="text-slate-500 block text-[11px] mb-1 font-medium">{t('blockchain.txHash', 'Transaction Hash')}:</span>
                  <code className="text-xs break-all text-amber-700 font-mono bg-slate-50 p-2 rounded-lg block border border-slate-100">{batch.transactionHash}</code>
                </div>
                <div className="pt-2">
                  <span className="text-slate-500 block text-[11px] mb-1 font-medium">Data Hash (SHA-256):</span>
                  <code className="text-xs break-all text-slate-700 font-mono bg-slate-50 p-2 rounded-lg block border border-slate-100">{batch.dataHash}</code>
                </div>
                {batch.blockNumber && (
                  <div className="flex justify-between items-center pt-2">
                    <span className="text-slate-500 font-medium">{t('blockchain.blockNumber', 'Block Number')}:</span>
                    <span className="font-mono font-bold text-slate-800">#{batch.blockNumber}</span>
                  </div>
                )}
              </div>
            ) : (
              <p className="text-slate-400 text-xs py-4 text-center">Pending blockchain transaction seal.</p>
            )}
          </div>

          {/* 4. Anti-Counterfeit & Scans */}
          <div className="bg-white rounded-2xl border border-slate-200/80 p-6 shadow-xs space-y-4">
            <h3 className="text-sm font-extrabold text-slate-900 font-['Outfit'] flex items-center gap-2">
              <span>🛡️</span>
              <span>{t('admin.antiCounterfeitRisk', 'Anti-Counterfeit & Scans')}</span>
            </h3>
            <div className="text-xs space-y-2.5 divide-y divide-slate-100">
              <div className="flex justify-between items-center pt-2">
                <span className="text-slate-500 font-medium">Public Consumer Scans:</span>
                <strong className="text-slate-900 font-bold">{batch.totalScans} times</strong>
              </div>
              <div className="flex justify-between items-center pt-2">
                <span className="text-slate-500 font-medium">{t('admin.riskLevel', 'Risk Level')}:</span>
                <span className={`inline-flex items-center px-2 py-0.5 rounded-full text-xs font-bold ${
                  batch.riskLevel === 'HIGH_RISK'
                    ? 'bg-red-50 text-red-700 border border-red-200'
                    : batch.riskLevel === 'WATCH'
                    ? 'bg-amber-50 text-amber-800 border border-amber-200'
                    : 'bg-emerald-50 text-emerald-700 border border-emerald-200'
                }`}>
                  {batch.riskLevel}
                </span>
              </div>
              {batch.publicVerificationUrl && (
                <div className="pt-3">
                  <a
                    href={batch.publicVerificationUrl}
                    target="_blank"
                    rel="noopener noreferrer"
                    className="w-full inline-flex items-center justify-center gap-2 px-3 py-2 rounded-xl bg-slate-100 hover:bg-slate-200 text-slate-800 text-xs font-bold transition-colors"
                  >
                    <span>🔗</span>
                    <span>Inspect Public Verification Portal ↗</span>
                  </a>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </AdminLayout>
  )
}

export default AdminBatchDetailsPage
