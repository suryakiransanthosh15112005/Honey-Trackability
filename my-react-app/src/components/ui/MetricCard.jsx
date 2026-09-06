/**
 * MetricCard — Standardized Premium Dashboard KPI Metric Card.
 * Flawless text placement, balanced proportions, aligned hierarchy conforming to card shape.
 */
export const MetricCard = ({
  icon,
  label,
  value,
  subtext,
  trend,
  badge,
  className = '',
}) => {
  return (
    <div
      className={`bg-white rounded-2xl border border-slate-200/80 p-5 sm:p-6 shadow-xs hover:shadow-md hover:-translate-y-0.5 transition-all duration-200 flex flex-col justify-between h-full min-h-[10.5rem] text-left group relative overflow-hidden ${className}`}
    >
      {/* Top subtle warm gold highlight bar */}
      <div className="absolute inset-x-0 top-0 h-1 bg-gradient-to-r from-amber-400 via-amber-500 to-transparent opacity-80 group-hover:opacity-100 transition-opacity" />

      {/* Card Header: Icon Chip + Category Label on Left, Status Badge on Right */}
      <div className="flex items-center justify-between gap-3 mb-2">
        <div className="flex items-center gap-2.5 min-w-0">
          {icon && (
            <span className="w-8 h-8 rounded-xl bg-amber-50 text-amber-800 border border-amber-200/70 flex items-center justify-center text-sm shrink-0 shadow-2xs">
              {icon}
            </span>
          )}
          <span className="text-[11px] sm:text-xs font-bold uppercase tracking-wider text-slate-500 truncate">
            {label}
          </span>
        </div>
        {badge && (
          <span className="text-[10px] sm:text-[11px] font-bold px-2.5 py-0.5 rounded-full bg-slate-100 text-slate-700 border border-slate-200/80 shrink-0">
            {badge}
          </span>
        )}
      </div>

      {/* Card Body: Dominant Value Typography */}
      <div className="my-2">
        <div className="text-3xl sm:text-4xl font-extrabold text-slate-900 font-['Outfit'] tracking-tight leading-none">
          {value}
        </div>
      </div>

      {/* Card Footer: Context Subtext & Trend Indicator */}
      {(subtext || trend) && (
        <div className="mt-3 pt-3 border-t border-slate-100 flex items-center justify-between text-xs text-slate-500 gap-2">
          {subtext && (
            <div className="flex items-center gap-2 min-w-0">
              <span className="w-1.5 h-1.5 rounded-full bg-amber-500 shrink-0" />
              <span className="truncate font-medium text-slate-500">{subtext}</span>
            </div>
          )}
          {trend && (
            <span className="text-amber-700 font-bold shrink-0">{trend}</span>
          )}
        </div>
      )}
    </div>
  )
}

export default MetricCard
