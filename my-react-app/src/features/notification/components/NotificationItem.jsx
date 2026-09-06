import React from 'react'
import { useNavigate } from 'react-router-dom'
import { useNotifications } from '../hooks/useNotifications'

const TYPE_ICONS = {
  LAB_RESULT: '🔬',
  HIVE_ALERT: '🐝',
  NEW_ORDER: '📦',
  ORDER_STATUS: '🚚',
  BATCH_SYNC: '🔄',
  QR_GENERATED: '🏷️',
  PROFILE_STATUS: '👤',
  DISPUTE_UPDATE: '⚖️',
  SYSTEM: '📢',
}

export const NotificationItem = ({ notification, onCloseDropdown }) => {
  const navigate = useNavigate()
  const { markAsRead } = useNotifications()

  if (!notification) return null

  const { id, title, message, type, isRead, createdAt, relatedEntityType, relatedEntityId } = notification
  const icon = TYPE_ICONS[type] || '🔔'

  const handleClick = async () => {
    if (!isRead) {
      markAsRead(id)
    }

    if (onCloseDropdown) {
      onCloseDropdown()
    }

    // Whitelisted route navigation based on relatedEntityType
    if (relatedEntityType && relatedEntityId) {
      switch (relatedEntityType.toUpperCase()) {
        case 'BATCH':
          navigate(`/beekeeper/batches/${relatedEntityId}`)
          break
        case 'ORDER':
          navigate(`/orders`)
          break
        case 'HIVE':
          navigate(`/hives/${relatedEntityId}/health`)
          break
        case 'PROFILE':
          navigate(`/profile`)
          break
        case 'DISPUTE':
          navigate(`/admin/disputes`)
          break
        default:
          break
      }
    }
  }

  return (
    <div
      onClick={handleClick}
      className={`p-3.5 rounded-xl border transition-all cursor-pointer flex items-start gap-3 ${isRead
          ? 'bg-white border-slate-200 opacity-75 hover:opacity-100 shadow-sm'
          : 'bg-blue-50/70 border-blue-200 shadow-sm font-semibold'
        }`}
    >
      <div className="text-2xl p-2 rounded-lg bg-slate-100 border border-slate-200 shrink-0">
        {icon}
      </div>

      <div className="flex-1 min-w-0">
        <div className="flex items-center justify-between gap-2">
          <h4
            className={`text-xs tracking-tight truncate ${isRead ? 'text-slate-500 font-medium' : 'text-slate-900 font-bold'
              }`}
          >
            {title}
          </h4>
          {!isRead && (
            <span className="w-2 h-2 rounded-full bg-blue-600 shrink-0" title="Unread" />
          )}
        </div>

        <p className="text-xs text-slate-700 mt-0.5 line-clamp-2 leading-relaxed">
          {message}
        </p>

        <span className="text-[10px] text-slate-400 font-mono mt-1 block font-medium">
          {createdAt ? new Date(createdAt).toLocaleString('en-IN', { dateStyle: 'short', timeStyle: 'short' }) : ''}
        </span>
      </div>
    </div>
  )
}

export default NotificationItem
