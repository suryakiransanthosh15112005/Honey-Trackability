import apiClient from '../../../services/axios'

export const yieldPredictionApi = {
  getYieldPrediction: (hiveId) =>
    apiClient.get(`/beekeepers/hives/${hiveId}/yield-prediction`),

  getAllYieldPredictions: () =>
    apiClient.get('/beekeepers/hives/yield-predictions'),

  refreshYieldPrediction: (hiveId) =>
    apiClient.post(`/beekeepers/hives/${hiveId}/yield-prediction/refresh`),
}

export default yieldPredictionApi
