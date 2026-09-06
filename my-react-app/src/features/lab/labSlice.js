import { createSlice, createAsyncThunk } from '@reduxjs/toolkit'
import labApi from './api/labApi'

export const fetchPendingTests = createAsyncThunk('lab/fetchPending', async (_, { rejectWithValue }) => {
  try {
    const res = await labApi.getPendingTests()
    return res.data.data
  } catch (err) {
    return rejectWithValue(err.response?.data?.message || 'Failed to load pending tests')
  }
})

export const fetchLabTest = createAsyncThunk('lab/fetchOne', async (batchId, { rejectWithValue }) => {
  try {
    const res = await labApi.getLabTest(batchId)
    return res.data.data
  } catch (err) {
    return rejectWithValue(err.response?.data?.message || 'Failed to load lab test details')
  }
})

export const submitLabTest = createAsyncThunk('lab/submit', async ({ batchId, data }, { rejectWithValue }) => {
  try {
    const res = await labApi.submitLabTest(batchId, data)
    return res.data.data
  } catch (err) {
    return rejectWithValue(err.response?.data?.message || 'Failed to submit lab test')
  }
})

export const fetchLabStats = createAsyncThunk('lab/fetchStats', async (_, { rejectWithValue }) => {
  try {
    const res = await labApi.getLabStats()
    return res.data.data
  } catch (err) {
    return rejectWithValue(err.response?.data?.message || 'Failed to load lab statistics')
  }
})

const labSlice = createSlice({
  name: 'lab',
  initialState: {
    pendingBatches: [],
    selectedTest: null,
    stats: { pending: 0, completed: 0, pure: 0, underReview: 0, failed: 0 },
    loading: false,
    error: null,
  },
  reducers: {
    clearLabError(state) {
      state.error = null
    },
    clearSelectedTest(state) {
      state.selectedTest = null
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
      .addCase(fetchPendingTests.pending, pending)
      .addCase(fetchPendingTests.fulfilled, (state, action) => {
        state.loading = false
        state.pendingBatches = action.payload || []
      })
      .addCase(fetchPendingTests.rejected, rejected)

      .addCase(fetchLabTest.pending, pending)
      .addCase(fetchLabTest.fulfilled, (state, action) => {
        state.loading = false
        state.selectedTest = action.payload
      })
      .addCase(fetchLabTest.rejected, rejected)

      .addCase(submitLabTest.pending, pending)
      .addCase(submitLabTest.fulfilled, (state, action) => {
        state.loading = false
        state.selectedTest = action.payload
        state.pendingBatches = state.pendingBatches.filter(
          (b) => b.batchId !== action.payload.batchId
        )
      })
      .addCase(submitLabTest.rejected, rejected)

      .addCase(fetchLabStats.fulfilled, (state, action) => {
        state.stats = action.payload
      })
  },
})

export const { clearLabError, clearSelectedTest } = labSlice.actions
export default labSlice.reducer
