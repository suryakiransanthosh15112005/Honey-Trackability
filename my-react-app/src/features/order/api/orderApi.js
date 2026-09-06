import apiClient from '../../../services/axios'

export const orderApi = {
  // Customer endpoints
  checkout: (data) => apiClient.post('/orders/checkout', data),
  getMyOrders: (params = {}) => apiClient.get('/orders', { params }),
  getMyOrderByNumber: (orderNumber) => apiClient.get(`/orders/${orderNumber}`),
  cancelOrder: (orderNumber) => apiClient.put(`/orders/${orderNumber}/cancel`),

  // Beekeeper endpoints
  getBeekeeperOrders: (params = {}) => apiClient.get('/beekeepers/orders', { params }),
  updateOrderStatus: (orderNumber, status) =>
    apiClient.put(`/beekeepers/orders/${orderNumber}/status`, { status }),
}

export default orderApi
