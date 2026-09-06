import React from 'react'
import Card from '../../../components/ui/Card'

export const VerificationTimeline = ({ timeline }) => {
  if (!timeline || timeline.length === 0) return null

  return (
    <Card className="p-6 space-y-5 border border-[#E2E8F0] bg-white">
      <h2 className="text-lg font-bold text-[#1E293B] font-['Inter'] flex items-center gap-2">
        <span>⏱️</span> Traceability Milestone History
      </h2>

      <div className="relative pl-6 space-y-6 before:absolute before:left-2.5 before:top-2 before:bottom-2 before:w-0.5 before:bg-[#2563EB]">
        {timeline.map((item, idx) => (
          <div key={idx} className="relative flex items-start gap-4 group">
            {/* Timeline node icon */}
            <div className="absolute -left-6 top-0 w-6 h-6 rounded-full bg-white border-2 border-[#2563EB] flex items-center justify-center text-xs shadow-sm">
              <span>{item.icon || '●'}</span>
            </div>

            <div className="flex-1 bg-[#F8FAFC] p-3.5 rounded-xl border border-[#E2E8F0] group-hover:border-[#2563EB] transition-colors">
              <div className="flex items-center justify-between gap-2 mb-1">
                <p className="text-[#1E293B] font-bold text-xs font-['Inter']">{item.title}</p>
                <span className="text-[10px] text-[#64748B] font-mono">
                  {item.date
                    ? new Date(item.date).toLocaleDateString('en-IN', {
                      day: '2-digit',
                      month: 'short',
                      year: 'numeric',
                    })
                    : '—'}
                </span>
              </div>
              <p className="text-xs text-[#64748B] leading-relaxed">{item.description}</p>
            </div>
          </div>
        ))}
      </div>
    </Card>
  )
}

export default VerificationTimeline
