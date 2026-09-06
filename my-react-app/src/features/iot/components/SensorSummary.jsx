import React from 'react'

export const SensorSummary = ({ temperature, humidity, beeActivity, checkedAt }) => {
  return (
    <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
      {/* Temperature */}
      <div className="p-4 rounded-xl bg-white border border-slate-200 shadow-sm space-y-1">
        <div className="flex items-center justify-between text-xs text-slate-500">
          <span>🌡️ Internal Temp</span>
          <span className="font-mono text-[11px]">Normal: 32–37°C</span>
        </div>
        <p className="text-2xl font-bold font-mono text-slate-900">
          {temperature != null ? `${Number(temperature).toFixed(1)}°C` : '—'}
        </p>
      </div>

      {/* Humidity */}
      <div className="p-4 rounded-xl bg-white border border-slate-200 shadow-sm space-y-1">
        <div className="flex items-center justify-between text-xs text-slate-500">
          <span>💧 Hive Humidity</span>
          <span className="font-mono text-[11px]">Normal: 45–70%</span>
        </div>
        <p className="text-2xl font-bold font-mono text-slate-900">
          {humidity != null ? `${Number(humidity).toFixed(1)}%` : '—'}
        </p>
      </div>

      {/* Bee Activity */}
      <div className="p-4 rounded-xl bg-white border border-slate-200 shadow-sm space-y-1">
        <div className="flex items-center justify-between text-xs text-slate-500">
          <span>🐝 Bee Activity</span>
          <span className="font-mono text-[11px]">Healthy: ≥70/100</span>
        </div>
        <p className="text-2xl font-bold font-mono text-slate-900">
          {beeActivity != null ? `${beeActivity}/100` : '—'}
        </p>
      </div>
    </div>
  )
}

export default SensorSummary
