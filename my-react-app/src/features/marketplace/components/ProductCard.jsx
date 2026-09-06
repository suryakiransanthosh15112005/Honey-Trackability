import React from 'react'
import { Link } from 'react-router-dom'
import PurityBadge from './PurityBadge'
import VerifiedBadge from './VerifiedBadge'

/**
 * ProductCard — executive marketplace listing card with rich visual hierarchy.
 */
const ProductCard = ({ product }) => {
  if (!product) return null

  const {
    id,
    batchId,
    productName,
    flowerSource,
    region,
    pricePerKg,
    availableQuantityKg,
    imageUrl,
    verified,
    purityScore,
    beekeeper,
  } = product

  const defaultImg =
    'https://images.unsplash.com/photo-1587049352846-4a222e784d38?w=600&auto=format&fit=crop&q=80'

  const stockKg = Number(availableQuantityKg || 0)
  const isLowStock = stockKg > 0 && stockKg <= 10

  return (
    <div className="group rounded-2xl bg-white border border-slate-200 shadow-xs hover:shadow-lg hover:border-blue-400 transition-all duration-300 flex flex-col overflow-hidden transform hover:-translate-y-1">
      {/* Product Image & Badges Container */}
      <div className="relative aspect-4/3 w-full bg-amber-50/50 overflow-hidden">
        <img
          src={imageUrl || defaultImg}
          alt={productName}
          className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500 ease-out"
          onError={(e) => {
            e.target.src = defaultImg
          }}
        />

        {/* Floating Badges */}
        <div className="absolute top-3 left-3 right-3 flex items-start justify-between gap-2 pointer-events-none">
          <div className="pointer-events-auto">
            <VerifiedBadge verified={verified} batchId={batchId} />
          </div>
          {purityScore != null && (
            <div className="pointer-events-auto shadow-xs rounded-full">
              <PurityBadge score={purityScore} size="sm" />
            </div>
          )}
        </div>

        {/* Bottom image gradient for subtle contrast */}
        <div className="absolute inset-x-0 bottom-0 h-10 bg-linear-to-t from-slate-900/30 to-transparent pointer-events-none" />
      </div>

      {/* Product Details Content */}
      <div className="p-5 flex-1 flex flex-col justify-between gap-4">
        <div className="space-y-2.5">
          {/* Floral source & Regional Origin */}
          <div className="flex items-center justify-between gap-2 text-xs">
            <span className="inline-flex items-center px-2.5 py-0.5 rounded-full font-bold bg-amber-50 text-amber-900 border border-amber-200/80">
              🍯 {flowerSource || 'Multiflora'}
            </span>
            <span className="text-slate-600 font-medium truncate max-w-36">
              📍 {region || 'India'}
            </span>
          </div>

          {/* Product Title */}
          <h3 className="font-['Outfit'] font-bold text-base sm:text-lg text-slate-900 line-clamp-1 group-hover:text-blue-600 transition-colors">
            <Link to={`/marketplace/product/${id}`} className="hover:underline">
              {productName}
            </Link>
          </h3>

          {/* Beekeeper Signature */}
          {beekeeper && (
            <div className="flex items-center gap-2 pt-0.5 text-xs text-slate-600">
              <span className="w-6 h-6 rounded-full bg-blue-50 border border-blue-200 flex items-center justify-center text-xs shrink-0">
                🧑‍🌾
              </span>
              <span className="truncate">
                Harvested by <strong className="text-slate-800 font-semibold">{beekeeper.name}</strong>
                {beekeeper.village ? ` (${beekeeper.village})` : ''}
              </span>
            </div>
          )}
        </div>

        {/* Pricing & Stock Scarcity */}
        <div className="pt-3 border-t border-slate-100 flex items-baseline justify-between gap-2">
          <div className="flex items-baseline gap-1">
            <span className="text-xl sm:text-2xl font-black font-mono text-slate-900">
              ₹{Number(pricePerKg).toFixed(0)}
            </span>
            <span className="text-xs text-slate-600 font-semibold">/ kg</span>
          </div>

          <div>
            {isLowStock ? (
              <span className="inline-flex items-center gap-1 text-xs font-bold text-amber-900 bg-amber-50 border border-amber-300 px-2 py-0.5 rounded-md">
                ⚡ Only {stockKg} kg left
              </span>
            ) : (
              <span className="text-xs text-slate-600 font-medium">
                <strong className="text-slate-800 font-semibold">{stockKg} kg</strong> available
              </span>
            )}
          </div>
        </div>

        {/* Action Button */}
        <div>
          <Link
            to={`/marketplace/product/${id}`}
            className="w-full inline-flex items-center justify-center gap-2 py-2.5 px-4 rounded-xl bg-blue-600 hover:bg-blue-700 active:bg-blue-800 text-white text-xs sm:text-sm font-bold shadow-xs hover:shadow-md transition-all duration-200"
          >
            <span>View Details & Proof</span>
            <span>→</span>
          </Link>
        </div>
      </div>
    </div>
  )
}

export default ProductCard
