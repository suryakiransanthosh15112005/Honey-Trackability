import React, { useState } from 'react'

export const PredictionExplanation = ({ explanation, details }) => {
  const [open, setOpen] = useState(false)

  if (!explanation && !details) return null

  return (
    <div className="mt-4 pt-3 border-t border-slate-200 dark:border-slate-800">
      <button
        type="button"
        onClick={() => setOpen(!open)}
        className="btn btn--ghost btn--xs p-0 text-blue-600 dark:text-blue-400 text-xs font-semibold"
      >
        <span>{open ? '▼' : '►'}</span> Why this prediction?
      </button>

      {open && (
        <div className="mt-3 p-4 rounded-xl animate-fade-in bg-slate-50 dark:bg-slate-900 border border-slate-200 dark:border-slate-800 text-xs leading-relaxed">
          <p className="text-slate-800 dark:text-slate-200 mb-3">{explanation}</p>

          {details && (
            <div className="flex flex-col gap-2 pt-2 border-t border-slate-200 dark:border-slate-800">
              <div className="flex justify-between items-center text-xs">
                <span className="text-muted">Hive Health Status:</span>
                <span className={`badge badge--${details.healthStatus?.toLowerCase() || 'healthy'}`}>
                  {details.healthStatus}
                </span>
              </div>

              {details.historicalDataPoints > 0 ? (
                <div className="flex justify-between items-center text-xs">
                  <span className="text-muted">Harvest History:</span>
                  <span className="font-mono text-primary">
                    {details.historicalDataPoints} harvests (avg {details.averageHistoricalYield} kg)
                  </span>
                </div>
              ) : (
                <div className="flex justify-between items-center text-xs">
                  <span className="text-muted">Harvest History:</span>
                  <span className="text-muted italic">No prior harvests (using baseline)</span>
                </div>
              )}

              {details.beeActivity !== null && details.beeActivity !== undefined && (
                <div className="flex justify-between items-center text-xs">
                  <span className="text-muted">Bee Activity:</span>
                  <span className="font-mono text-primary">{details.beeActivity}%</span>
                </div>
              )}

              {details.temperature && (
                <div className="flex justify-between items-center text-xs">
                  <span className="text-muted">Temperature / Humidity:</span>
                  <span className="font-mono text-primary">
                    {details.temperature}°C / {details.humidity}%
                  </span>
                </div>
              )}
            </div>
          )}
        </div>
      )}
    </div>
  )
}

export default PredictionExplanation
