import apiClient from '../../../services/axios'

export const labApi = {
  getPendingTests: () => apiClient.get('/lab/tests/pending'),
  getLabTest: (batchId) => apiClient.get(`/lab/tests/${batchId}`),
  submitLabTest: (batchId, data) => {
    if (data instanceof FormData) {
      return apiClient.post(`/lab/tests/${batchId}`, data, {
        headers: { 'Content-Type': 'multipart/form-data' },
      })
    }
    return apiClient.post(`/lab/tests/${batchId}`, data)
  },
  getLabStats: () => apiClient.get('/lab/stats'),
  getBeekeeperLabResult: (batchId) => apiClient.get(`/beekeepers/batches/${batchId}/lab-result`),
}

export default labApi
