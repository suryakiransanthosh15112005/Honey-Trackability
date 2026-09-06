import React, { useEffect, useState } from 'react'
import { useParams, Link } from 'react-router-dom'
import BeekeeperLayout from '../../../layouts/BeekeeperLayout'
import BatchStatusBadge from '../components/BatchStatusBadge'
import BatchForm from '../components/BatchForm'
import BlockchainStatusCard from '../components/BlockchainStatusCard'
import LabResultBadge from '../../lab/components/LabResultBadge'
import QrCodeCard from '../components/QrCodeCard'
import VerificationHistoryCard from '../../verification/components/VerificationHistoryCard'
import Card from '../../../components/ui/Card'
import Button from '../../../components/ui/Button'
import Alert from '../../../components/feedback/Alert'
import LoadingSpinner from '../../../components/feedback/LoadingSpinner'
import { useBatches } from '../hooks/useBatches'
import labApi from '../../lab/api/labApi'

export const BatchDetailsPage = () => {
  const { batchId } = useParams()
  const {
    selectedBatch: batch,
    loading,
    error,
    fetchBatch,
    updateBatch,
    sendForTesting,
    clearError,
  } = useBatches()

  const [isEditing, setIsEditing] = useState(false)
  const [showConfirmTesting, setShowConfirmTesting] = useState(false)
  const [successMsg, setSuccessMsg] = useState(null)
  const [labResult, setLabResult] = useState(null)
  const [loadingLabResult, setLoadingLabResult] = useState(false)

  useEffect(() => {
    fetchBatch(batchId)
  }, [batchId, fetchBatch])

  useEffect(() => {
    if (batch && ['PURE', 'UNDER_REVIEW', 'FAILED'].includes(batch.status)) {
      setLoadingLabResult(true)
      labApi.getBeekeeperLabResult(batchId)
        .then((res) => setLabResult(res.data.data))
        .catch(() => setLabResult(null))
        .finally(() => setLoadingLabResult(false))
    }
  }, [batch, batchId])

  const handleUpdate = async (formData) => {
    const result = await updateBatch(batchId, formData)
    if (!result.error) {
      setIsEditing(false)
      setSuccessMsg('Batch details updated successfully!')
      setTimeout(() => setSuccessMsg(null), 3000)
    }
  }

  const handleSendForTesting = async () => {
    const result = await sendForTesting(batchId)
    if (!result.error) {
      setShowConfirmTesting(false)
      setSuccessMsg('Batch has been sent for laboratory testing!')
      setTimeout(() => setSuccessMsg(null), 4000)
    }
  }

  const isCreated = batch?.status === 'CREATED'

  return (
    <BeekeeperLayout>
      <div className="w-full space-y-6">
        {/* Breadcrumb */}
        <nav className="flex items-center gap-2 text-xs text-slate-500">
          <Link to="/beekeeper/batches" className="hover:text-blue-600 transition-colors">
            My Honey Batches
          </Link>
          <span>/</span>
          <span className="text-slate-800 font-mono font-medium">{batchId}</span>
        </nav>

        {error && <Alert type="error" message={error} onClose={clearError} />}
        {successMsg && <Alert type="success" message={successMsg} />}

        {loading && !batch ? (
          <LoadingSpinner text="Loading batch details..." />
        ) : !batch ? (
          <Card className="text-center py-16 bg-white border border-slate-200 shadow-sm max-w-xl mx-auto">
            <div className="text-5xl mb-4">🍯</div>
            <h2 className="text-xl font-bold text-slate-900 font-['Outfit']">Batch Not Found</h2>
            <p className="text-slate-500 text-sm mt-2">
              This batch does not exist or does not belong to your account.
            </p>
            <Link to="/beekeeper/batches">
              <Button variant="secondary" className="mt-4">
                ← Back to Batches
              </Button>
            </Link>
          </Card>
        ) : (
          <div className="space-y-6">
            {/* Batch Main Header Card */}
            <Card className="p-6 bg-white border border-slate-200/90 shadow-sm">
              <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
                <div className="flex items-center gap-4">
                  {batch.photoUrl && (batch.photoUrl.startsWith('http') || batch.photoUrl.startsWith('data:') || batch.photoUrl.startsWith('/')) ? (
                    <img
                      src={batch.photoUrl}
                      alt={batch.batchId}
                      className="w-16 h-16 rounded-2xl object-cover border border-slate-200 shadow-sm"
                      onError={(e) => {
                        e.currentTarget.style.display = 'none'
                        const fallback = e.currentTarget.parentElement?.querySelector('.photo-fallback')
                        if (fallback) fallback.classList.remove('hidden')
                      }}
                    />
                  ) : null}
                  <div
                    className={`photo-fallback w-16 h-16 rounded-2xl bg-gradient-to-br from-amber-50 to-amber-100 border border-amber-200 items-center justify-center text-3xl shadow-2xs ${batch.photoUrl && (batch.photoUrl.startsWith('http') || batch.photoUrl.startsWith('data:') || batch.photoUrl.startsWith('/')) ? 'hidden' : 'flex'
                      }`}
                  >
                    🍯
                  </div>
                  <div>
                    <p className="text-blue-600 font-mono font-black text-xl tracking-tight">
                      {batch.batchId}
                    </p>
                    <p className="text-slate-900 font-semibold text-sm mt-0.5">
                      Hive: {batch.hiveCode || 'Active Hive'}{' '}
                      {batch.clusterName ? `· ${batch.clusterName}` : ''}
                    </p>
                  </div>
                </div>

                <div className="flex items-center gap-3">
                  <BatchStatusBadge status={batch.status} size="lg" />
                  {isCreated && !isEditing && (
                    <Button
                      variant="primary"
                      size="sm"
                      onClick={() => setIsEditing(true)}
                    >
                      ✏️ Edit
                    </Button>
                  )}
                </div>
              </div>
            </Card>

            {/* Editing Form */}
            {isEditing && (
              <Card className="p-6 bg-white border border-slate-200 shadow-sm">
                <h2 className="text-lg font-bold text-slate-900 font-['Outfit'] mb-4">
                  Edit Batch Details
                </h2>
                <BatchForm
                  initialValues={batch}
                  onSubmit={handleUpdate}
                  loading={loading}
                  isEdit={true}
                  onCancel={() => setIsEditing(false)}
                  submitLabel="Save Batch Changes"
                />
              </Card>
            )}

            {/* Two-Column Responsive Grid on Desktop */}
            <div className="grid grid-cols-1 lg:grid-cols-12 gap-6 items-start">
              {/* Left Column: Attributes, Blockchain, Analytics */}
              <div className="lg:col-span-7 space-y-6">
                {!isEditing && (
                  /* Overview Attributes Grid */
                  <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                    {[
                      { label: 'Batch ID', value: batch.batchId, mono: true },
                      {
                        label: 'Status',
                        value: <BatchStatusBadge status={batch.status} />,
                      },
                      {
                        label: 'Harvest Quantity',
                        value: `${batch.quantityKg} KG`,
                        highlight: true,
                      },
                      {
                        label: 'Harvest Date',
                        value: batch.harvestDate
                          ? new Date(batch.harvestDate).toLocaleDateString('en-IN', {
                            day: '2-digit',
                            month: 'long',
                            year: 'numeric',
                          })
                          : '—',
                      },
                      {
                        label: 'Source Hive',
                        value: `${batch.hiveCode || 'Hive'} (${batch.clusterName || 'Apiary'})`,
                      },
                      {
                        label: 'Registered On',
                        value: batch.createdAt
                          ? new Date(batch.createdAt).toLocaleDateString('en-IN')
                          : '—',
                      },
                    ].map(({ label, value, mono, highlight }) => (
                      <div
                        key={label}
                        className="p-4 rounded-2xl bg-white border border-slate-200/80 shadow-2xs hover:shadow-xs transition-shadow"
                      >
                        <p className="text-xs text-slate-500 mb-1 font-medium">{label}</p>
                        <div
                          className={`text-slate-900 font-semibold ${mono ? 'font-mono text-sm' : ''
                            } ${highlight ? 'text-amber-600 text-lg font-mono font-black' : ''}`}
                        >
                          {value}
                        </div>
                      </div>
                    ))}
                  </div>
                )}

                {/* BLOCKCHAIN VERIFICATION SECTION */}
                <div className="space-y-2">
                  <h2 className="text-lg font-bold text-slate-900 font-['Outfit'] flex items-center gap-2">
                    <span>🛡️</span> Blockchain Integrity & Proof
                  </h2>
                  <BlockchainStatusCard batchId={batch.batchId} />
                </div>

                {/* BEEKEEPER VERIFICATION ANALYTICS & ANTI-COUNTERFEIT MONITORING */}
                {batch?.status === 'QR_GENERATED' && (
                  <div className="space-y-2">
                    <h2 className="text-lg font-bold text-slate-900 font-['Outfit'] flex items-center gap-2">
                      <span>📊</span> Verification Activity & Anti-Counterfeit Signals
                    </h2>
                    <VerificationHistoryCard batchId={batch.batchId} />
                  </div>
                )}

                {/* Reserved Sections */}
                <div className="space-y-3 pt-2">
                  {[
                    {
                      title: '🛍️ Decentralized Honey Marketplace',
                      desc: 'Available in Phase 10',
                    },
                  ].map(({ title, desc }) => (
                    <div
                      key={title}
                      className="bg-white rounded-2xl p-5 border border-dashed border-slate-200 flex flex-col sm:flex-row items-start sm:items-center justify-between gap-2"
                    >
                      <span className="text-slate-900 font-semibold text-sm font-['Outfit']">
                        {title}
                      </span>
                      <span className="text-xs text-slate-500 bg-slate-100 px-3 py-1 rounded-full">
                        {desc}
                      </span>
                    </div>
                  ))}
                </div>
              </div>

              {/* Right Column: QR Code Passport & Lab Testing */}
              <div className="lg:col-span-5 space-y-6">
                {/* SMART QR CODE PASSPORT SECTION */}
                <QrCodeCard
                  batch={batch}
                  onQrGenerated={() => fetchBatch(batchId)}
                />

                {/* LAB TESTING / PURITY ANALYSIS SECTION */}
                <div className="space-y-2">
                  <h2 className="text-lg font-bold text-slate-900 font-['Outfit'] flex items-center gap-2">
                    <span>🧪</span> Laboratory Purity Analysis
                  </h2>

                  {loadingLabResult ? (
                    <Card className="p-6 text-center text-xs text-slate-500 bg-white border border-slate-200">
                      Loading laboratory purity report...
                    </Card>
                  ) : labResult ? (
                    <Card className="p-6 border border-blue-200 bg-blue-50/30 space-y-4">
                      <div className="flex items-center justify-between">
                        <div>
                          <p className="text-slate-900 font-bold text-base font-['Outfit']">
                            Purity Quality Certificate
                          </p>
                          <p className="text-xs text-slate-500">
                            Tested on {labResult.testedAt ? new Date(labResult.testedAt).toLocaleDateString('en-IN', { day: '2-digit', month: 'long', year: 'numeric' }) : '—'}
                          </p>
                        </div>
                        <LabResultBadge result={labResult.result} size="lg" />
                      </div>

                      <div className="grid grid-cols-2 gap-3 pt-1">
                        <div className="p-3.5 rounded-xl bg-white border border-blue-200">
                          <p className="text-[11px] text-slate-500 font-medium">Purity Score</p>
                          <p className="text-2xl font-black text-blue-700 font-mono mt-0.5">
                            {labResult.purityScore}%
                          </p>
                        </div>
                        <div className="p-3.5 rounded-xl bg-white border border-blue-200">
                          <p className="text-[11px] text-slate-500 font-medium">Test Result</p>
                          <p className="text-lg font-bold text-slate-900 mt-0.5">
                            {labResult.result}
                          </p>
                        </div>
                      </div>

                      {labResult.remarks && (
                        <div className="p-3 rounded-xl bg-white border border-slate-200 text-xs">
                          <span className="text-slate-500">Lab Notes: </span>
                          <span className="text-slate-900">{labResult.remarks}</span>
                        </div>
                      )}

                      {labResult.certificateUrl && (
                        <div className="flex items-center justify-between p-3 rounded-xl bg-blue-50 border border-blue-200 text-xs">
                          <span className="text-slate-900 font-semibold flex items-center gap-1.5">
                            <span>📄</span> Official Lab Certificate
                          </span>
                          <a
                            href={labResult.certificateUrl}
                            target="_blank"
                            rel="noopener noreferrer"
                            className="text-blue-600 hover:underline font-bold"
                          >
                            View Document ↗
                          </a>
                        </div>
                      )}
                    </Card>
                  ) : batch.status === 'SENT_FOR_TESTING' ? (
                    <Card className="p-6 border border-blue-200 bg-blue-50/50 flex items-center justify-between">
                      <div className="flex items-center gap-3">
                        <span className="text-3xl">⏳</span>
                        <div>
                          <p className="text-slate-900 font-semibold text-sm">Testing in Progress</p>
                          <p className="text-xs text-slate-500">This batch is queued at the government laboratory for purity analysis.</p>
                        </div>
                      </div>
                      <span className="px-3 py-1 rounded-full text-xs font-bold bg-blue-100 border border-blue-300 text-blue-800 animate-pulse">
                        Awaiting Lab
                      </span>
                    </Card>
                  ) : (
                    <Card className="p-5 border border-dashed border-slate-200 text-xs text-slate-500 flex items-center justify-between bg-white">
                      <span>Honey batch has not yet been submitted to the laboratory.</span>
                      <span className="text-[11px] bg-slate-100 px-3 py-1 rounded-full text-slate-600">
                        Status: {batch.status}
                      </span>
                    </Card>
                  )}
                </div>

                {/* Testing Workflow Action Card */}
                {isCreated && !isEditing && (
                  <Card className="p-6 border border-blue-200 bg-blue-50/40">
                    <div className="space-y-3">
                      <div className="space-y-1">
                        <h3 className="text-slate-900 font-bold text-base font-['Outfit'] flex items-center gap-2">
                          <span>🧪</span> Send to Laboratory
                        </h3>
                        <p className="text-slate-600 text-xs">
                          Once submitted for testing, this batch will be queued for lab purity analysis
                          and cannot be edited further.
                        </p>
                      </div>
                      <Button
                        variant="primary"
                        onClick={() => setShowConfirmTesting(true)}
                        className="w-full font-bold"
                      >
                        Send for Testing →
                      </Button>
                    </div>
                  </Card>
                )}
              </div>
            </div>

            {/* Confirmation Modal */}
            {showConfirmTesting && (
              <div className="fixed inset-0 bg-slate-900/60 backdrop-blur-sm flex items-center justify-center p-4 z-50 animate-fade-in">
                <Card className="max-w-md w-full p-6 space-y-4 border border-slate-200 bg-white shadow-xl rounded-3xl">
                  <div className="text-3xl text-center">🧪</div>
                  <h3 className="text-xl font-bold text-slate-900 text-center font-['Outfit']">
                    Confirm Lab Submission
                  </h3>
                  <p className="text-slate-600 text-sm text-center">
                    Send batch <span className="font-mono text-blue-600 font-bold">{batch.batchId}</span> ({batch.quantityKg} KG) to the laboratory for purity testing?
                  </p>
                  <div className="flex items-center gap-3 pt-2">
                    <Button
                      variant="primary"
                      loading={loading}
                      onClick={handleSendForTesting}
                      className="flex-1 font-bold"
                    >
                      Yes, Send Batch
                    </Button>
                    <Button
                      variant="secondary"
                      onClick={() => setShowConfirmTesting(false)}
                      className="flex-1"
                    >
                      Cancel
                    </Button>
                  </div>
                </Card>
              </div>
            )}
          </div>
        )}
      </div>
    </BeekeeperLayout>
  )
}

export default BatchDetailsPage
