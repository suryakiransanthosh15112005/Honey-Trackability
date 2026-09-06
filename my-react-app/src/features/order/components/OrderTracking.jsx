import React from 'react'

const STAGES = [
  { key: 'CONFIRMED', label: 'Confirmed', icon: '📝' },
  { key: 'PACKED', label: 'Packed', icon: '📦' },
  { key: 'SHIPPED', label: 'Shipped', icon: '🚚' },
  { key: 'DELIVERED', label: 'Delivered', icon: '🏠' },
]

const OrderTracking = ({ currentStatus }) => {
  if (currentStatus === 'CANCELLED') {
    return (
      <div className="order-tracker order-tracker--cancelled">
        <div className="order-tracker__cancelled-banner">
          <span className="order-tracker__cancelled-icon">❌</span>
          <div>
            <strong>Order Cancelled</strong>
            <p className="text-xs text-secondary">This order was cancelled and inventory was restored.</p>
          </div>
        </div>
      </div>
    )
  }

  const currentIdx = STAGES.findIndex((s) => s.key === currentStatus)

  return (
    <div className="order-tracker">
      <div className="order-tracker__steps">
        {STAGES.map((stage, idx) => {
          const isCompleted = currentIdx >= idx
          const isCurrent = currentIdx === idx

          return (
            <React.Fragment key={stage.key}>
              <div
                className={`order-tracker__step ${isCompleted ? 'order-tracker__step--completed' : ''} ${isCurrent ? 'order-tracker__step--current' : ''
                  }`}
              >
                <div className="order-tracker__node">
                  <span className="order-tracker__icon">{stage.icon}</span>
                  {isCompleted && !isCurrent && <span className="order-tracker__check">✓</span>}
                </div>
                <span className="order-tracker__label">{stage.label}</span>
              </div>
              {idx < STAGES.length - 1 && (
                <div
                  className={`order-tracker__line ${currentIdx > idx ? 'order-tracker__line--completed' : ''
                    }`}
                />
              )}
            </React.Fragment>
          )
        })}
      </div>
    </div>
  )
}

export default OrderTracking
