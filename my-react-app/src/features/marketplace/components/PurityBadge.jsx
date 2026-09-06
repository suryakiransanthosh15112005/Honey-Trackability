import React from 'react'
import { getPurityTier } from '../constants/marketplaceConstants'

/**
 * PurityBadge — shows the lab-tested purity score with color-coded tier.
 * Props:
 *   score: number | null (0-100)
 *   size: 'sm' | 'md' | 'lg'
 */
const PurityBadge = ({ score, size = 'md' }) => {
  if (score == null) {
    return (
      <span className={`purity-badge purity-badge--unknown purity-badge--${size}`}>
        Not Tested
      </span>
    )
  }

  const tier = getPurityTier(score)

  return (
    <span
      className={`purity-badge purity-badge--${size}`}
      style={{ '--purity-color': tier?.color || '#64748B' }}
      title={`Lab-tested purity: ${score}% (${tier?.label})`}
    >
      <span className="purity-badge__icon">🧪</span>
      <span className="purity-badge__score">{score.toFixed(1)}%</span>
      {size !== 'sm' && (
        <span className="purity-badge__label">{tier?.label}</span>
      )}
    </span>
  )
}

export default PurityBadge
