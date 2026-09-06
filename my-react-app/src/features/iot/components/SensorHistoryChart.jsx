import React, { useState } from 'react'

export const SensorHistoryChart = ({ readings = [] }) => {
  const [metric, setMetric] = useState('temperature') // 'temperature', 'humidity', 'beeActivity'

  if (!readings || readings.length === 0) {
    return (
      <div className="p-8 text-center text-xs text-slate-500 border border-dashed border-slate-300 rounded-xl bg-white shadow-sm">
        No historical sensor readings recorded yet.
      </div>
    )
  }

  // Reverse readings to show chronological left-to-right (oldest to newest)
  const sorted = [...readings].reverse()

  const config = {
    temperature: {
      label: 'Temperature (°C)',
      color: '#D97706',
      unit: '°C',
      min: 25,
      max: 45,
      getVal: (r) => Number(r.temperature),
      normalBand: { min: 32, max: 37 },
    },
    humidity: {
      label: 'Humidity (%)',
      color: '#2563EB',
      unit: '%',
      min: 20,
      max: 100,
      getVal: (r) => Number(r.humidity),
      normalBand: { min: 45, max: 70 },
    },
    beeActivity: {
      label: 'Bee Activity (0-100)',
      color: '#1D4ED8',
      unit: '/100',
      min: 0,
      max: 100,
      getVal: (r) => Number(r.beeActivity),
      normalBand: { min: 70, max: 100 },
    },
  }[metric]

  const values = sorted.map(config.getVal)
  const minVal = Math.min(config.min, ...values)
  const maxVal = Math.max(config.max, ...values)
  const range = maxVal - minVal || 1

  const width = 600
  const height = 180
  const padding = 30

  const points = sorted.map((r, i) => {
    const val = config.getVal(r)
    const x = padding + (i / Math.max(1, sorted.length - 1)) * (width - 2 * padding)
    const y = height - padding - ((val - minVal) / range) * (height - 2 * padding)
    return { x, y, val, r }
  })

  const pathD = points.length > 1
    ? points.reduce((acc, p, i) => `${acc} ${i === 0 ? 'M' : 'L'} ${p.x},${p.y}`, '')
    : ''

  return (
    <div className="space-y-4">
      {/* Metric Selector Tabs */}
      <div className="flex items-center justify-between flex-wrap gap-2">
        <span className="text-xs text-slate-500 font-medium">Historical Telemetry</span>
        <div className="flex gap-1.5 p-1 bg-slate-100 rounded-xl border border-slate-200 text-xs">
          {[
            { id: 'temperature', label: '🌡️ Temperature' },
            { id: 'humidity', label: '💧 Humidity' },
            { id: 'beeActivity', label: '🐝 Bee Activity' },
          ].map((tab) => (
            <button
              key={tab.id}
              onClick={() => setMetric(tab.id)}
              className={`px-3 py-1 rounded-lg font-medium transition-all ${
                metric === tab.id
                  ? 'bg-blue-600 text-white shadow-sm font-semibold'
                  : 'text-slate-600 hover:text-slate-900'
              }`}
            >
              {tab.label}
            </button>
          ))}
        </div>
      </div>

      {/* SVG Line Chart */}
      <div className="p-4 rounded-xl bg-white border border-slate-200 overflow-x-auto shadow-sm">
        <svg viewBox={`0 0 ${width} ${height}`} className="w-full h-44">
          {/* Background Grid Lines */}
          <line x1={padding} y1={padding} x2={width - padding} y2={padding} stroke="rgba(0,0,0,0.06)" />
          <line x1={padding} y1={height / 2} x2={width - padding} y2={height / 2} stroke="rgba(0,0,0,0.06)" />
          <line x1={padding} y1={height - padding} x2={width - padding} y2={height - padding} stroke="rgba(0,0,0,0.1)" />

          {/* Value Labels on Y-axis */}
          <text x={padding - 5} y={padding + 4} textAnchor="end" fill="#64748B" fontSize="10" fontFamily="monospace">
            {Math.round(maxVal)}
          </text>
          <text x={padding - 5} y={height - padding + 4} textAnchor="end" fill="#64748B" fontSize="10" fontFamily="monospace">
            {Math.round(minVal)}
          </text>

          {/* Connecting Path */}
          {points.length > 1 && (
            <path
              d={pathD}
              fill="none"
              stroke={config.color}
              strokeWidth="2.5"
              strokeLinecap="round"
              strokeLinejoin="round"
            />
          )}

          {/* Data Points */}
          {points.map((p, idx) => (
            <g key={idx} className="group">
              <circle
                cx={p.x}
                cy={p.y}
                r="4.5"
                fill="#FFFFFF"
                stroke={config.color}
                strokeWidth="2"
                className="transition-all hover:r-6 cursor-pointer"
              />
              {/* Tooltip on last or hover */}
              <text
                x={p.x}
                y={p.y - 10}
                textAnchor="middle"
                fill="#1E293B"
                fontSize="10"
                fontFamily="monospace"
                className="opacity-0 group-hover:opacity-100 transition-opacity font-bold"
              >
                {p.val}
                {config.unit}
              </text>
            </g>
          ))}
        </svg>

        <div className="flex items-center justify-between text-[11px] text-slate-500 pt-2 border-t border-slate-100 font-mono">
          <span>Oldest Reading</span>
          <span>Latest Telemetry Stream</span>
        </div>
      </div>
    </div>
  )
}

export default SensorHistoryChart
