import { useSelector, useDispatch } from 'react-redux'
import { useEffect, useCallback } from 'react'
import {
  fetchNotifications,
  fetchUnreadCount,
  markNotificationAsRead,
  markAllNotificationsAsRead,
  clearNotificationError,
} from '../notificationSlice'

export const useNotifications = (autoFetch = false) => {
  const notificationState = useSelector((state) => state.notification || {})
  const dispatch = useDispatch()

  useEffect(() => {
    if (autoFetch) {
      dispatch(fetchUnreadCount())
      dispatch(fetchNotifications({ page: 0, size: 20 }))
    }
  }, [autoFetch, dispatch])

  const loadNotifications = useCallback(
    (params = {}) => dispatch(fetchNotifications(params)),
    [dispatch]
  )

  const loadUnreadCount = useCallback(
    () => dispatch(fetchUnreadCount()),
    [dispatch]
  )

  const markAsRead = useCallback(
    (id) => dispatch(markNotificationAsRead(id)),
    [dispatch]
  )

  const markAllRead = useCallback(
    () => dispatch(markAllNotificationsAsRead()),
    [dispatch]
  )

  return {
    items: notificationState.items || [],
    unreadCount: notificationState.unreadCount || 0,
    pageInfo: notificationState.pageInfo || {},
    loading: notificationState.loading || false,
    error: notificationState.error || null,
    fetchNotifications: loadNotifications,
    fetchUnreadCount: loadUnreadCount,
    markAsRead,
    markAllRead,
    clearError: () => dispatch(clearNotificationError()),
  }
}

export default useNotifications
