import React from 'react'

export const QuantityStepper = ({
  value = 5.0,
  onChange,
  min = 0.5,
  max = 1000.0,
  step = 0.5,
  error = null,
}) => {
  const currentVal = typeof value === 'number' ? value : parseFloat(value) || 0.0

  const handleDecrement = () => {
    const next = Math.max(min, +(currentVal - step).toFixed(2))
    onChange(next)
  }

  const handleIncrement = () => {
    const next = Math.min(max, +(currentVal + step).toFixed(2))
    onChange(next)
  }

  const handleDirectInput = (e) => {
    const val = parseFloat(e.target.value)
    if (!isNaN(val)) {
      onChange(val)
    } else if (e.target.value === '') {
      onChange('')
    }
  }

  return (
    <div className="space-y-2">
      <label className="block text-sm font-medium text-slate-700">
        Harvest Quantity (KG) *
      </label>

      {/* Stepper Control */}
      <div className="flex items-center gap-3">
        <button
          type="button"
          onClick={handleDecrement}
          disabled={currentVal <= min}
          className="w-14 h-14 rounded-2xl bg-amber-50 border border-amber-200 text-amber-800 text-2xl font-black flex items-center justify-center hover:bg-amber-100 active:scale-95 disabled:opacity-30 disabled:pointer-events-none transition-all"
          aria-label="Decrease quantity"
        >
          −
        </button>

        <div className="flex-1 text-center py-3 px-4 rounded-2xl bg-slate-50 border border-slate-200 flex items-center justify-center gap-2">
          <input
            type="number"
            step="0.1"
            min={min}
            max={max}
            value={value}
            onChange={handleDirectInput}
            className="w-28 text-center text-3xl font-black text-slate-900 font-mono bg-transparent focus:outline-none focus:border-b-2 focus:border-blue-600"
          />
          <span className="text-amber-800 font-bold text-lg">KG</span>
        </div>

        <button
          type="button"
          onClick={handleIncrement}
          disabled={currentVal >= max}
          className="w-14 h-14 rounded-2xl bg-amber-50 border border-amber-200 text-amber-800 text-2xl font-black flex items-center justify-center hover:bg-amber-100 active:scale-95 disabled:opacity-30 disabled:pointer-events-none transition-all"
          aria-label="Increase quantity"
        >
          +
        </button>
      </div>

      {/* Quick Preset Buttons */}
      <div className="flex items-center gap-2 pt-1">
        <span className="text-xs text-slate-500">Quick:</span>
        {[1, 5, 10, 25, 50].map((preset) => (
          <button
            key={preset}
            type="button"
            onClick={() => onChange(preset)}
            className={`px-2.5 py-1 text-xs rounded-lg border transition-all ${
              currentVal === preset
                ? 'bg-amber-500 text-white font-bold border-amber-500'
                : 'border-slate-200 text-slate-700 hover:border-amber-400 bg-white'
            }`}
          >
            {preset}kg
          </button>
        ))}
      </div>

      {error && <p className="text-blue-600 text-xs mt-1">{error}</p>}
    </div>
  )
}

export default QuantityStepper
