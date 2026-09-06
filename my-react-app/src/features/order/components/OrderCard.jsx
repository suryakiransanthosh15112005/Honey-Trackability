import React from 'react'
import { Link } from 'react-router-dom'
import OrderTracking from './OrderTracking'

const OrderCard = ({ order, onCancel, isCancelling }) => {
  if (!order) return null

  const {
    orderNumber,
    createdAt,
    totalAmount,
    paymentStatus,
    orderStatus,
    fulfillmentType,
    items,
  } = order

  const isConfirmed = orderStatus === 'CONFIRMED'

  return (
    <div className="card order-card">
      <div className="order-card__header">
        <div>
          <span className="text-secondary text-xs">ORDER PLACED</span>
          <p className="order-card__date">
            {createdAt ? new Date(createdAt).toLocaleDateString('en-IN', { dateStyle: 'medium' }) : 'Recently'}
          </p>
        </div>

        <div>
          <span className="text-secondary text-xs">TOTAL</span>
          <p className="order-card__total">₹{Number(totalAmount).toFixed(2)}</p>
        </div>

        <div>
          <span className="text-secondary text-xs">ORDER #</span>
          <p className="order-card__number">
            <code>{orderNumber}</code>
          </p>
        </div>

        <div className="order-card__status-badges">
          <span
            className={`badge ${
              paymentStatus === 'SUCCESS' ? 'badge--success' : 'badge--warning'
            }`}
          >
            {paymentStatus === 'SUCCESS' ? '✅ Paid' : paymentStatus}
          </span>
          <span className="badge badge--dark">
            {fulfillmentType === 'LOCAL_PICKUP' ? '🏪 Pickup' : '🚚 Delivery'}
          </span>
        </div>
      </div>

      <div className="order-card__tracker mt-4">
        <OrderTracking currentStatus={orderStatus} />
      </div>

      <div className="order-card__items mt-4">
        {items?.map((item) => (
          <div key={item.id} className="order-card__item-row">
            <div>
              <strong>{item.productName}</strong>
              <span className="text-secondary text-xs ml-2">
                ({Number(item.quantityKg).toFixed(1)} kg × ₹{Number(item.unitPrice).toFixed(2)})
              </span>
            </div>
            <strong>₹{Number(item.subtotal).toFixed(2)}</strong>
          </div>
        ))}
      </div>

      <div className="order-card__footer mt-4">
        <Link
          to={`/orders/${orderNumber}`}
          className="btn btn--outline btn--sm"
        >
          View Full Details
        </Link>

        {isConfirmed && onCancel && (
          <button
            type="button"
            className="btn btn--ghost btn--sm text-danger"
            disabled={isCancelling}
            onClick={() => onCancel(orderNumber)}
          >
            Cancel Order
          </button>
        )}
      </div>
    </div>
  )
}

export default OrderCard
