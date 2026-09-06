import React from 'react'

export const PurityScoreInput = ({ value = 98, onChange, error }) => {
  const numVal = typeof value === 'number' ? value : parseInt(value, 10) || 0

  const getScoreColor = (score) => {
    if (score >= 90) return 'text-blue-700 border-blue-200 bg-blue-50'
    if (score >= 75) return 'text-amber-800 border-amber-200 bg-amber-50'
    return 'text-slate-800 border-slate-300 bg-slate-100'
  }

  return (
    <div className="space-y-3">
      <div className="flex items-center justify-between">
        <label className="block text-sm font-medium text-slate-800 font-semibold">
          Laboratory Purity Score (0 - 100%) *
        </label>
        <div
          className={`px-3 py-1 rounded-xl border font-mono font-black text-lg ${getScoreColor(
            numVal
          )}`}
        >
          {numVal}%
        </div>
      </div>

      {/* Range Slider */}
      <input
        type="range"
        min="0"
        max="100"
        value={numVal}
        onChange={(e) => onChange(parseInt(e.target.value, 10))}
        className="w-full h-2.5 rounded-lg bg-slate-200 accent-blue-600 cursor-pointer"
      />

      {/* Preset Quick Selectors */}
      <div className="flex items-center justify-between gap-2 pt-1">
        <span className="text-xs text-slate-500 font-medium">Quick Presets:</span>
        {[99, 95, 90, 80, 50].map((preset) => (
          <button
            key={preset}
            type="button"
            onClick={() => onChange(preset)}
            className={`px-2.5 py-1 text-xs rounded-lg border font-mono transition-all ${
              numVal === preset
                ? 'bg-blue-600 text-white font-bold border-blue-600 shadow-sm'
                : 'border-slate-200 text-slate-700 bg-white hover:border-blue-500'
            }`}
          >
            {preset}%
          </button>
        ))}
      </div>

      {error && <p className="text-blue-600 text-xs mt-1 font-semibold">{error}</p>}
    </div>
  )
}

export default PurityScoreInput
