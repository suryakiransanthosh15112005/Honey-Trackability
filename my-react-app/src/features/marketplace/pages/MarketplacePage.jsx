import React from 'react'
import MainLayout from '../../../layouts/MainLayout'
import useProducts from '../hooks/useProducts'
import ProductFilters from '../components/ProductFilters'
import ProductGrid from '../components/ProductGrid'
import { useLanguage } from '../../../i18n/LanguageContext'
import VoiceButton from '../../../components/common/VoiceButton'

/**
 * MarketplacePage — premier public marketplace for authentic verified artisan honey.
 */
const MarketplacePage = () => {
  const { t } = useLanguage()
  const {
    products,
    filters,
    totalElements,
    totalPages,
    loading,
    error,
    updateFilter,
    updateSearch,
    goToPage,
    resetFilters,
  } = useProducts()

  const voiceText = `${t('marketplace.title', 'Artisan Honey Directly From Indian Beekeepers')}. ${t('marketplace.subtitle', 'Trace every jar back to the hive, beekeeper, and lab purity report via cryptographic blockchain proof.')}`

  return (
    <MainLayout>
      <div className="space-y-8 pb-16">
        {/* Modern Marketplace Hero */}
        <section className="relative overflow-hidden bg-linear-to-b from-blue-50/70 via-amber-50/30 to-white border-b border-slate-200 py-10 sm:py-14">
          <div className="container mx-auto px-4 sm:px-6 lg:px-8 max-w-7xl">
            <div className="max-w-3xl space-y-4">
              <div className="flex items-center gap-3 flex-wrap">
                <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-bold bg-amber-50 text-amber-900 border border-amber-300 shadow-xs">
                  🍯 100% PURE & BLOCKCHAIN VERIFIED
                </span>
                <VoiceButton textToSpeak={voiceText} size="sm" />
              </div>

              <h1 className="text-3xl sm:text-4xl lg:text-5xl font-black text-slate-900 font-['Outfit'] tracking-tight leading-tight">
                {t('marketplace.title', 'Artisan Honey Directly From Indian Beekeepers')}
              </h1>

              <p className="text-sm sm:text-base text-slate-600 leading-relaxed">
                {t(
                  'marketplace.subtitle',
                  'Trace every jar back to the hive, beekeeper, and lab purity report via cryptographic blockchain proof.'
                )}
              </p>

              {/* 3 Core Trust Guarantees */}
              <div className="pt-2 flex items-center gap-4 sm:gap-6 flex-wrap text-xs font-semibold text-slate-700">
                <span className="inline-flex items-center gap-1.5 bg-white/80 backdrop-blur-xs px-3 py-1.5 rounded-lg border border-slate-200 shadow-xs">
                  🛡️ KVIC Certified Apiaries
                </span>
                <span className="inline-flex items-center gap-1.5 bg-white/80 backdrop-blur-xs px-3 py-1.5 rounded-lg border border-slate-200 shadow-xs">
                  🔬 Lab Tested Purity
                </span>
                <span className="inline-flex items-center gap-1.5 bg-white/80 backdrop-blur-xs px-3 py-1.5 rounded-lg border border-slate-200 shadow-xs">
                  ⛓️ Cryptographic QR Provenance
                </span>
              </div>
            </div>
          </div>
        </section>

        {/* Marketplace Content Container */}
        <section className="container mx-auto px-4 sm:px-6 lg:px-8 max-w-7xl space-y-6">
          <ProductFilters
            filters={filters}
            onSearchChange={updateSearch}
            onFilterChange={updateFilter}
            onReset={resetFilters}
          />

          {/* Results Summary Header */}
          <div className="flex items-center justify-between gap-4 py-2 border-b border-slate-200 flex-wrap">
            <span className="text-xs sm:text-sm text-slate-600 font-medium">
              Showing <strong className="text-slate-900 font-bold">{products.length}</strong> of{' '}
              <strong className="text-slate-900 font-bold">{totalElements}</strong> verified honey products
            </span>
            <span className="text-xs text-blue-700 font-semibold bg-blue-50 px-2.5 py-1 rounded-full border border-blue-200">
              Live Verified Harvests
            </span>
          </div>

          {/* Product Grid */}
          <ProductGrid products={products} loading={loading} error={error} />

          {/* Pagination Controls */}
          {totalPages > 1 && (
            <div className="pt-6 flex items-center justify-center gap-3">
              <button
                type="button"
                className="px-4 py-2 rounded-xl text-xs sm:text-sm font-bold bg-white border border-slate-300 text-slate-700 hover:bg-slate-50 disabled:opacity-40 disabled:cursor-not-allowed shadow-xs transition-colors cursor-pointer"
                disabled={filters.page === 0}
                onClick={() => goToPage(filters.page - 1)}
              >
                ← Previous
              </button>
              <span className="text-xs sm:text-sm font-semibold text-slate-700 px-3 py-1.5 bg-slate-100 rounded-lg">
                Page {filters.page + 1} of {totalPages}
              </span>
              <button
                type="button"
                className="px-4 py-2 rounded-xl text-xs sm:text-sm font-bold bg-white border border-slate-300 text-slate-700 hover:bg-slate-50 disabled:opacity-40 disabled:cursor-not-allowed shadow-xs transition-colors cursor-pointer"
                disabled={filters.page >= totalPages - 1}
                onClick={() => goToPage(filters.page + 1)}
              >
                Next →
              </button>
            </div>
          )}
        </section>
      </div>
    </MainLayout>
  )
}

export default MarketplacePage
