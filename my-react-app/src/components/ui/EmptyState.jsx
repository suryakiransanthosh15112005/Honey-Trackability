import React from 'react'

/**
 * EmptyState — Standardized Empty State Component.
 * Used across tables, dashboards, grids, and list views.
 */
export const EmptyState = ({
  icon = '🐝',
  title = 'No items found',
  description = 'There are no records to display at this time.',
  action = null,
  className = '',
}) => {
  return (
    <div className={`card p-8 sm:p-12 text-center flex flex-col items-center justify-center space-y-4 max-w-lg mx-auto ${className}`}>
      <div className="w-16 h-16 rounded-2xl bg-amber-50 border border-amber-200 flex items-center justify-center text-3xl shadow-sm">
        {icon}
      </div>
      <div className="space-y-1.5">
        <h3 className="text-lg font-extrabold text-slate-900 tracking-tight font-['Outfit']">
          {title}
        </h3>
        {description && (
          <p className="text-sm text-slate-500 max-w-sm mx-auto leading-relaxed">
            {description}
          </p>
        )}
      </div>
      {action && <div className="pt-2">{action}</div>}
    </div>
  )
}

export default EmptyState
