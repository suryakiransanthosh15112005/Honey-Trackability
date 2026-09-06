import React from 'react'
import { Link } from 'react-router-dom'
import NotificationItem from './NotificationItem'

export const NotificationDropdown = ({ items = [], unreadCount = 0, onMarkAllRead, onClose }) => {
  return (
    <div className="absolute right-0 mt-2 w-80 sm:w-96 rounded-2xl border border-slate-200 bg-white backdrop-blur-xl shadow-2xl z-50 overflow-hidden flex flex-col max-h-[32rem]">
      {/* Header */}
      <div className="p-3.5 border-b border-slate-100 flex items-center justify-between bg-slate-50">
        <div className="flex items-center gap-2">
          <span className="text-base">🔔</span>
          <h3 className="font-bold text-slate-900 text-sm font-['Outfit']">Notifications</h3>
          {unreadCount > 0 && (
            <span className="bg-blue-50 border border-blue-200 text-blue-700 text-[10px] px-2 py-0.5 rounded-full font-bold">
              {unreadCount} new
            </span>
          )}
        </div>
        {unreadCount > 0 && (
          <button
            type="button"
            className="text-xs text-blue-600 hover:text-blue-800 font-semibold transition-colors"
            onClick={onMarkAllRead}
          >
            Mark all read
          </button>
        )}
      </div>

      {/* List Content */}
      <div className="p-3 space-y-2 overflow-y-auto flex-1 custom-scrollbar">
        {items.length === 0 ? (
          <div className="py-8 text-center space-y-2">
            <span className="text-3xl opacity-50 block">🔔</span>
            <p className="text-xs text-slate-500">No notifications yet</p>
          </div>
        ) : (
          items.slice(0, 5).map((item) => (
            <NotificationItem key={item.id} notification={item} onCloseDropdown={onClose} />
          ))
        )}
      </div>

      {/* Footer */}
      <div className="p-2.5 border-t border-slate-100 text-center bg-slate-50">
        <Link
          to="/notifications"
          onClick={onClose}
          className="text-xs text-blue-600 font-bold hover:underline"
        >
          View All Notifications →
        </Link>
      </div>
    </div>
  )
}

export default NotificationDropdown
