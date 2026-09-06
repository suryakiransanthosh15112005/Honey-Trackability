import React from 'react'

export const RecentVerificationList = ({ events }) => {
  if (!events || events.length === 0) {
    return (
      <p className="text-xs text-slate-500 italic">No prior verification scans recorded.</p>
    )
  }

  return (
    <div className="space-y-2 max-h-48 overflow-y-auto pr-1">
      {events.map((ev, idx) => (
        <div
          key={idx}
          className="flex items-center justify-between p-2.5 rounded-xl bg-slate-50 border border-slate-200 text-xs shadow-sm"
        >
          <div className="flex items-center gap-2">
            <span className="text-xs">
              {ev.result === 'VERIFIED' ? '✅' : ev.result === 'UNDER_REVIEW' ? '⚠️' : '❌'}
            </span>
            <span className="text-slate-800 font-medium font-['Outfit']">{ev.result} Scan</span>
          </div>
          <span className="text-[11px] text-slate-500 font-mono">
            {ev.scannedAt
              ? new Date(ev.scannedAt).toLocaleDateString('en-IN', {
                day: '2-digit',
                month: 'short',
                hour: '2-digit',
                minute: '2-digit',
              })
              : '—'}
          </span>
        </div>
      ))}
    </div>
  )
}

export default RecentVerificationList
