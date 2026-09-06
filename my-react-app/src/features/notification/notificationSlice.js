import { createSlice, createAsyncThunk } from '@reduxjs/toolkit'
import notificationApi from './api/notificationApi'

export const fetchNotifications = createAsyncThunk(
  'notification/fetchAll',
  async (params, { rejectWithValue }) => {
    try {
      const res = await notificationApi.getNotifications(params)
      return res.data.data
    } catch (err) {
      return rejectWithValue(err.response?.data?.message || 'Failed to fetch notifications')
    }
  }
)

export const fetchUnreadCount = createAsyncThunk(
  'notification/fetchUnreadCount',
  async (_, { rejectWithValue }) => {
    try {
      const res = await notificationApi.getUnreadCount()
      return res.data.data?.unreadCount || 0
    } catch (err) {
      return rejectWithValue(err.response?.data?.message || 'Failed to fetch unread count')
    }
  }
)

export const markNotificationAsRead = createAsyncThunk(
  'notification/markAsRead',
  async (id, { rejectWithValue }) => {
    try {
      const res = await notificationApi.markAsRead(id)
      return res.data.data
    } catch (err) {
      return rejectWithValue(err.response?.data?.message || 'Failed to mark notification as read')
    }
  }
)

export const markAllNotificationsAsRead = createAsyncThunk(
  'notification/markAllAsRead',
  async (_, { rejectWithValue }) => {
    try {
      await notificationApi.markAllAsRead()
      return true
    } catch (err) {
      return rejectWithValue(err.response?.data?.message || 'Failed to mark all as read')
    }
  }
)

const notificationSlice = createSlice({
  name: 'notification',
  initialState: {
    items: [],
    pageInfo: { pageNumber: 0, pageSize: 20, totalElements: 0, totalPages: 0, isLast: true },
    unreadCount: 0,
    loading: false,
    error: null,
  },
  reducers: {
    clearNotificationError(state) {
      state.error = null
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchNotifications.pending, (state) => {
        state.loading = true
        state.error = null
      })
      .addCase(fetchNotifications.fulfilled, (state, action) => {
        state.loading = false
        state.items = action.payload.content || []
        state.pageInfo = {
          pageNumber: action.payload.pageNumber,
          pageSize: action.payload.pageSize,
          totalElements: action.payload.totalElements,
          totalPages: action.payload.totalPages,
          isLast: action.payload.isLast,
        }
      })
      .addCase(fetchNotifications.rejected, (state, action) => {
        state.loading = false
        state.error = action.payload
      })

      .addCase(fetchUnreadCount.fulfilled, (state, action) => {
        state.unreadCount = action.payload
      })

      .addCase(markNotificationAsRead.fulfilled, (state, action) => {
        const item = state.items.find((i) => i.id === action.payload.id)
        if (item && !item.isRead) {
          item.isRead = true
          item.readAt = action.payload.readAt
          state.unreadCount = Math.max(0, state.unreadCount - 1)
        }
      })

      .addCase(markAllNotificationsAsRead.fulfilled, (state) => {
        state.items.forEach((i) => {
          i.isRead = true
        })
        state.unreadCount = 0
      })
  },
})

export const { clearNotificationError } = notificationSlice.actions
export default notificationSlice.reducer
