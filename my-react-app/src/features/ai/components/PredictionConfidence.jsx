import React from 'react'

export const PredictionConfidence = ({ confidence = 50 }) => {
  const isLow = confidence < 60

  return (
    <div className="flex-col gap-2">
      <div className="flex items-center justify-between">
        <span className="text-xs text-secondary font-medium">Prediction Confidence</span>
        <span className={`font-mono text-xs font-bold ${isLow ? 'text-alert' : 'text-gold'}`}>
          {confidence}%
        </span>
      </div>

      {/* Progress Bar */}
      <div className="w-full h-1.5 bg-slate-200 dark:bg-slate-700 rounded-full overflow-hidden">
        <div
          className={`h-full transition-all duration-400 ${isLow ? 'bg-blue-700' : 'bg-amber-500'}`}
          style={{
            width: `${Math.min(100, Math.max(0, confidence))}%`,
          }}
        />
      </div>

      {isLow && (
        <div className="alert alert--warning mt-1 py-1.5 px-2.5 text-xs mb-0">
          ⚠️ Low Confidence: More harvest history is needed for a more reliable estimate.
        </div>
      )}
    </div>
  )
}

export default PredictionConfidence
