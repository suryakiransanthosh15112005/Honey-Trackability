import React, { useState } from 'react'
import Card from '../../../components/ui/Card'

export const BlockchainCard = ({ blockchain }) => {
  const [showDetails, setShowDetails] = useState(false)
  const [copiedTx, setCopiedTx] = useState(false)

  if (!blockchain) return null

  const handleCopyTx = () => {
    if (blockchain.transactionHash) {
      navigator.clipboard.writeText(blockchain.transactionHash)
      setCopiedTx(true)
      setTimeout(() => setCopiedTx(false), 2000)
    }
  }

  const shortTx = blockchain.transactionHash
    ? `${blockchain.transactionHash.substring(0, 10)}...${blockchain.transactionHash.substring(
      blockchain.transactionHash.length - 8
    )}`
    : '—'

  return (
    <Card className="p-6 space-y-4 border border-amber-200 bg-amber-50/40">
      <div className="flex items-center justify-between">
        <h2 className="text-lg font-bold text-slate-900 font-['Outfit'] flex items-center gap-2">
          <span>🔗</span> Blockchain Immutable Proof
        </h2>
        <span className="px-3 py-1 rounded-full text-xs font-bold bg-blue-50 border border-blue-200 text-blue-700 flex items-center gap-1.5">
          <span className="w-1.5 h-1.5 rounded-full bg-blue-600 animate-pulse" />
          Tamper-Proof
        </span>
      </div>

      <div className="grid grid-cols-2 gap-3 text-xs">
        <div className="p-3.5 rounded-xl bg-white border border-slate-200 shadow-sm">
          <p className="text-slate-500 font-medium">Blockchain Network</p>
          <p className="text-slate-900 font-mono font-bold mt-0.5">{blockchain.network}</p>
        </div>
        <div className="p-3.5 rounded-xl bg-white border border-slate-200 shadow-sm">
          <p className="text-slate-500 font-medium">Block Height</p>
          <p className="text-amber-700 font-mono font-bold mt-0.5">#{blockchain.blockNumber}</p>
        </div>
      </div>

      {/* Transaction Hash */}
      <div className="p-3.5 rounded-xl bg-white border border-slate-200 shadow-sm flex items-center justify-between gap-2">
        <div className="min-w-0 flex-1">
          <p className="text-[10px] text-slate-500 uppercase font-bold tracking-wider">Transaction Hash</p>
          <p className="text-slate-900 font-mono text-xs font-bold truncate mt-0.5">{shortTx}</p>
        </div>
        <button
          type="button"
          onClick={handleCopyTx}
          className="text-xs px-2.5 py-1 rounded-lg bg-amber-100 hover:bg-amber-200 text-amber-800 border border-amber-300 font-mono transition-colors font-semibold"
        >
          {copiedTx ? 'Copied' : 'Copy'}
        </button>
      </div>

      {/* Expandable Technical Proof Details */}
      <div className="pt-1">
        <button
          type="button"
          onClick={() => setShowDetails(!showDetails)}
          className="text-xs text-slate-600 hover:text-blue-600 flex items-center gap-1 transition-colors font-medium"
        >
          <span>{showDetails ? '▲ Hide' : '▼ View'} Cryptographic SHA-256 Digest</span>
        </button>

        {showDetails && (
          <div className="mt-3 p-3.5 rounded-xl bg-white border border-slate-200 space-y-2 text-[11px] font-mono text-slate-600 break-all animate-fade-in shadow-sm">
            <div>
              <span className="text-slate-900 font-bold block mb-0.5">Data Hash (SHA-256):</span>
              <span className="text-amber-800 font-bold">{blockchain.dataHash}</span>
            </div>
            <div>
              <span className="text-slate-900 font-bold block mb-0.5">Timestamp:</span>
              <span className="text-slate-800">{blockchain.recordedAt ? new Date(blockchain.recordedAt).toISOString() : '—'}</span>
            </div>
          </div>
        )}
      </div>
    </Card>
  )
}

export default BlockchainCard
