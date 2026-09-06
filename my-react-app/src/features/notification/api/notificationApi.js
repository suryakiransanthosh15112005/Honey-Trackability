import apiClient from '../../../services/axios'

export const notificationApi = {
  getNotifications: (params = {}) => apiClient.get('/notifications', { params }),
  getUnreadCount: () => apiClient.get('/notifications/unread-count'),
  markAsRead: (id) => apiClient.put(`/notifications/${id}/read`),
  markAllAsRead: () => apiClient.put('/notifications/read-all'),
}

export default notificationApi
