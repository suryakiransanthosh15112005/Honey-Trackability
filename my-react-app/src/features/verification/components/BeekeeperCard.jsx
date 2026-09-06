import React from 'react'
import Card from '../../../components/ui/Card'

export const BeekeeperCard = ({ beekeeper, harvestDate, hiveCode, clusterName, quantityKg, batchPhotoUrl }) => {
  return (
    <Card className="p-6 space-y-4 border border-[#E2E8F0] bg-white">
      <div className="flex items-center justify-between">
        <h2 className="text-lg font-bold text-[#1E293B] font-['Inter'] flex items-center gap-2">
          <span>👨‍🌾</span> Beekeeper & Origin Details
        </h2>
        <span className="text-[11px] font-bold px-2.5 py-1 rounded-full bg-[#FEF3C7] border border-[#F59E0B] text-[#D97706]">
          Certified Apiary
        </span>
      </div>

      {/* Beekeeper Profile Info */}
      <div className="flex items-center gap-4 p-4 rounded-2xl bg-[#F8FAFC] border border-[#E2E8F0]">
        {beekeeper?.photoUrl ? (
          <img
            src={beekeeper.photoUrl}
            alt={beekeeper.name}
            className="w-14 h-14 rounded-2xl object-cover border border-[#2563EB]/40 shadow-sm"
          />
        ) : (
          <div className="w-14 h-14 rounded-2xl bg-[#FEF3C7] flex items-center justify-center text-3xl border border-[#F59E0B]">
            🐝
          </div>
        )}
        <div className="flex-1 min-w-0">
          <p className="text-[11px] text-[#64748B] uppercase font-bold tracking-wider">Produced By</p>
          <p className="text-[#1E293B] font-bold text-base truncate">{beekeeper?.name || 'Local Beekeeper'}</p>
          <p className="text-xs text-[#2563EB] mt-0.5 flex items-center gap-1">
            <span>📍</span> {beekeeper?.village || 'India'}
          </p>
        </div>
      </div>

      {/* Harvest Attributes Grid */}
      <div className="grid grid-cols-2 gap-3 text-xs">
        <div className="p-3 rounded-xl bg-[#F8FAFC] border border-[#E2E8F0]">
          <p className="text-[#64748B] font-medium">Harvest Date</p>
          <p className="text-[#1E293B] font-bold mt-0.5">
            {harvestDate ? new Date(harvestDate).toLocaleDateString('en-IN', { day: '2-digit', month: 'short', year: 'numeric' }) : '—'}
          </p>
        </div>
        <div className="p-3 rounded-xl bg-[#F8FAFC] border border-[#E2E8F0]">
          <p className="text-[#64748B] font-medium">Harvest Weight</p>
          <p className="text-[#2563EB] font-mono font-bold mt-0.5">{quantityKg} KG</p>
        </div>
        <div className="p-3 rounded-xl bg-[#F8FAFC] border border-[#E2E8F0]">
          <p className="text-[#64748B] font-medium">Source Hive</p>
          <p className="text-[#1E293B] font-mono font-bold mt-0.5">{hiveCode || 'Registered Hive'}</p>
        </div>
        <div className="p-3 rounded-xl bg-[#F8FAFC] border border-[#E2E8F0]">
          <p className="text-[#64748B] font-medium">Apiary Cluster</p>
          <p className="text-[#1E293B] font-medium mt-0.5">{clusterName || 'Regional Cluster'}</p>
        </div>
      </div>

      {/* Harvest Photo Thumbnail if available */}
      {batchPhotoUrl && (
        <div className="pt-2">
          <p className="text-[11px] text-[#64748B] mb-2 font-medium">Harvest Photo Documentation:</p>
          <img
            src={batchPhotoUrl}
            alt="Honey Harvest"
            className="w-full h-40 rounded-xl object-cover border border-[#E2E8F0]"
          />
        </div>
      )}
    </Card>
  )
}

export default BeekeeperCard
