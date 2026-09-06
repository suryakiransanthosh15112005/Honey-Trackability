import apiClient from '../../../services/axios'

export const cartApi = {
  getCart: () => apiClient.get('/cart'),
  addItem: (data) => apiClient.post('/cart/items', data),
  updateItem: (id, data) => apiClient.put(`/cart/items/${id}`, data),
  removeItem: (id) => apiClient.delete(`/cart/items/${id}`),
  clearCart: () => apiClient.delete('/cart'),
}

export default cartApi
