import React from 'react'
import { FLOWER_SOURCES, HONEY_REGIONS, SORT_OPTIONS } from '../constants/marketplaceConstants'

/**
 * ProductFilters — executive search, category, region, and sort filter bar.
 */
const ProductFilters = ({
  filters,
  onSearchChange,
  onFilterChange,
  onReset,
}) => {
  const popularSources = ['MULTIFLORA', 'JAMUN', 'MUSTARD', 'SUNFLOWER', 'KARANJ']

  const hasActiveFilters =
    Boolean(filters.search) ||
    Boolean(filters.flowerSource) ||
    Boolean(filters.region) ||
    filters.sortBy !== 'newest'

  return (
    <div className="bg-white rounded-2xl border border-slate-200 shadow-xs p-5 space-y-4">
      {/* Top Header Row */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 pb-3 border-b border-slate-100">
        <div className="flex items-center gap-2">
          <span className="w-8 h-8 rounded-lg bg-amber-50 border border-amber-200 flex items-center justify-center text-sm">
            🔍
          </span>
          <div>
            <h3 className="font-['Outfit'] font-bold text-base text-slate-900">
              Filter Verified Honey
            </h3>
            <p className="text-xs text-slate-600">
              Narrow down by botanical floral source, apiary territory, or purity tier.
            </p>
          </div>
        </div>

        {hasActiveFilters && (
          <button
            onClick={onReset}
            className="inline-flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-xs font-bold text-slate-700 hover:text-blue-600 bg-slate-100 hover:bg-slate-200 transition-colors self-start sm:self-auto cursor-pointer"
            type="button"
          >
            <span>✕</span>
            <span>Reset Filters</span>
          </button>
        )}
      </div>

      {/* Main Filter Controls Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-3.5">
        {/* Search Input */}
        <div className="space-y-1 sm:col-span-2 lg:col-span-1">
          <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider" htmlFor="filter-search">
            Search Honey / Beekeeper
          </label>
          <div className="relative">
            <span className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none text-slate-600 text-xs">
              🔎
            </span>
            <input
              id="filter-search"
              type="text"
              className="w-full pl-8 pr-8 py-2 text-xs sm:text-sm bg-slate-50 border border-slate-200 rounded-xl focus:bg-white focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent text-slate-900 transition-all placeholder:text-slate-600"
              placeholder="e.g. Wildflower, Nilgiris, Ramesh..."
              value={filters.search || ''}
              onChange={(e) => onSearchChange(e.target.value)}
            />
            {filters.search && (
              <button
                type="button"
                onClick={() => onSearchChange('')}
                className="absolute inset-y-0 right-0 pr-2.5 flex items-center text-slate-600 hover:text-slate-800 text-xs cursor-pointer"
                title="Clear search"
              >
                ✕
              </button>
            )}
          </div>
        </div>

        {/* Flower Source Filter */}
        <div className="space-y-1">
          <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider" htmlFor="filter-flower">
            Flower Source
          </label>
          <select
            id="filter-flower"
            className="w-full px-3 py-2 text-xs sm:text-sm bg-slate-50 border border-slate-200 rounded-xl focus:bg-white focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent text-slate-900 transition-all cursor-pointer font-medium"
            value={filters.flowerSource || ''}
            onChange={(e) => onFilterChange('flowerSource', e.target.value)}
          >
            {FLOWER_SOURCES.map((f) => (
              <option key={f.value} value={f.value}>
                {f.label}
              </option>
            ))}
          </select>
        </div>

        {/* Region Filter */}
        <div className="space-y-1">
          <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider" htmlFor="filter-region">
            Geographic Region
          </label>
          <select
            id="filter-region"
            className="w-full px-3 py-2 text-xs sm:text-sm bg-slate-50 border border-slate-200 rounded-xl focus:bg-white focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent text-slate-900 transition-all cursor-pointer font-medium"
            value={filters.region || ''}
            onChange={(e) => onFilterChange('region', e.target.value)}
          >
            {HONEY_REGIONS.map((r) => (
              <option key={r.value} value={r.value}>
                {r.label}
              </option>
            ))}
          </select>
        </div>

        {/* Sort By */}
        <div className="space-y-1">
          <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider" htmlFor="filter-sort">
            Sort Order
          </label>
          <select
            id="filter-sort"
            className="w-full px-3 py-2 text-xs sm:text-sm bg-slate-50 border border-slate-200 rounded-xl focus:bg-white focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent text-slate-900 transition-all cursor-pointer font-medium"
            value={filters.sortBy || 'newest'}
            onChange={(e) => onFilterChange('sortBy', e.target.value)}
          >
            {SORT_OPTIONS.map((s) => (
              <option key={s.value} value={s.value}>
                {s.label}
              </option>
            ))}
          </select>
        </div>
      </div>

      {/* Quick Category Chips */}
      <div className="pt-2 flex items-center gap-2 flex-wrap text-xs">
        <span className="text-slate-600 font-bold mr-1">Popular:</span>
        <button
          type="button"
          onClick={() => onFilterChange('flowerSource', '')}
          className={`px-3 py-1 rounded-full text-xs font-semibold transition-all cursor-pointer ${
            !filters.flowerSource
              ? 'bg-blue-600 text-white shadow-xs'
              : 'bg-slate-100 hover:bg-slate-200 text-slate-700'
          }`}
        >
          All Honey
        </button>
        {popularSources.map((src) => {
          const match = FLOWER_SOURCES.find((f) => f.value === src)
          const isSelected = filters.flowerSource === src
          return (
            <button
              key={src}
              type="button"
              onClick={() => onFilterChange('flowerSource', isSelected ? '' : src)}
              className={`px-3 py-1 rounded-full text-xs font-semibold transition-all cursor-pointer ${
                isSelected
                  ? 'bg-amber-500 text-slate-900 font-bold shadow-xs'
                  : 'bg-slate-100 hover:bg-slate-200 text-slate-700'
              }`}
            >
              🍯 {match?.label || src}
            </button>
          )
        })}
      </div>
    </div>
  )
}

export default ProductFilters
