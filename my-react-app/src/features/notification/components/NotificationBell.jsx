import React, { useState, useRef, useEffect } from 'react'
import { useSelector } from 'react-redux'
import useNotifications from '../hooks/useNotifications'
import NotificationDropdown from './NotificationDropdown'

export const NotificationBell = () => {
  const { isAuthenticated } = useSelector((state) => state.auth)
  const { items, unreadCount, markAllRead } = useNotifications(isAuthenticated)
  const [isOpen, setIsOpen] = useState(false)
  const bellRef = useRef(null)

  // Close dropdown on outside click
  useEffect(() => {
    const handleClickOutside = (e) => {
      if (bellRef.current && !bellRef.current.contains(e.target)) {
        setIsOpen(false)
      }
    }
    document.addEventListener('mousedown', handleClickOutside)
    return () => document.removeEventListener('mousedown', handleClickOutside)
  }, [])

  if (!isAuthenticated) return null

  return (
    <div className="relative" ref={bellRef}>
      <button
        type="button"
        id="nav-notification-bell"
        className="btn btn--ghost btn--sm relative p-2 text-lg hover:bg-white/5 transition-colors"
        onClick={() => setIsOpen((prev) => !prev)}
        aria-label="Notifications"
        title="Notifications"
      >
        <span>🔔</span>
        {unreadCount > 0 && (
          <span className="absolute -top-1 -right-1 w-5 h-5 rounded-full bg-blue-600 text-white font-bold text-[10px] flex items-center justify-center border-2 border-white animate-bounce shadow-sm">
            {unreadCount > 9 ? '9+' : unreadCount}
          </span>
        )}
      </button>

      {isOpen && (
        <NotificationDropdown
          items={items}
          unreadCount={unreadCount}
          onMarkAllRead={markAllRead}
          onClose={() => setIsOpen(false)}
        />
      )}
    </div>
  )
}

export default NotificationBell
