import React from 'react'

const STATUS_CONFIG = {
  ACTIVE: {
    dot: 'bg-blue-600 animate-pulse',
    text: 'text-blue-700',
    border: 'border-blue-200',
    bg: 'bg-blue-50',
    label: 'Active',
    icon: '🟢',
  },
  INACTIVE: {
    dot: 'bg-slate-400',
    text: 'text-slate-700',
    border: 'border-slate-300',
    bg: 'bg-slate-100',
    label: 'Inactive',
    icon: '⚪',
  },
  ALERT: {
    dot: 'bg-amber-600 animate-pulse',
    text: 'text-amber-800',
    border: 'border-amber-200',
    bg: 'bg-amber-50',
    label: 'Alert',
    icon: '🟡',
  },
}

export const HiveStatusBadge = ({ status, size = 'sm' }) => {
  const cfg = STATUS_CONFIG[status] || STATUS_CONFIG.INACTIVE
  const padding = size === 'lg' ? 'px-4 py-1.5 text-sm' : 'px-2.5 py-1 text-xs'

  return (
    <span
      className={`inline-flex items-center gap-1.5 rounded-full font-bold border ${cfg.bg} ${cfg.border} ${cfg.text} ${padding}`}
    >
      <span className={`w-2 h-2 rounded-full ${cfg.dot}`} />
      {cfg.icon} {cfg.label}
    </span>
  )
}

export default HiveStatusBadge
