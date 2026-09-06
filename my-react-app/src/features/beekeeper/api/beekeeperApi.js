import apiClient from '../../../services/axios'

export const beekeeperApi = {
  createProfile: (profileData) => apiClient.post('/beekeepers/profile', profileData),
  getProfile: () => apiClient.get('/beekeepers/profile'),
  updateProfile: (profileData) => apiClient.put('/beekeepers/profile', profileData),
  getProfileStatus: () => apiClient.get('/beekeepers/profile/status'),
  uploadPhoto: (file) => {
    const formData = new FormData()
    formData.append('file', file)
    return apiClient.post('/beekeepers/profile/photo', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    })
  },
}

export default beekeeperApi
