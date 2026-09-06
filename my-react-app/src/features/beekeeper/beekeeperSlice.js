import { createSlice, createAsyncThunk } from '@reduxjs/toolkit'
import beekeeperApi from './api/beekeeperApi'

export const fetchBeekeeperProfile = createAsyncThunk(
  'beekeeper/fetchProfile',
  async (_, { rejectWithValue }) => {
    try {
      const response = await beekeeperApi.getProfile()
      return response.data.data
    } catch (err) {
      if (err.response?.status === 404) {
        return rejectWithValue({ isNotFound: true, message: 'Profile not found' })
      }
      return rejectWithValue(err.response?.data?.message || 'Failed to fetch beekeeper profile')
    }
  }
)

export const fetchProfileStatus = createAsyncThunk(
  'beekeeper/fetchStatus',
  async (_, { rejectWithValue }) => {
    try {
      const response = await beekeeperApi.getProfileStatus()
      return response.data.data
    } catch (err) {
      return rejectWithValue(err.response?.data?.message || 'Failed to fetch profile status')
    }
  }
)

export const createProfile = createAsyncThunk(
  'beekeeper/createProfile',
  async (profileData, { rejectWithValue }) => {
    try {
      const response = await beekeeperApi.createProfile(profileData)
      return response.data.data
    } catch (err) {
      return rejectWithValue(err.response?.data?.message || 'Failed to create beekeeper profile')
    }
  }
)

export const updateProfile = createAsyncThunk(
  'beekeeper/updateProfile',
  async (profileData, { rejectWithValue }) => {
    try {
      const response = await beekeeperApi.updateProfile(profileData)
      return response.data.data
    } catch (err) {
      return rejectWithValue(err.response?.data?.message || 'Failed to update beekeeper profile')
    }
  }
)

const beekeeperSlice = createSlice({
  name: 'beekeeper',
  initialState: {
    profile: null,
    status: null,
    loading: false,
    fetched: false,
    error: null,
  },
  reducers: {
    clearBeekeeperError(state) {
      state.error = null
    },
    resetBeekeeperState(state) {
      state.profile = null
      state.status = null
      state.loading = false
      state.fetched = false
      state.error = null
    },
  },
  extraReducers: (builder) => {
    // Fetch Profile
    builder
      .addCase(fetchBeekeeperProfile.pending, (state) => {
        state.loading = true
        state.error = null
      })
      .addCase(fetchBeekeeperProfile.fulfilled, (state, action) => {
        state.loading = false
        state.profile = action.payload
        state.fetched = true
        state.error = null
      })
      .addCase(fetchBeekeeperProfile.rejected, (state, action) => {
        state.loading = false
        state.fetched = true
        if (action.payload?.isNotFound) {
          state.profile = null
          state.error = null
        } else {
          state.error =
            typeof action.payload === 'string'
              ? action.payload
              : action.payload?.message || 'Failed to fetch profile'
        }
      })

    // Fetch Status
    builder.addCase(fetchProfileStatus.fulfilled, (state, action) => {
      state.status = action.payload
    })

    // Create Profile
    builder
      .addCase(createProfile.pending, (state) => {
        state.loading = true
        state.error = null
      })
      .addCase(createProfile.fulfilled, (state, action) => {
        state.loading = false
        state.profile = action.payload
        state.fetched = true
        state.status = { completed: true, verificationStatus: action.payload.verificationStatus }
      })
      .addCase(createProfile.rejected, (state, action) => {
        state.loading = false
        state.error = action.payload
      })

    // Update Profile
    builder
      .addCase(updateProfile.pending, (state) => {
        state.loading = true
        state.error = null
      })
      .addCase(updateProfile.fulfilled, (state, action) => {
        state.loading = false
        state.profile = action.payload
        state.fetched = true
      })
      .addCase(updateProfile.rejected, (state, action) => {
        state.loading = false
        state.error = action.payload
      })
  },
})

export const { clearBeekeeperError, resetBeekeeperState } = beekeeperSlice.actions
export default beekeeperSlice.reducer
