import apiClient from '../../../services/axios'

export const authApi = {
  login: (credentials) => apiClient.post('/auth/login', credentials),
  sendOtp: (data) => apiClient.post('/auth/send-otp', data),
  verifyOtp: (data) => apiClient.post('/auth/verify-otp', data),
}

export default authApi
