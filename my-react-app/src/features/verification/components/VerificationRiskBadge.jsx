import React from 'react'

const RISK_CONFIG = {
  NORMAL: {
    bg: 'bg-[#EFF6FF]',
    border: 'border-[#DBEAFE]',
    text: 'text-[#2563EB]',
    dot: 'bg-[#2563EB]',
    label: 'NORMAL ACTIVITY',
    icon: '🔵',
  },
  WATCH: {
    bg: 'bg-[#FEF3C7]',
    border: 'border-[#F59E0B]',
    text: 'text-[#D97706]',
    dot: 'bg-[#D97706]',
    label: 'WATCH SIGNAL',
    icon: '🍯',
  },
  HIGH_RISK: {
    bg: 'bg-[#EFF6FF]',
    border: 'border-[#2563EB]',
    text: 'text-[#1D4ED8]',
    dot: 'bg-[#1D4ED8]',
    label: 'HIGH RISK ACTIVITY',
    icon: '🛡️',
  },
}

export const VerificationRiskBadge = ({ riskLevel = 'NORMAL', size = 'sm' }) => {
  const cfg = RISK_CONFIG[riskLevel] || RISK_CONFIG.NORMAL
  const padding = size === 'lg' ? 'px-3.5 py-1.5 text-xs' : 'px-2.5 py-0.5 text-[11px]'

  return (
    <span
      className={`inline-flex items-center gap-1.5 rounded-full font-bold border font-mono tracking-wider ${cfg.bg} ${cfg.border} ${cfg.text} ${padding}`}
    >
      <span className={`w-1.5 h-1.5 rounded-full ${cfg.dot}`} />
      <span>{cfg.icon}</span>
      <span>{cfg.label}</span>
    </span>
  )
}

export default VerificationRiskBadge
