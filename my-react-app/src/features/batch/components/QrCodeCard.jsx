import React, { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import Card from '../../../components/ui/Card'
import Button from '../../../components/ui/Button'
import Alert from '../../../components/feedback/Alert'
import apiClient from '../../../services/axios'

export const QrCodeCard = ({ batch, onQrGenerated }) => {
  const [qrData, setQrData] = useState(null)
  const [loading, setLoading] = useState(false)
  const [generating, setGenerating] = useState(false)
  const [error, setError] = useState(null)
  const [showPrintModal, setShowPrintModal] = useState(false)

  const isEligible = batch?.status === 'PURE' || batch?.status === 'QR_GENERATED'

  useEffect(() => {
    if (batch?.batchId && (batch.status === 'QR_GENERATED' || batch.status === 'PURE')) {
      setLoading(true)
      apiClient.get(`/beekeepers/batches/${batch.batchId}/qr`)
        .then((res) => setQrData(res.data.data))
        .catch(() => setQrData(null))
        .finally(() => setLoading(false))
    }
  }, [batch])

  const handleGenerateQr = async () => {
    try {
      setGenerating(true)
      setError(null)
      const res = await apiClient.post(`/beekeepers/batches/${batch.batchId}/generate-qr`)
      setQrData(res.data.data)
      if (onQrGenerated) onQrGenerated(res.data.data)
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to generate QR code')
    } finally {
      setGenerating(false)
    }
  }

  const handleDownload = () => {
    if (!qrData?.qrImageUrl) return
    const link = document.createElement('a')
    link.href = qrData.qrImageUrl
    link.download = `HONEYCHAIN-QR-${batch.batchId}.png`
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
  }

  const handlePrint = () => {
    window.print()
  }

  return (
    <Card className="p-6 space-y-4 border border-amber-200 bg-white shadow-sm">
      <div className="flex items-center justify-between">
        <h2 className="text-lg font-bold text-slate-900 font-['Outfit'] flex items-center gap-2">
          <span>📱</span> Smart QR Code Traceability Passport
        </h2>
        {qrData ? (
          <span className="px-3 py-1 rounded-full text-xs font-bold bg-blue-50 border border-blue-200 text-blue-700">
            ● QR Active
          </span>
        ) : (
          <span className="px-3 py-1 rounded-full text-xs font-bold bg-amber-50 border border-amber-200 text-amber-800">
            {isEligible ? 'Ready to Generate' : 'Locked'}
          </span>
        )}
      </div>

      {error && <Alert type="error" message={error} onClose={() => setError(null)} />}

      {qrData ? (
        <div className="flex flex-col sm:flex-row items-center gap-6 p-4 rounded-2xl bg-slate-50 border border-slate-200">
          {/* QR Image */}
          <div className="p-3 rounded-2xl bg-white flex items-center justify-center shadow-md border border-slate-200">
            <img
              src={qrData.qrImageUrl}
              alt={`QR for ${batch.batchId}`}
              className="w-40 h-40 object-contain rounded-lg"
            />
          </div>

          {/* Details & Actions */}
          <div className="space-y-3 text-center sm:text-left flex-1 min-w-0">
            <div>
              <p className="text-xs text-blue-700 font-bold flex items-center justify-center sm:justify-start gap-1">
                <span>✅</span> Ready for Customer Verification
              </p>
              <p className="text-[11px] text-slate-500 mt-0.5 truncate font-mono">
                {qrData.qrValue}
              </p>
            </div>

            <div className="flex flex-wrap items-center justify-center sm:justify-start gap-2 pt-1">
              <Button size="sm" variant="primary" onClick={handleDownload} className="text-xs font-bold">
                📥 Download PNG
              </Button>
              <Button size="sm" variant="secondary" onClick={() => setShowPrintModal(true)} className="text-xs">
                🖨️ Print Label
              </Button>
              <Link to={`/verify/${batch.batchId}`} target="_blank" rel="noopener noreferrer">
                <Button size="sm" variant="secondary" className="text-xs text-blue-600 border-blue-200 hover:bg-blue-50">
                  View Public Page ↗
                </Button>
              </Link>
            </div>
          </div>
        </div>
      ) : isEligible ? (
        <div className="p-6 rounded-2xl border border-dashed border-blue-200 bg-blue-50/50 text-center space-y-3">
          <span className="text-4xl block">✨</span>
          <h3 className="text-base font-bold text-slate-900 font-['Outfit']">
            Purity Verified — Generate QR Passport
          </h3>
          <p className="text-xs text-slate-600 max-w-md mx-auto">
            This honey batch passed laboratory quality verification. Generate its permanent, cryptographic QR passport for consumers to verify on their smartphones.
          </p>
          <Button
            variant="primary"
            loading={generating}
            onClick={handleGenerateQr}
            className="mt-2 font-bold"
          >
            {generating ? 'Generating QR Code...' : '📱 Generate Smart QR Code'}
          </Button>
        </div>
      ) : (
        <div className="p-5 rounded-2xl border border-dashed border-slate-200 text-xs text-slate-500 flex items-center justify-between">
          <span>QR code generation unlocks once laboratory testing confirms PURE status.</span>
          <span className="text-[11px] bg-slate-100 px-3 py-1 rounded-full text-slate-600">
            Current: {batch?.status}
          </span>
        </div>
      )}

      {/* Print Jar Label Modal */}
      {showPrintModal && qrData && (
        <div className="fixed inset-0 bg-slate-900/60 backdrop-blur-sm flex items-center justify-center p-4 z-50 animate-fade-in">
          <div className="max-w-md w-full bg-white border border-slate-200 rounded-3xl p-6 space-y-5 shadow-2xl">
            <div className="flex items-center justify-between border-b border-slate-100 pb-3">
              <h3 className="text-base font-bold text-slate-900 font-['Outfit']">
                Printable Honey Jar Label
              </h3>
              <button
                type="button"
                onClick={() => setShowPrintModal(false)}
                className="text-xs text-slate-500 hover:text-slate-900 px-2 py-1 rounded bg-slate-100"
              >
                ✕
              </button>
            </div>

            {/* Printable Area */}
            <div id="honeychain-jar-label" className="p-6 rounded-2xl bg-white text-slate-900 text-center space-y-3 border-2 border-slate-900 shadow-inner">
              <div className="flex items-center justify-center gap-1.5 font-bold text-sm tracking-wider font-['Outfit']">
                <span>🍯</span> HONEYCHAIN VERIFIED
              </div>
              <p className="text-[11px] font-mono font-bold text-slate-700">
                Batch: {batch.batchId}
              </p>
              <div className="flex justify-center my-2">
                <img
                  src={qrData.qrImageUrl}
                  alt="QR Code"
                  className="w-36 h-36 object-contain"
                />
              </div>
              <div className="inline-block px-3 py-0.5 rounded-full bg-blue-50 border border-blue-200 text-blue-800 text-[10px] font-bold">
                ✅ 100% Pure · Blockchain Certified
              </div>
              <p className="text-[9px] text-slate-500 font-medium">
                Scan with smartphone camera to verify authenticity & hive origin
              </p>
            </div>

            <div className="flex items-center gap-3 pt-2">
              <Button variant="primary" className="flex-1 font-bold" onClick={handlePrint}>
                🖨️ Print Label Now
              </Button>
              <Button variant="secondary" className="flex-1" onClick={() => setShowPrintModal(false)}>
                Close
              </Button>
            </div>
          </div>
        </div>
      )}
    </Card>
  )
}

export default QrCodeCard
