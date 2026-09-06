import React from 'react'

const BATCH_STATUS_CONFIG = {
  CREATED: {
    dot: 'bg-amber-600 animate-pulse',
    text: 'text-amber-800',
    border: 'border-amber-200',
    bg: 'bg-amber-50',
    label: 'Created',
    icon: '🟡',
  },
  SENT_FOR_TESTING: {
    dot: 'bg-blue-600 animate-pulse',
    text: 'text-blue-700',
    border: 'border-blue-200',
    bg: 'bg-blue-50',
    label: 'Sent for Testing',
    icon: '🔵',
  },
  UNDER_REVIEW: {
    dot: 'bg-amber-600',
    text: 'text-amber-900',
    border: 'border-amber-200',
    bg: 'bg-amber-50',
    label: 'Under Review',
    icon: '🟡',
  },
  PURE: {
    dot: 'bg-blue-600',
    text: 'text-blue-700',
    border: 'border-blue-200',
    bg: 'bg-blue-50',
    label: 'Pure Certified',
    icon: '🔵',
  },
  FAILED: {
    dot: 'bg-slate-600',
    text: 'text-slate-800',
    border: 'border-slate-300',
    bg: 'bg-slate-100',
    label: 'Test Failed',
    icon: '❌',
  },
  QR_GENERATED: {
    dot: 'bg-amber-700',
    text: 'text-amber-900',
    border: 'border-amber-300',
    bg: 'bg-amber-100/70',
    label: 'QR Ready',
    icon: '🏷️',
  },
  IN_STOCK: {
    dot: 'bg-blue-700',
    text: 'text-blue-800',
    border: 'border-blue-200',
    bg: 'bg-blue-100/70',
    label: 'In Stock',
    icon: '📦',
  },
  SOLD: {
    dot: 'bg-slate-400',
    text: 'text-slate-700',
    border: 'border-slate-200',
    bg: 'bg-slate-100',
    label: 'Sold',
    icon: '✅',
  },
}

export const BatchStatusBadge = ({ status, size = 'sm' }) => {
  const cfg = BATCH_STATUS_CONFIG[status] || BATCH_STATUS_CONFIG.CREATED
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

export default BatchStatusBadge
