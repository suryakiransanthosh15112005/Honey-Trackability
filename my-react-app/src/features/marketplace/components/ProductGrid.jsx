import React from 'react'
import ProductCard from './ProductCard'

/**
 * ProductGrid — renders luxury product cards or polished loading/empty state.
 */
const ProductGrid = ({ products, loading, error }) => {
  if (loading) {
    return (
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
        {[1, 2, 3, 4, 5, 6, 7, 8].map((n) => (
          <div
            key={n}
            className="rounded-2xl bg-white border border-slate-200 overflow-hidden shadow-xs animate-pulse p-0 flex flex-col"
          >
            <div className="aspect-4/3 w-full bg-slate-200" />
            <div className="p-5 space-y-3 flex-1 flex flex-col justify-between">
              <div className="space-y-2">
                <div className="h-4 bg-slate-200 rounded w-1/3" />
                <div className="h-5 bg-slate-200 rounded w-4/5" />
                <div className="h-3 bg-slate-200 rounded w-1/2" />
              </div>
              <div className="pt-3 border-t border-slate-100 flex items-center justify-between">
                <div className="h-6 bg-slate-200 rounded w-1/4" />
                <div className="h-4 bg-slate-200 rounded w-1/4" />
              </div>
              <div className="h-9 bg-slate-200 rounded-xl w-full" />
            </div>
          </div>
        ))}
      </div>
    )
  }

  if (error) {
    return (
      <div className="p-6 rounded-2xl bg-amber-50 border border-amber-200 text-slate-800 space-y-2 text-center max-w-lg mx-auto my-8">
        <span className="text-3xl block">⚠️</span>
        <h4 className="font-bold text-base text-slate-900">Failed to Load Marketplace Honey</h4>
        <p className="text-xs text-slate-600">{error}</p>
      </div>
    )
  }

  if (!products || products.length === 0) {
    return (
      <div className="py-16 px-4 text-center rounded-2xl bg-white border border-slate-200 shadow-xs max-w-xl mx-auto space-y-4 my-8">
        <div className="w-16 h-16 rounded-full bg-amber-50 border border-amber-200 flex items-center justify-center mx-auto text-3xl">
          🍯
        </div>
        <div className="space-y-1">
          <h3 className="font-['Outfit'] font-bold text-lg text-slate-900">
            No Verified Honey Matching Your Filters
          </h3>
          <p className="text-xs text-slate-600 max-w-md mx-auto">
            We couldn't find any batches matching your current search criteria. Try choosing another floral source, clearing your search query, or resetting filters.
          </p>
        </div>
      </div>
    )
  }

  return (
    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
      {products.map((p) => (
        <ProductCard key={p.id} product={p} />
      ))}
    </div>
  )
}

export default ProductGrid
