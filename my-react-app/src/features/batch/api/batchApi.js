import apiClient from '../../../services/axios'

export const batchApi = {
  getBatches: (params = {}) => apiClient.get('/beekeepers/batches', { params }),
  getBatch: (batchId) => apiClient.get(`/beekeepers/batches/${batchId}`),

  createBatch: (data, idempotencyKey = null) => {
    const headers = {}
    if (idempotencyKey) {
      headers['X-Idempotency-Key'] = idempotencyKey
    }

    if (data instanceof FormData) {
      return apiClient.post('/beekeepers/batches', data, {
        headers: {
          'Content-Type': 'multipart/form-data',
          ...headers,
        },
      })
    }

    return apiClient.post('/beekeepers/batches', data, { headers })
  },

  updateBatch: (batchId, data) => {
    if (data instanceof FormData) {
      return apiClient.put(`/beekeepers/batches/${batchId}`, data, {
        headers: { 'Content-Type': 'multipart/form-data' },
      })
    }
    return apiClient.put(`/beekeepers/batches/${batchId}`, data)
  },

  sendForTesting: (batchId) => apiClient.post(`/beekeepers/batches/${batchId}/send-testing`),
  getBatchStats: () => apiClient.get('/beekeepers/batches/stats'),

  // Blockchain Endpoints
  getBlockchainRecord: (batchId) => apiClient.get(`/beekeepers/batches/${batchId}/blockchain`),
  verifyBlockchainRecord: (batchId) => apiClient.post(`/beekeepers/batches/${batchId}/blockchain/verify`),
}

export default batchApi
