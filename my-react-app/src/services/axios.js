import axios from 'axios'
import { getStoredToken, removeStoredToken } from './storage'

// In development: Vite proxy routes '/api' → 'http://localhost:8080', so baseURL = '/api'
// In production: set VITE_API_BASE_URL to your backend's public URL (e.g., https://api.honeychain.example.com)
//   The baseURL becomes 'https://api.honeychain.example.com/api'
// IMPORTANT: VITE_API_BASE_URL must be the URL reachable from the USER'S BROWSER — not a Docker service name.
const apiBaseUrl = import.meta.env.VITE_API_BASE_URL
  ? `${import.meta.env.VITE_API_BASE_URL}/api`
  : '/api'

const apiClient = axios.create({
  baseURL: apiBaseUrl,
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json',
  },
})

// Request interceptor — attach JWT token if present
apiClient.interceptors.request.use(
  (config) => {
    const token = getStoredToken()
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

// Response interceptor — centralized error handling
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    const status = error.response?.status

    if (status === 401) {
      removeStoredToken()
      // Only redirect if not already on an auth page
      if (!window.location.pathname.startsWith('/login') && !window.location.pathname.startsWith('/otp-login')) {
        window.location.href = '/login?session=expired'
      }
    } else if (status === 403) {
      console.warn('Access Forbidden: insufficient privileges for this endpoint.')
    }

    return Promise.reject(error)
  }
)

export default apiClient
