import React from 'react'

const RESULT_STYLES = {
  PURE: {
    bg: 'bg-blue-50',
    border: 'border-blue-200',
    text: 'text-blue-700',
    dot: 'bg-blue-600',
    icon: '✅',
    label: 'PURE',
  },
  UNDER_REVIEW: {
    bg: 'bg-amber-50',
    border: 'border-amber-200',
    text: 'text-amber-800',
    dot: 'bg-amber-600 animate-pulse',
    icon: '⚠️',
    label: 'UNDER REVIEW',
  },
  FAILED: {
    bg: 'bg-slate-100',
    border: 'border-slate-300',
    text: 'text-slate-800',
    dot: 'bg-slate-600',
    icon: '❌',
    label: 'FAILED',
  },
}

export const LabResultBadge = ({ result, size = 'sm' }) => {
  const cfg = RESULT_STYLES[result] || RESULT_STYLES.UNDER_REVIEW
  const padding = size === 'lg' ? 'px-4 py-1.5 text-sm' : 'px-2.5 py-1 text-xs'

  return (
    <span
      className={`inline-flex items-center gap-1.5 rounded-full font-bold border ${cfg.bg} ${cfg.border} ${cfg.text} ${padding}`}
    >
      <span className={`w-2 h-2 rounded-full ${cfg.dot}`} />
      <span>{cfg.icon}</span>
      <span>{cfg.label}</span>
    </span>
  )
}

export default LabResultBadge
