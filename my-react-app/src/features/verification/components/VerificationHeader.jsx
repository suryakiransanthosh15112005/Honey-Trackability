import React, { useState } from 'react'

export const VerificationHeader = ({ verification, batchId }) => {
  const [copied, setCopied] = useState(false)
  const isGenuine = verification?.verificationStatus === 'GENUINE' && verification?.verified
  const isUnderReview = verification?.verificationStatus === 'UNDER_REVIEW'
  const isFailed = verification?.verificationStatus === 'FAILED'
  const isNotFound = verification?.verificationStatus === 'NOT_FOUND'

  const handleCopy = () => {
    navigator.clipboard.writeText(batchId)
    setCopied(true)
    setTimeout(() => setCopied(false), 2000)
  }

  const getStatusBadge = () => {
    if (isGenuine) {
      return {
        bg: 'bg-[#EFF6FF] border-[#2563EB] text-[#2563EB]',
        icon: '🔗',
        title: 'GENUINE / VERIFIED HONEY',
        subtitle: 'Cryptographically verified & traceable from hive to consumer',
      }
    }
    if (isUnderReview) {
      return {
        bg: 'bg-[#FEF3C7] border-[#F59E0B] text-[#D97706]',
        icon: '🍯',
        title: 'UNDER LABORATORY REVIEW',
        subtitle: 'Sample is currently undergoing secondary laboratory testing',
      }
    }
    if (isFailed) {
      return {
        bg: 'bg-[#F8FAFC] border-[#1E293B] text-[#1E293B]',
        icon: '🛡️',
        title: 'VERIFICATION FAILED',
        subtitle: 'This batch did not pass authenticity or blockchain integrity checks',
      }
    }
    return {
      bg: 'bg-[#FEF3C7] border-[#F59E0B] text-[#D97706]',
      icon: '🔍',
      title: 'BATCH NOT FOUND',
      subtitle: 'This batch identifier is not registered on the HoneyChain network',
    }
  }

  const badge = getStatusBadge()

  return (
    <div className="text-center space-y-4">
      {/* Brand Header */}
      <div className="inline-flex items-center gap-2 px-3.5 py-1.5 rounded-full bg-[#FEF3C7] border border-[#F59E0B] text-xs font-bold text-[#D97706]">
        <span>🍯</span> HONEYCHAIN PUBLIC PASSPORT
      </div>

      {/* Main Verification Status Card */}
      <div className={`p-6 sm:p-8 rounded-3xl border ${badge.bg} transition-all`}>
        <div className="text-5xl sm:text-6xl mb-3">{badge.icon}</div>
        <h1 className="text-2xl sm:text-3xl font-black font-['Inter'] tracking-tight">
          {badge.title}
        </h1>
        <p className="text-xs sm:text-sm text-[#64748B] mt-1.5 max-w-md mx-auto leading-relaxed">
          {badge.subtitle}
        </p>

        {/* Batch ID Banner */}
        <div className="mt-5 inline-flex items-center gap-3 px-4 py-2 rounded-2xl bg-white border border-[#E2E8F0] shadow-sm">
          <span className="text-xs text-[#64748B] font-medium">Batch ID:</span>
          <span className="font-mono font-bold text-[#2563EB] text-sm tracking-wider">
            {batchId}
          </span>
          <button
            type="button"
            onClick={handleCopy}
            title="Copy Batch ID"
            className="text-xs text-[#64748B] hover:text-[#2563EB] transition-colors"
          >
            {copied ? '✓' : '📋'}
          </button>
        </div>
      </div>
    </div>
  )
}

export default VerificationHeader
