import React from 'react'

const STATUS_CONFIG = {
  HEALTHY: {
    bg: 'bg-blue-50',
    border: 'border-blue-200',
    text: 'text-blue-700',
    dot: 'bg-blue-600',
    label: 'Healthy',
    icon: '🟢',
  },
  WATCH: {
    bg: 'bg-amber-50',
    border: 'border-amber-200',
    text: 'text-amber-800',
    dot: 'bg-amber-600 animate-pulse',
    label: 'Watch',
    icon: '🟡',
  },
  ALERT: {
    bg: 'bg-blue-100',
    border: 'border-blue-300',
    text: 'text-blue-900',
    dot: 'bg-blue-700 animate-ping',
    label: 'Alert',
    icon: '🔵',
  },
}

export const HealthStatusBadge = ({ status = 'HEALTHY', size = 'sm' }) => {
  const cfg = STATUS_CONFIG[status] || STATUS_CONFIG.HEALTHY
  const padding = size === 'lg' ? 'px-4 py-1.5 text-sm gap-2' : 'px-2.5 py-0.5 text-xs gap-1.5'

  return (
    <span
      className={`inline-flex items-center rounded-full font-bold border font-['Outfit'] ${cfg.bg} ${cfg.border} ${cfg.text} ${padding}`}
    >
      <span className={`w-2 h-2 rounded-full inline-block ${cfg.dot}`} />
      <span>{cfg.icon}</span>
      <span>{cfg.label}</span>
    </span>
  )
}

export default HealthStatusBadge
