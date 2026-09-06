import { createSlice, createAsyncThunk } from '@reduxjs/toolkit'
import authApi from './api/authApi'
import {
  getStoredToken,
  setStoredToken,
  removeStoredToken,
  getStoredRole,
  setStoredRole,
  getStoredUser,
  setStoredUser
} from '../../services/storage'

export const loginWithPassword = createAsyncThunk(
  'auth/loginWithPassword',
  async (credentials, { rejectWithValue }) => {
    try {
      const response = await authApi.login(credentials)
      return response.data
    } catch (err) {
      return rejectWithValue(err.response?.data?.message || 'Login failed')
    }
  }
)

export const sendOtp = createAsyncThunk(
  'auth/sendOtp',
  async (data, { rejectWithValue }) => {
    try {
      const response = await authApi.sendOtp(data)
      return response.data
    } catch (err) {
      return rejectWithValue(err.response?.data?.message || 'Failed to send OTP')
    }
  }
)

export const verifyOtp = createAsyncThunk(
  'auth/verifyOtp',
  async (data, { rejectWithValue }) => {
    try {
      const response = await authApi.verifyOtp(data)
      return response.data
    } catch (err) {
      return rejectWithValue(err.response?.data?.message || 'Invalid or expired OTP')
    }
  }
)

const initialState = {
  token: getStoredToken(),
  role: getStoredRole(),
  user: getStoredUser(),
  phoneNumber: getStoredUser()?.phoneNumber || null,
  isAuthenticated: !!getStoredToken(),
  loading: false,
  error: null,
  otpSent: false,
}

const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    logout(state) {
      state.token = null
      state.role = null
      state.user = null
      state.phoneNumber = null
      state.isAuthenticated = false
      state.otpSent = false
      state.error = null
      removeStoredToken()
    },
    clearError(state) {
      state.error = null
    },
    resetOtpState(state) {
      state.otpSent = false
    },
  },
  extraReducers: (builder) => {
    // Password Login
    builder
      .addCase(loginWithPassword.pending, (state) => {
        state.loading = true
        state.error = null
      })
      .addCase(loginWithPassword.fulfilled, (state, action) => {
        state.loading = false
        state.isAuthenticated = true
        state.token = action.payload.token
        state.role = action.payload.role
        state.phoneNumber = action.payload.phoneNumber
        state.user = action.payload
        setStoredToken(action.payload.token)
        setStoredRole(action.payload.role)
        setStoredUser(action.payload)
      })
      .addCase(loginWithPassword.rejected, (state, action) => {
        state.loading = false
        state.error = action.payload
      })

    // Send OTP
    builder
      .addCase(sendOtp.pending, (state) => {
        state.loading = true
        state.error = null
      })
      .addCase(sendOtp.fulfilled, (state) => {
        state.loading = false
        state.otpSent = true
      })
      .addCase(sendOtp.rejected, (state, action) => {
        state.loading = false
        state.error = action.payload
      })

    // Verify OTP
    builder
      .addCase(verifyOtp.pending, (state) => {
        state.loading = true
        state.error = null
      })
      .addCase(verifyOtp.fulfilled, (state, action) => {
        state.loading = false
        state.isAuthenticated = true
        state.token = action.payload.token
        state.role = action.payload.role
        state.phoneNumber = action.payload.phoneNumber
        state.user = action.payload
        setStoredToken(action.payload.token)
        setStoredRole(action.payload.role)
        setStoredUser(action.payload)
      })
      .addCase(verifyOtp.rejected, (state, action) => {
        state.loading = false
        state.error = action.payload
      })
  },
})

export const { logout, clearError, resetOtpState } = authSlice.actions
export default authSlice.reducer
