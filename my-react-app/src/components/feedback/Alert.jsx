import React from 'react'

export const Alert = ({ type = 'info', message, onClose, className = '' }) => {
  if (!message) return null

  return (
    <div className={`alert alert--${type} ${className}`}>
      <span>{message}</span>
      {onClose && (
        <button onClick={onClose} className="alert__close">✕</button>
      )}
    </div>
  )
}

export default Alert
