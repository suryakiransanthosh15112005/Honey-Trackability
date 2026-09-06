import apiClient from '../../../services/axios'

export const customerApi = {
  getProfile: () => apiClient.get('/customers/profile'),
  createProfile: (data) => apiClient.post('/customers/profile', data),
  updateProfile: (data) => apiClient.put('/customers/profile', data),
  getProfileStatus: () => apiClient.get('/customers/profile/status'),
}

export default customerApi
