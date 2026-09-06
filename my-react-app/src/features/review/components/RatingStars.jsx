import React from 'react'

/**
 * RatingStars — Renders 5 stars either interactively (click to select) or read-only.
 *
 * Props:
 *   value     – current rating (1–5)
 *   onChange  – callback(rating) when user clicks a star; omit for read-only
 *   size      – 'sm' | 'md' | 'lg'  (default 'md')
 *   showValue – display numeric rating next to stars (default false)
 */
const RatingStars = ({ value = 0, onChange, size = 'md', showValue = false }) => {
  const isInteractive = typeof onChange === 'function'

  const sizeClass = size === 'sm' ? 'text-sm' : size === 'lg' ? 'text-2xl' : 'text-lg'

  return (
    <span className="rating-stars inline-flex items-center gap-0.5">
      {[1, 2, 3, 4, 5].map((star) => {
        const filled = star <= value
        return (
          <button
            key={star}
            type="button"
            className={`rating-star bg-transparent border-0 p-0.5 leading-none transition-all ${sizeClass} ${
              filled ? 'text-amber-500 rating-star--filled' : 'text-slate-400'
            } ${isInteractive ? 'cursor-pointer hover:scale-110 rating-star--interactive' : 'cursor-default'}`}
            onClick={isInteractive ? () => onChange(star) : undefined}
            aria-label={`${star} star${star !== 1 ? 's' : ''}`}
            tabIndex={isInteractive ? 0 : -1}
          >
            {filled ? '★' : '☆'}
          </button>
        )
      })}
      {showValue && value > 0 && (
        <span className={`rating-stars__value font-bold text-amber-500 ml-1 ${sizeClass}`}>
          {Number(value).toFixed(1)}
        </span>
      )}
    </span>
  )
}

export default RatingStars
