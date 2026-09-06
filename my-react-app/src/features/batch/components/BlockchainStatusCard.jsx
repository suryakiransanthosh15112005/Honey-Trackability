import React, { useState, useEffect } from 'react'
import Card from '../../../components/ui/Card'
import Button from '../../../components/ui/Button'
import batchApi from '../api/batchApi'

export const BlockchainStatusCard = ({ batchId }) => {
  const [record, setRecord] = useState(null)
  const [loading, setLoading] = useState(true)
  const [verifying, setVerifying] = useState(false)
  const [verificationResult, setVerificationResult] = useState(null)
  const [copiedKey, setCopiedKey] = useState(null)
  const [error, setError] = useState(null)

  useEffect(() => {
    if (!batchId) return
    const fetchRecord = async () => {
      try {
        setLoading(true)
        const res = await batchApi.getBlockchainRecord(batchId)
        setRecord(res.data.data)
      } catch (err) {
        setError('Blockchain record not yet available.')
      } finally {
        setLoading(false)
      }
    }
    fetchRecord()
  }, [batchId])

  const handleVerify = async () => {
    try {
      setVerifying(true)
      setVerificationResult(null)
      const res = await batchApi.verifyBlockchainRecord(batchId)
      setVerificationResult(res.data.data)
    } catch (err) {
      setVerificationResult({
        verified: false,
        message: err.response?.data?.message || 'Verification request failed',
      })
    } finally {
      setVerifying(false)
    }
  }

  const handleCopy = (text, key) => {
    navigator.clipboard.writeText(text)
    setCopiedKey(key)
    setTimeout(() => setCopiedKey(null), 2000)
  }

  const shortenHash = (hash, prefixLen = 10, suffixLen = 10) => {
    if (!hash || hash.length <= prefixLen + suffixLen) return hash
    return `${hash.substring(0, prefixLen)}...${hash.substring(hash.length - suffixLen)}`
  }

  if (loading) {
    return (
      <Card className="p-6 border border-slate-200 bg-white shadow-sm">
        <div className="flex items-center gap-3 text-sm text-slate-500 animate-pulse">
          <span>🔗</span> Loading blockchain record...
        </div>
      </Card>
    )
  }

  if (error || !record || !record.recorded) {
    return (
      <Card className="p-6 border border-dashed border-slate-200 bg-white shadow-sm">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-3">
            <span className="text-2xl">🔗</span>
            <div>
              <p className="text-slate-900 font-semibold text-sm font-['Outfit']">
                Blockchain Record
              </p>
              <p className="text-xs text-slate-500">
                {error || 'No blockchain record found for this batch.'}
              </p>
            </div>
          </div>
        </div>
      </Card>
    )
  }

  return (
    <Card className="p-6 border border-amber-200 bg-white shadow-sm space-y-5">
      {/* Header */}
      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-3 pb-3 border-b border-slate-100">
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 rounded-xl bg-amber-50 border border-amber-200 flex items-center justify-center text-xl">
            🔗
          </div>
          <div>
            <h3 className="text-slate-900 font-bold text-base font-['Outfit'] flex items-center gap-2">
              Blockchain Ledger
              <span className="text-xs px-2 py-0.5 rounded-full bg-blue-50 text-blue-700 font-bold border border-blue-200 flex items-center gap-1">
                <span className="w-1.5 h-1.5 rounded-full bg-blue-600 animate-pulse" />
                Immutable Proof
              </span>
            </h3>
            <p className="text-[11px] text-slate-500 mt-0.5">
              Network: <span className="font-mono text-blue-600 font-bold">{record.network}</span> · Block #{record.blockNumber}
            </p>
          </div>
        </div>

        <Button
          variant="primary"
          size="sm"
          loading={verifying}
          onClick={handleVerify}
          className="w-full sm:w-auto font-bold flex items-center gap-1.5"
        >
          {verifying ? 'Recalculating SHA-256...' : '🛡️ Verify Record'}
        </Button>
      </div>

      {/* Verification Result Banner */}
      {verificationResult && (
        <div
          className={`p-4 rounded-xl border text-xs space-y-1.5 animate-fade-in ${
            verificationResult.verified
              ? 'bg-blue-50 border-blue-200 text-blue-900'
              : 'bg-amber-50 border-amber-200 text-amber-900'
          }`}
        >
          <div className="flex items-center gap-2 font-bold text-sm">
            <span>{verificationResult.verified ? '✅' : '❌'}</span>
            <span>{verificationResult.verified ? 'Blockchain Hash Match Confirmed' : 'Tamper Detected!'}</span>
          </div>
          <p className="text-slate-700 leading-relaxed">{verificationResult.message}</p>
          {verificationResult.calculatedHash && (
            <div className="pt-2 font-mono text-[10px] space-y-0.5 text-slate-500">
              <p>Stored Hash: <span className="text-slate-900 font-bold">{verificationResult.storedHash}</span></p>
              <p>Computed Hash: <span className={verificationResult.verified ? 'text-blue-700 font-bold' : 'text-amber-800 font-bold'}>{verificationResult.calculatedHash}</span></p>
            </div>
          )}
        </div>
      )}

      {/* Crypto Details Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
        {/* Data Hash */}
        <div className="p-3.5 rounded-xl bg-slate-50 border border-slate-200 space-y-1">
          <div className="flex items-center justify-between text-[11px] text-slate-500">
            <span className="font-bold uppercase tracking-wider">SHA-256 Data Hash</span>
            <button
              onClick={() => handleCopy(record.dataHash, 'dataHash')}
              className="text-blue-600 hover:underline font-medium"
            >
              {copiedKey === 'dataHash' ? '✓ Copied' : 'Copy Full'}
            </button>
          </div>
          <p className="font-mono text-xs text-slate-900 break-all select-all font-semibold">
            {shortenHash(record.dataHash, 14, 14)}
          </p>
        </div>

        {/* Transaction Hash */}
        <div className="p-3.5 rounded-xl bg-slate-50 border border-slate-200 space-y-1">
          <div className="flex items-center justify-between text-[11px] text-slate-500">
            <span className="font-bold uppercase tracking-wider">Transaction Hash</span>
            <button
              onClick={() => handleCopy(record.transactionHash, 'txHash')}
              className="text-blue-600 hover:underline font-medium"
            >
              {copiedKey === 'txHash' ? '✓ Copied' : 'Copy Full'}
            </button>
          </div>
          <p className="font-mono text-xs text-slate-900 break-all select-all font-semibold">
            {shortenHash(record.transactionHash, 14, 14)}
          </p>
        </div>
      </div>

      {/* Bottom Metadata */}
      <div className="flex flex-wrap items-center justify-between text-[11px] text-slate-500 pt-1">
        <span>Recorded: {record.recordedAt ? new Date(record.recordedAt).toLocaleString('en-IN') : '—'}</span>
        <span className="font-mono">Record Type: {record.recordType}</span>
      </div>
    </Card>
  )
}

export default BlockchainStatusCard
