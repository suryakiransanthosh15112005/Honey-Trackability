import { createSlice, createAsyncThunk } from '@reduxjs/toolkit'
import hiveApi from './api/hiveApi'

export const fetchHives = createAsyncThunk('hive/fetchAll', async (_, { rejectWithValue }) => {
  try {
    const res = await hiveApi.getHives()
    return res.data.data
  } catch (err) {
    return rejectWithValue(err.response?.data?.message || 'Failed to fetch hives')
  }
})

export const fetchHive = createAsyncThunk('hive/fetchOne', async (id, { rejectWithValue }) => {
  try {
    const res = await hiveApi.getHive(id)
    return res.data.data
  } catch (err) {
    return rejectWithValue(err.response?.data?.message || 'Failed to fetch hive')
  }
})

export const createHive = createAsyncThunk('hive/create', async (data, { rejectWithValue }) => {
  try {
    const res = await hiveApi.createHive(data)
    return res.data.data
  } catch (err) {
    return rejectWithValue(err.response?.data?.message || 'Failed to create hive')
  }
})

export const updateHive = createAsyncThunk('hive/update', async ({ id, data }, { rejectWithValue }) => {
  try {
    const res = await hiveApi.updateHive(id, data)
    return res.data.data
  } catch (err) {
    return rejectWithValue(err.response?.data?.message || 'Failed to update hive')
  }
})

export const updateHiveStatus = createAsyncThunk('hive/updateStatus', async ({ id, status }, { rejectWithValue }) => {
  try {
    const res = await hiveApi.updateHiveStatus(id, status)
    return res.data.data
  } catch (err) {
    return rejectWithValue(err.response?.data?.message || 'Failed to update hive status')
  }
})

const hiveSlice = createSlice({
  name: 'hive',
  initialState: {
    hives: [],
    selectedHive: null,
    loading: false,
    error: null,
  },
  reducers: {
    clearHiveError(state) { state.error = null },
    clearSelectedHive(state) { state.selectedHive = null },
  },
  extraReducers: (builder) => {
    const pending = (state) => { state.loading = true; state.error = null }
    const rejected = (state, action) => { state.loading = false; state.error = action.payload }

    builder
      .addCase(fetchHives.pending, pending)
      .addCase(fetchHives.fulfilled, (state, action) => { state.loading = false; state.hives = action.payload })
      .addCase(fetchHives.rejected, rejected)

      .addCase(fetchHive.pending, pending)
      .addCase(fetchHive.fulfilled, (state, action) => { state.loading = false; state.selectedHive = action.payload })
      .addCase(fetchHive.rejected, rejected)

      .addCase(createHive.pending, pending)
      .addCase(createHive.fulfilled, (state, action) => {
        state.loading = false
        state.hives.unshift(action.payload)
      })
      .addCase(createHive.rejected, rejected)

      .addCase(updateHive.pending, pending)
      .addCase(updateHive.fulfilled, (state, action) => {
        state.loading = false
        const idx = state.hives.findIndex(h => h.id === action.payload.id)
        if (idx !== -1) state.hives[idx] = action.payload
        if (state.selectedHive?.id === action.payload.id) state.selectedHive = action.payload
      })
      .addCase(updateHive.rejected, rejected)

      .addCase(updateHiveStatus.pending, pending)
      .addCase(updateHiveStatus.fulfilled, (state, action) => {
        state.loading = false
        const idx = state.hives.findIndex(h => h.id === action.payload.id)
        if (idx !== -1) state.hives[idx] = action.payload
        if (state.selectedHive?.id === action.payload.id) state.selectedHive = action.payload
      })
      .addCase(updateHiveStatus.rejected, rejected)
  },
})

export const { clearHiveError, clearSelectedHive } = hiveSlice.actions
export default hiveSlice.reducer
