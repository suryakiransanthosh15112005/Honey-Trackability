import React, { useEffect, useState } from 'react'
import { useParams, Link } from 'react-router-dom'
import LabLayout from '../../../layouts/LabLayout'
import LabTestForm from '../components/LabTestForm'
import LabResultBadge from '../components/LabResultBadge'
import Card from '../../../components/ui/Card'
import Button from '../../../components/ui/Button'
import Alert from '../../../components/feedback/Alert'
import LoadingSpinner from '../../../components/feedback/LoadingSpinner'
import { useLabTests } from '../hooks/useLabTests'
import batchApi from '../../batch/api/batchApi'
import labApi from '../api/labApi'

export const LabTestDetailsPage = () => {
  const { batchId } = useParams()
  const { submitLabTest, loading, error, clearError } = useLabTests()

  const [batch, setBatch] = useState(null)
  const [existingTest, setExistingTest] = useState(null)
  const [loadingDetails, setLoadingDetails] = useState(true)
  const [submissionSuccess, setSubmissionSuccess] = useState(null)

  useEffect(() => {
    const loadBatchAndTest = async () => {
      try {
        setLoadingDetails(true)
        // Fetch batch details
        const batchRes = await batchApi.getBatch(batchId)
        setBatch(batchRes.data.data)

        // Attempt to fetch existing test if already tested
        try {
          const testRes = await labApi.getLabTest(batchId)
          setExistingTest(testRes.data.data)
        } catch (e) {
          // No test exists yet
          setExistingTest(null)
        }
      } catch (err) {
        // Handled in UI
      } finally {
        setLoadingDetails(false)
      }
    }
    loadBatchAndTest()
  }, [batchId])

  const handleSubmitTest = async (formData) => {
    const result = await submitLabTest(batchId, formData)
    if (!result.error) {
      setExistingTest(result.payload)
      setSubmissionSuccess(result.payload)
    }
  }

  return (
    <LabLayout>
      <div className="w-full space-y-6">
        {/* Breadcrumb */}
        <nav className="flex items-center gap-2 text-xs text-slate-500">
          <Link to="/lab/dashboard" className="hover:text-blue-600 transition-colors font-medium">
            Lab Dashboard
          </Link>
          <span>/</span>
          <Link to="/lab/tests/pending" className="hover:text-blue-600 transition-colors font-medium">
            Pending Tests
          </Link>
          <span>/</span>
          <span className="text-slate-800 font-mono font-semibold">{batchId}</span>
        </nav>

        {error && <Alert type="error" message={error} onClose={clearError} />}

        {loadingDetails ? (
          <LoadingSpinner text="Loading batch details for laboratory analysis..." />
        ) : !batch ? (
          <Card className="text-center py-16 max-w-xl mx-auto">
            <div className="text-5xl mb-4">🧪</div>
            <h2 className="text-xl font-bold text-slate-900 font-['Outfit']">Batch Not Found</h2>
            <p className="text-slate-500 text-sm mt-2">
              Could not find honey batch with ID <span className="font-mono">{batchId}</span>.
            </p>
            <Link to="/lab/tests/pending">
              <Button variant="secondary" className="mt-4">
                ← Back to Pending Tests
              </Button>
            </Link>
          </Card>
        ) : (
          <div className="grid grid-cols-1 lg:grid-cols-12 gap-6 items-start">
            {/* Main Column */}
            <div className="lg:col-span-8 space-y-6">
              {/* Batch Info Header */}
              <Card className="p-6 bg-white border border-slate-200/90 shadow-sm">
                <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
                  <div className="flex items-center gap-4">
                    {batch.photoUrl ? (
                      <img
                        src={batch.photoUrl}
                        alt={batch.batchId}
                        className="w-16 h-16 rounded-2xl object-cover border border-amber-200 shadow-md"
                      />
                    ) : (
                      <div className="w-16 h-16 rounded-2xl bg-blue-50 flex items-center justify-center text-3xl border border-blue-200">
                        🍯
                      </div>
                    )}
                    <div>
                      <p className="text-amber-800 font-mono font-black text-xl tracking-wide">
                        {batch.batchId}
                      </p>
                      <p className="text-slate-900 font-semibold text-sm mt-0.5">
                        Hive: {batch.hiveCode || 'Active Hive'}{' '}
                        {batch.clusterName ? `· ${batch.clusterName}` : ''}
                      </p>
                    </div>
                  </div>

                  <div className="flex items-center gap-3">
                    <span className="px-3 py-1 rounded-full text-xs font-bold bg-blue-50 border border-blue-200 text-blue-700">
                      Status: {batch.status}
                    </span>
                  </div>
                </div>

                {/* Batch Summary Info */}
                <div className="grid grid-cols-2 sm:grid-cols-4 gap-3 mt-5 pt-4 border-t border-slate-100 text-xs">
                  <div>
                    <p className="text-slate-500 font-medium">Quantity</p>
                    <p className="text-slate-900 font-mono font-bold mt-0.5">{batch.quantityKg} KG</p>
                  </div>
                  <div>
                    <p className="text-slate-500 font-medium">Harvest Date</p>
                    <p className="text-slate-900 mt-0.5">{batch.harvestDate}</p>
                  </div>
                  <div>
                    <p className="text-slate-500 font-medium">Cluster</p>
                    <p className="text-slate-900 mt-0.5">{batch.clusterName || 'Apiary'}</p>
                  </div>
                  <div>
                    <p className="text-slate-500 font-medium">Registered On</p>
                    <p className="text-slate-900 mt-0.5">{batch.createdAt ? new Date(batch.createdAt).toLocaleDateString('en-IN') : '—'}</p>
                  </div>
                </div>
              </Card>

              {/* Test Form or Completed Test Display */}
              {existingTest ? (
                /* Already Tested Result Card */
                <Card className="p-6 border border-blue-200 bg-blue-50/40 space-y-6 shadow-sm">
                  <div className="flex items-center justify-between">
                    <div className="flex items-center gap-3">
                      <span className="text-3xl">🧪</span>
                      <div>
                        <h2 className="text-xl font-bold text-slate-900 font-['Outfit']">
                          Laboratory Analysis Completed
                        </h2>
                        <p className="text-xs text-slate-500">
                          Tested on {existingTest.testedAt ? new Date(existingTest.testedAt).toLocaleString('en-IN') : '—'}
                        </p>
                      </div>
                    </div>
                    <LabResultBadge result={existingTest.result} size="lg" />
                  </div>

                  <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                    <div className="p-4 rounded-xl bg-white border border-blue-200 shadow-sm">
                      <p className="text-xs text-slate-500 mb-1 font-medium">Purity Score</p>
                      <p className="text-3xl font-black text-blue-600 font-mono">
                        {existingTest.purityScore}%
                      </p>
                    </div>
                    <div className="p-4 rounded-xl bg-white border border-blue-200 shadow-sm">
                      <p className="text-xs text-slate-500 mb-1 font-medium">Certification Result</p>
                      <div className="mt-1">
                        <LabResultBadge result={existingTest.result} />
                      </div>
                    </div>
                  </div>

                  {existingTest.remarks && (
                    <div className="p-4 rounded-xl bg-white border border-slate-200 text-xs space-y-1 shadow-sm">
                      <p className="text-slate-500 font-medium">Laboratory Remarks:</p>
                      <p className="text-slate-800 leading-relaxed">{existingTest.remarks}</p>
                    </div>
                  )}

                  {existingTest.certificateUrl && (
                    <div className="flex items-center justify-between p-4 rounded-xl bg-blue-50 border border-blue-200 shadow-sm">
                      <div className="flex items-center gap-2 text-xs text-slate-900">
                        <span>📄</span>
                        <span className="font-semibold">Official Certificate Document</span>
                      </div>
                      <a
                        href={existingTest.certificateUrl}
                        target="_blank"
                        rel="noopener noreferrer"
                        className="text-xs text-blue-600 hover:text-blue-800 font-bold underline"
                      >
                        View Certificate ↗
                      </a>
                    </div>
                  )}

                  {/* Immutable Blockchain Proof */}
                  {existingTest.blockchainRecord && (
                    <div className="p-4 rounded-xl bg-white border border-amber-200 text-xs space-y-2 shadow-sm">
                      <div className="flex items-center justify-between">
                        <span className="font-bold text-amber-800 flex items-center gap-1.5">
                          <span>🔗</span> Blockchain Proof ({existingTest.blockchainRecord.network})
                        </span>
                        <span className="font-mono text-slate-500">
                          Block #{existingTest.blockchainRecord.blockNumber}
                        </span>
                      </div>
                      <div className="font-mono text-[10px] space-y-1 text-slate-600 break-all">
                        <p>Data Hash: <span className="text-slate-900 font-bold">{existingTest.blockchainRecord.dataHash}</span></p>
                        <p>Tx Hash: <span className="text-slate-900 font-bold">{existingTest.blockchainRecord.transactionHash}</span></p>
                      </div>
                    </div>
                  )}

                  <div className="pt-2">
                    <Link to="/lab/tests/pending">
                      <Button variant="secondary" className="w-full font-semibold">
                        ← Back to Pending Tests
                      </Button>
                    </Link>
                  </div>
                </Card>
              ) : (
                /* Active Lab Form */
                <Card className="p-6 bg-white border border-slate-200 shadow-sm">
                  <h2 className="text-xl font-bold text-slate-900 font-['Outfit'] mb-4 flex items-center gap-2">
                    <span>🧪</span> Enter Laboratory Purity Analysis
                  </h2>
                  <LabTestForm
                    onSubmit={handleSubmitTest}
                    loading={loading}
                    batchId={batchId}
                  />
                </Card>
              )}
            </div>

            {/* Side Column: Laboratory Standards Reference */}
            <div className="lg:col-span-4 space-y-6">
              <Card className="p-5 border border-blue-200/80 bg-gradient-to-br from-blue-50/50 to-white space-y-3">
                <div className="flex items-center gap-2">
                  <span className="text-xl">🔬</span>
                  <h3 className="font-bold text-slate-900 font-['Outfit'] text-sm">FSSAI / KVIC Honey Standards</h3>
                </div>
                <ul className="text-xs text-slate-600 space-y-2 leading-relaxed">
                  <li><strong>Moisture:</strong> Must not exceed 20.0% by mass.</li>
                  <li><strong>Total Reducing Sugars:</strong> Minimum 65.0% by mass.</li>
                  <li><strong>Sucrose:</strong> Maximum 5.0% by mass.</li>
                  <li><strong>Hydroxymethylfurfural (HMF):</strong> Maximum 80 mg/kg.</li>
                </ul>
              </Card>

              <Card className="p-5 border border-amber-200/80 bg-gradient-to-br from-amber-50/40 to-white space-y-2">
                <div className="flex items-center gap-2">
                  <span className="text-xl">🛡️</span>
                  <h3 className="font-bold text-slate-900 font-['Outfit'] text-sm">Blockchain Smart Contract</h3>
                </div>
                <p className="text-xs text-slate-600 leading-relaxed">
                  Upon submitting this test, the result and cryptographic purity hash will be immutably recorded on the ledger.
                </p>
              </Card>
            </div>
          </div>
        )}
      </div>
    </LabLayout>
  )
}

export default LabTestDetailsPage
