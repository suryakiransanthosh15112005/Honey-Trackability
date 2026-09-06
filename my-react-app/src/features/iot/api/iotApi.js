import apiClient from '../../../services/axios'

export const iotApi = {
  // Health summary for all beekeeper hives
  getAllHivesHealth: () => apiClient.get('/beekeepers/hives/health'),

  // Single hive health
  getHiveHealth: (hiveId) => apiClient.get(`/beekeepers/hives/${hiveId}/health`),

  // Latest sensor reading
  getLatestSensor: (hiveId) => apiClient.get(`/beekeepers/hives/${hiveId}/sensors/latest`),

  // Paginated sensor history
  getSensorHistory: (hiveId, page = 0, size = 20) =>
    apiClient.get(`/beekeepers/hives/${hiveId}/sensors/history?page=${page}&size=${size}`),
}

export default iotApi
