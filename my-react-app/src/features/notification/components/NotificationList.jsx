import React, { useState } from 'react'
import NotificationItem from './NotificationItem'
import Button from '../../../components/ui/Button'

export const NotificationList = ({ items = [], onMarkAllRead, loading }) => {
  const [filter, setFilter] = useState('ALL') // 'ALL' | 'UNREAD'

  const filteredItems = items.filter((i) => {
    if (filter === 'UNREAD') return !i.isRead
    return true
  })

  return (
    <div className="space-y-4">
      {/* Filter Tabs & Bulk Actions */}
      <div className="flex items-center justify-between flex-wrap gap-3 pb-2 border-b border-slate-100">
        <div className="flex items-center gap-2">
          <button
            type="button"
            className={`px-3 py-1.5 rounded-xl text-xs font-semibold transition-all ${
              filter === 'ALL'
                ? 'bg-blue-600 text-white font-bold shadow-sm'
                : 'text-slate-600 hover:text-slate-900 bg-slate-100'
            }`}
            onClick={() => setFilter('ALL')}
          >
            All ({items.length})
          </button>
          <button
            type="button"
            className={`px-3 py-1.5 rounded-xl text-xs font-semibold transition-all ${
              filter === 'UNREAD'
                ? 'bg-blue-600 text-white font-bold shadow-sm'
                : 'text-slate-600 hover:text-slate-900 bg-slate-100'
            }`}
            onClick={() => setFilter('UNREAD')}
          >
            Unread ({items.filter((i) => !i.isRead).length})
          </button>
        </div>

        {items.some((i) => !i.isRead) && (
          <Button variant="ghost" size="xs" onClick={onMarkAllRead} className="text-xs text-blue-600 font-bold">
            ✓ Mark All as Read
          </Button>
        )}
      </div>

      {/* Item List */}
      {filteredItems.length === 0 ? (
        <div className="py-16 text-center space-y-3 bg-white rounded-2xl border border-slate-200 shadow-sm">
          <div className="text-4xl">🔔</div>
          <h3 className="text-lg font-bold text-slate-900 font-['Outfit']">No Notifications Found</h3>
          <p className="text-xs text-slate-500 max-w-sm mx-auto">
            {filter === 'UNREAD'
              ? 'You have read all your notifications.'
              : 'You will receive notifications here for orders, lab results, and hive alerts.'}
          </p>
        </div>
      ) : (
        <div className="space-y-2.5">
          {filteredItems.map((item) => (
            <NotificationItem key={item.id} notification={item} />
          ))}
        </div>
      )}
    </div>
  )
}

export default NotificationList
