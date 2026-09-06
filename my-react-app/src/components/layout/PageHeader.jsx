import React from 'react'

/**
 * PageHeader — Standardized Page Header component.
 * Desktop (>=768px): Title + Subtitle on LEFT with full natural width, Actions on RIGHT.
 * Mobile (<768px): Stacked layout cleanly aligned to the left.
 */
export const PageHeader = ({ title, subtitle, actions, className = '' }) => {
  return (
    <div className={`page-header flex flex-col md:flex-row md:items-center justify-between gap-5 pb-6 mb-8 border-b border-slate-200/70 text-left w-full ${className}`}>
      <div className="page-header__content min-w-0 flex-1 space-y-1.5">
        <h1 className="page-header__title text-2xl sm:text-[1.875rem] font-extrabold text-slate-900 tracking-tight font-['Outfit'] leading-tight">
          {title}
        </h1>
        {subtitle && (
          <p className="page-header__subtitle text-[0.9375rem] text-slate-500 font-normal leading-relaxed max-w-xl">
            {subtitle}
          </p>
        )}
      </div>
      {actions && (
        <div className="page-header__actions flex flex-wrap items-center gap-3 w-full md:w-auto shrink-0 justify-start md:justify-end">
          {actions}
        </div>
      )}
    </div>
  )
}

export default PageHeader
