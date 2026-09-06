// ====================================================================
// HONEYCHAIN — MARKETPLACE API LAYER
// All HTTP calls for marketplace and beekeeper product management.
// Uses the shared axios instance (auth token auto-injected).
// Public endpoints (/api/products/**) work without auth.
// ====================================================================

import apiClient from '../../../services/axios'

// ── Public Marketplace ─────────────────────────────────────────────
export const productApi = {
  /**
   * Browse public marketplace with filters, search, sort, pagination.
   * @param {Object} params - { page, size, search, region, flowerSource, minPrice, maxPrice, sortBy }
   */
  getProducts: (params = {}) => apiClient.get('/products', { params }),

  /**
   * Get full product detail (public — no auth needed).
   * @param {number|string} productId
   */
  getProduct: (productId) => apiClient.get(`/products/${productId}`),

  // ── Beekeeper Product Management (BEEKEEPER role required) ────────

  /**
   * Get the authenticated beekeeper's own product listings.
   * @param {Object} params - { page, size }
   */
  getMyProducts: (params = {}) => apiClient.get('/beekeepers/products', { params }),

  /**
   * Get eligible (PURE/QR_GENERATED/IN_STOCK) batches that can be listed.
   * Re-uses the existing batch endpoint filtered by eligible status.
   */
  getEligibleBatches: () => apiClient.get('/beekeepers/batches', {
    params: { status: 'PURE,QR_GENERATED,IN_STOCK' }
  }),

  /**
   * Create a new product listing for a PURE/verified batch.
   * @param {Object} data - ProductCreateRequest DTO
   */
  createProduct: (data) => apiClient.post('/beekeepers/products', data),

  /**
   * Update an existing product listing.
   * @param {number|string} productId
   * @param {Object} data - ProductUpdateRequest DTO
   */
  updateProduct: (productId, data) => apiClient.put(`/beekeepers/products/${productId}`, data),

  /**
   * Toggle a product listing active/inactive.
   * @param {number|string} productId
   * @param {boolean} active
   */
  setProductStatus: (productId, active) =>
    apiClient.patch(`/beekeepers/products/${productId}/status`, null, {
      params: { active },
    }),
}

export default productApi
