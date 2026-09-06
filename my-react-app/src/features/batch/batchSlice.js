import { createSlice, createAsyncThunk } from '@reduxjs/toolkit'
import batchApi from './api/batchApi'

export const fetchBatches = createAsyncThunk('batch/fetchAll', async (params, { rejectWithValue }) => {
  try {
    const res = await batchApi.getBatches(params)
    return res.data.data
  } catch (err) {
    return rejectWithValue(err.response?.data?.message || 'Failed to fetch batches')
  }
})

export const fetchBatch = createAsyncThunk('batch/fetchOne', async (batchId, { rejectWithValue }) => {
  try {
    const res = await batchApi.getBatch(batchId)
    return res.data.data
  } catch (err) {
    return rejectWithValue(err.response?.data?.message || 'Failed to fetch batch')
  }
})

export const createBatch = createAsyncThunk('batch/create', async (data, { rejectWithValue }) => {
  try {
    const res = await batchApi.createBatch(data)
    return res.data.data
  } catch (err) {
    return rejectWithValue(err.response?.data?.message || 'Failed to create batch')
  }
})

export const updateBatch = createAsyncThunk('batch/update', async ({ batchId, data }, { rejectWithValue }) => {
  try {
    const res = await batchApi.updateBatch(batchId, data)
    return res.data.data
  } catch (err) {
    return rejectWithValue(err.response?.data?.message || 'Failed to update batch')
  }
})

export const sendForTesting = createAsyncThunk('batch/sendForTesting', async (batchId, { rejectWithValue }) => {
  try {
    const res = await batchApi.sendForTesting(batchId)
    return res.data.data
  } catch (err) {
    return rejectWithValue(err.response?.data?.message || 'Failed to send batch for testing')
  }
})

export const fetchBatchStats = createAsyncThunk('batch/fetchStats', async (_, { rejectWithValue }) => {
  try {
    const res = await batchApi.getBatchStats()
    return res.data.data
  } catch (err) {
    return rejectWithValue(err.response?.data?.message || 'Failed to fetch batch stats')
  }
})

const batchSlice = createSlice({
  name: 'batch',
  initialState: {
    batches: [],
    pageInfo: {
      pageNumber: 0,
      pageSize: 10,
      totalElements: 0,
      totalPages: 0,
      isLast: true,
    },
    selectedBatch: null,
    stats: { total: 0, created: 0, sentForTesting: 0 },
    loading: false,
    error: null,
  },
  reducers: {
    clearBatchError(state) {
      state.error = null
    },
    clearSelectedBatch(state) {
      state.selectedBatch = null
    },
  },
  extraReducers: (builder) => {
    const pending = (state) => {
      state.loading = true
      state.error = null
    }
    const rejected = (state, action) => {
      state.loading = false
      state.error = action.payload
    }

    builder
      .addCase(fetchBatches.pending, pending)
      .addCase(fetchBatches.fulfilled, (state, action) => {
        state.loading = false
        const payload = action.payload
        if (Array.isArray(payload)) {
          state.batches = payload
          state.pageInfo = {
            pageNumber: 0,
            pageSize: payload.length || 10,
            totalElements: payload.length,
            totalPages: 1,
            isLast: true,
          }
        } else if (payload && Array.isArray(payload.content)) {
          state.batches = payload.content
          state.pageInfo = {
            pageNumber: payload.pageNumber || 0,
            pageSize: payload.pageSize || 10,
            totalElements: payload.totalElements || payload.content.length,
            totalPages: payload.totalPages || 1,
            isLast: payload.isLast ?? true,
          }
        } else {
          state.batches = []
          state.pageInfo = {
            pageNumber: 0,
            pageSize: 10,
            totalElements: 0,
            totalPages: 0,
            isLast: true,
          }
        }
      })
      .addCase(fetchBatches.rejected, rejected)

      .addCase(fetchBatch.pending, pending)
      .addCase(fetchBatch.fulfilled, (state, action) => {
        state.loading = false
        state.selectedBatch = action.payload
      })
      .addCase(fetchBatch.rejected, rejected)

      .addCase(createBatch.pending, pending)
      .addCase(createBatch.fulfilled, (state, action) => {
        state.loading = false
        if (action.payload) {
          state.batches.unshift(action.payload)
          state.selectedBatch = action.payload
        }
      })
      .addCase(createBatch.rejected, rejected)

      .addCase(updateBatch.pending, pending)
      .addCase(updateBatch.fulfilled, (state, action) => {
        state.loading = false
        if (action.payload) {
          const idx = state.batches.findIndex((b) => b.batchId === action.payload.batchId)
          if (idx !== -1) state.batches[idx] = action.payload
          if (state.selectedBatch?.batchId === action.payload.batchId) {
            state.selectedBatch = action.payload
          }
        }
      })
      .addCase(updateBatch.rejected, rejected)

      .addCase(sendForTesting.pending, pending)
      .addCase(sendForTesting.fulfilled, (state, action) => {
        state.loading = false
        if (action.payload) {
          const idx = state.batches.findIndex((b) => b.batchId === action.payload.batchId)
          if (idx !== -1) state.batches[idx] = action.payload
          if (state.selectedBatch?.batchId === action.payload.batchId) {
            state.selectedBatch = action.payload
          }
        }
      })
      .addCase(sendForTesting.rejected, rejected)

      .addCase(fetchBatchStats.fulfilled, (state, action) => {
        state.stats = action.payload || { total: 0, created: 0, sentForTesting: 0 }
      })
  },
})

export const { clearBatchError, clearSelectedBatch } = batchSlice.actions
export default batchSlice.reducer
