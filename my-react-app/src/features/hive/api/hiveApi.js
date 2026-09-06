import apiClient from '../../../services/axios'

export const hiveApi = {
  getHives: () => apiClient.get('/beekeepers/hives'),
  getHive: (id) => apiClient.get(`/beekeepers/hives/${id}`),
  createHive: (data) => apiClient.post('/beekeepers/hives', data),
  updateHive: (id, data) => apiClient.put(`/beekeepers/hives/${id}`, data),
  updateHiveStatus: (id, status) =>
    apiClient.patch(`/beekeepers/hives/${id}/status`, { status }),
  getHiveCount: () => apiClient.get('/beekeepers/hives/count'),
}

export default hiveApi
