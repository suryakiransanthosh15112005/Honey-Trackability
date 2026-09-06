import apiClient from '../../../services/axios'

export const disputeApi = {
  createDispute: (data) => apiClient.post('/disputes', data),
  getMyDisputes: (params) => apiClient.get('/disputes/my', { params }),
  getDisputeById: (id) => apiClient.get(`/disputes/${id}`),
}

export default disputeApi
