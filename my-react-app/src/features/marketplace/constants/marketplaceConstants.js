// ====================================================================
// HONEYCHAIN — MARKETPLACE CONSTANTS
// All static reference data used in marketplace forms and filters.
// ====================================================================

export const FLOWER_SOURCES = [
  { value: '', label: 'All Flower Sources' },
  { value: 'MULTIFLORA', label: 'Multiflora' },
  { value: 'JAMUN', label: 'Jamun' },
  { value: 'MUSTARD', label: 'Mustard' },
  { value: 'SUNFLOWER', label: 'Sunflower' },
  { value: 'EUCALYPTUS', label: 'Eucalyptus' },
  { value: 'LITCHI', label: 'Litchi' },
  { value: 'AJWAIN', label: 'Ajwain' },
  { value: 'CORIANDER', label: 'Coriander' },
  { value: 'KARANJ', label: 'Karanj' },
  { value: 'TUALANG', label: 'Tualang' },
  { value: 'OTHER', label: 'Other' },
]

export const HONEY_REGIONS = [
  { value: '', label: 'All Regions' },
  { value: 'Nilgiris, Tamil Nadu', label: 'Nilgiris, Tamil Nadu' },
  { value: 'Coorg, Karnataka', label: 'Coorg, Karnataka' },
  { value: 'Sundarbans, West Bengal', label: 'Sundarbans, West Bengal' },
  { value: 'Kashmir Valley, J&K', label: 'Kashmir Valley, J&K' },
  { value: 'Aravalli Hills, Rajasthan', label: 'Aravalli Hills, Rajasthan' },
  { value: 'Western Ghats, Kerala', label: 'Western Ghats, Kerala' },
  { value: 'Manipur Hills, Manipur', label: 'Manipur Hills, Manipur' },
  { value: 'Uttarakhand Hills', label: 'Uttarakhand Hills' },
  { value: 'Konkan, Maharashtra', label: 'Konkan, Maharashtra' },
  { value: 'Other', label: 'Other' },
]

export const SORT_OPTIONS = [
  { value: 'newest', label: 'Newest First' },
  { value: 'priceAsc', label: 'Price: Low to High' },
  { value: 'priceDesc', label: 'Price: High to Low' },
  { value: 'ratingDesc', label: 'Highest Purity' },
]

export const ELIGIBLE_BATCH_STATUSES = ['PURE', 'QR_GENERATED', 'IN_STOCK']

export const PURITY_TIERS = [
  { min: 95, label: 'Premium', color: '#2563EB' },
  { min: 85, label: 'Standard', color: '#D97706' },
  { min: 0,  label: 'Basic',   color: '#64748B' },
]

export const getPurityTier = (score) => {
  if (score == null) return null
  return PURITY_TIERS.find((t) => score >= t.min) || PURITY_TIERS[PURITY_TIERS.length - 1]
}
