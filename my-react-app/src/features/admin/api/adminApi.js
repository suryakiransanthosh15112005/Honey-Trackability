import apiClient from '../../../services/axios'

const adminApi = {
  // Top-level Dashboard Overview
  getDashboard: () => apiClient.get('/admin/dashboard'),

  // Beekeeper Governance
  getBeekeepers: (params) => apiClient.get('/admin/beekeepers', { params }),
  getBeekeeperDetails: (id) => apiClient.get(`/admin/beekeepers/${id}`),
  updateBeekeeperStatus: (id, status) =>
    apiClient.patch(`/admin/beekeepers/${id}/status`, { status }),

  // Batch Monitoring
  getBatches: (params) => apiClient.get('/admin/batches', { params }),
  getBatchDetails: (batchId) => apiClient.get(`/admin/batches/${batchId}`),

  // Lab Testing Monitoring
  getLabTests: (params) => apiClient.get('/admin/lab-tests', { params }),

  // Hive & IoT Monitoring
  getHives: (params) => apiClient.get('/admin/hives', { params }),

  // Dispute Management
  getDisputes: (params) => apiClient.get('/admin/disputes', { params }),
  getDisputeDetails: (id) => apiClient.get(`/admin/disputes/${id}`),
  updateDisputeStatus: (id, data) => apiClient.patch(`/admin/disputes/${id}/status`, data),

  // Order Governance & Monitoring
  getOrders: (params) => apiClient.get('/admin/orders', { params }).catch(() => apiClient.get('/orders', { params })),

  // Analytics Suite
  getPurityAnalytics: () => apiClient.get('/admin/analytics/purity'),
  getRegionalAnalytics: () => apiClient.get('/admin/analytics/regions'),
  getProductionTrend: () => apiClient.get('/admin/analytics/production'),
  getSalesAnalytics: () => apiClient.get('/admin/analytics/sales'),
  getVerificationRiskAnalytics: () => apiClient.get('/admin/analytics/verification-risk'),
}

export default adminApi
