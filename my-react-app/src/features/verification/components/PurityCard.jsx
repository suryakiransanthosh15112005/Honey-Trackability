import React from 'react'
import Card from '../../../components/ui/Card'
import LabResultBadge from '../../lab/components/LabResultBadge'
import Button from '../../../components/ui/Button'

export const PurityCard = ({ purity, onOpenCertificate }) => {
  if (!purity) {
    return (
      <Card className="p-6 border border-[#E2E8F0] text-center space-y-2 bg-white">
        <span className="text-3xl">🧪</span>
        <h3 className="text-base font-bold text-[#1E293B] font-['Inter']">Laboratory Testing Pending</h3>
        <p className="text-xs text-[#64748B]">
          This honey batch is awaiting accredited laboratory chemical analysis.
        </p>
      </Card>
    )
  }

  const isPure = purity.result === 'PURE'

  return (
    <Card className={`p-6 space-y-5 border ${isPure ? 'border-[#DBEAFE] bg-[#EFF6FF]' : 'border-[#F59E0B] bg-[#FEF3C7]'}`}>
      <div className="flex items-center justify-between">
        <h2 className="text-lg font-bold text-[#1E293B] font-['Inter'] flex items-center gap-2">
          <span>🧪</span> Laboratory Purity Analysis
        </h2>
        <LabResultBadge result={purity.result} size="lg" />
      </div>

      {/* Purity Score Gauge / Readout */}
      <div className="grid grid-cols-2 gap-3">
        <div className="p-4 rounded-2xl bg-white border border-[#DBEAFE] text-center">
          <p className="text-[11px] text-[#64748B] uppercase font-bold tracking-wider">Purity Score</p>
          <p className="text-4xl font-black text-[#2563EB] font-mono mt-1">
            {purity.score}%
          </p>
          <p className="text-[10px] text-[#2563EB] mt-1">FSSAI Standard Compliant</p>
        </div>

        <div className="p-4 rounded-2xl bg-white border border-[#DBEAFE] text-center flex flex-col justify-center">
          <p className="text-[11px] text-[#64748B] uppercase font-bold tracking-wider">Analysis Result</p>
          <p className="text-xl font-bold text-[#1E293B] mt-1">{purity.result}</p>
          <p className="text-[10px] text-[#64748B] mt-1">
            Tested {purity.testedAt ? new Date(purity.testedAt).toLocaleDateString('en-IN') : '—'}
          </p>
        </div>
      </div>

      {/* Lab Remarks */}
      {purity.remarks && (
        <div className="p-3.5 rounded-xl bg-white border border-[#E2E8F0] text-xs">
          <span className="text-[#64748B] font-medium">Laboratory Findings: </span>
          <span className="text-[#1E293B] leading-relaxed">{purity.remarks}</span>
        </div>
      )}

      {/* Certificate Action */}
      {purity.certificateUrl && (
        <div className="flex items-center justify-between p-3.5 rounded-xl bg-white border border-[#DBEAFE]">
          <div className="flex items-center gap-2 text-xs text-[#1E293B]">
            <span className="text-xl">📄</span>
            <div>
              <p className="font-bold">Accredited Laboratory Certificate</p>
              <p className="text-[10px] text-[#64748B]">Official purity test documentation</p>
            </div>
          </div>
          <Button
            size="sm"
            variant="secondary"
            onClick={() => onOpenCertificate(purity.certificateUrl)}
            className="text-xs font-bold"
          >
            View Document ↗
          </Button>
        </div>
      )}
    </Card>
  )
}

export default PurityCard
