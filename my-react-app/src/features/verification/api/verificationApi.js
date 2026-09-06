import apiClient from '../../../services/axios'

export const verificationApi = {
  // Public verification APIs (No JWT required) — use apiClient (proxy in dev, VITE_API_BASE_URL in prod)
  verifyBatch: (batchId) => apiClient.get(`/public/verify/${batchId}`),
  scanBatch: (batchId) => apiClient.post(`/public/verify/${batchId}/scan`),
  getPublicHistory: (batchId) => apiClient.get(`/public/verify/${batchId}/history`),

  // Beekeeper verification analytics (JWT required)
  getBeekeeperBatchHistory: (batchId) => apiClient.get(`/beekeepers/batches/${batchId}/verification-history`),
}

export default verificationApi

