import React, { useState, useEffect, useCallback } from 'react'
import { Link } from 'react-router-dom'
import BeekeeperLayout from '../../../layouts/BeekeeperLayout'
import PageHeader from '../../../components/layout/PageHeader'
import orderApi from '../api/orderApi'
import OrderTracking from '../components/OrderTracking'
import Alert from '../../../components/feedback/Alert'
import Button from '../../../components/ui/Button'

const BeekeeperOrdersPage = () => {
  const [orders, setOrders] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [updatingNum, setUpdatingNum] = useState(null)

  const loadOrders = useCallback(async () => {
    setLoading(true)
    setError(null)
    try {
      const res = await orderApi.getBeekeeperOrders({ page: 0, size: 50 })
      const data = res.data?.data
      if (data?.content) {
        setOrders(data.content)
      } else if (Array.isArray(data)) {
        setOrders(data)
      }
    } catch (err) {
      setError(err?.response?.data?.message || 'Failed to load customer orders')
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    loadOrders()
  }, [loadOrders])

  const handleStatusUpdate = async (orderNumber, nextStatus) => {
    setUpdatingNum(orderNumber)
    try {
      await orderApi.updateOrderStatus(orderNumber, nextStatus)
      await loadOrders()
    } catch (err) {
      alert(err?.response?.data?.message || 'Failed to update order status')
    } finally {
      setUpdatingNum(null)
    }
  }

  return (
    <BeekeeperLayout>
      <div className="space-y-6">
        <PageHeader
          title="📦 Customer Orders & Fulfillment"
          subtitle="Manage incoming orders for your honey products and progress their fulfillment stages."
          actions={
            <Link to="/beekeeper/products">
              <Button variant="secondary" size="sm">🍯 Manage Listings</Button>
            </Link>
          }
        />

        {error && <Alert type="danger" message={error} className="mb-6" />}

          {loading ? (
            <div className="card p-8 text-center">Loading incoming orders...</div>
          ) : orders.length === 0 ? (
            <div className="card empty-state p-10 text-center">
              <div className="empty-state__icon">📦</div>
              <h2 className="empty-state__title">No Orders Received Yet</h2>
              <p className="empty-state__description">
                When customers purchase your listed honey batches from the marketplace, their orders will appear here for packing and dispatch.
              </p>
            </div>
          ) : (
            <div className="orders-list">
              {orders.map((order) => {
                const {
                  orderNumber,
                  createdAt,
                  totalAmount,
                  orderStatus,
                  fulfillmentType,
                  deliveryAddress,
                  items,
                } = order

                const isUpdating = updatingNum === orderNumber

                return (
                  <div key={orderNumber} className="card order-card mb-6">
                    <div className="order-card__header">
                      <div>
                        <span className="text-secondary text-xs">ORDER NUMBER</span>
                        <p className="order-card__number">
                          <code>{orderNumber}</code>
                        </p>
                      </div>

                      <div>
                        <span className="text-secondary text-xs">RECEIVED ON</span>
                        <p className="order-card__date">
                          {createdAt ? new Date(createdAt).toLocaleString('en-IN') : 'Recently'}
                        </p>
                      </div>

                      <div>
                        <span className="text-secondary text-xs">TOTAL VALUE</span>
                        <p className="order-card__total">₹{Number(totalAmount).toFixed(2)}</p>
                      </div>

                      <div>
                        <span className="badge badge--dark">
                          {fulfillmentType === 'LOCAL_PICKUP' ? '🏪 Pickup' : '🚚 Delivery'}
                        </span>
                      </div>
                    </div>

                    <div className="order-card__tracker mt-4">
                      <OrderTracking currentStatus={orderStatus} />
                    </div>

                    {/* Items */}
                    <div className="order-card__items mt-4">
                      <h4 className="text-sm font-semibold mb-2">Ordered Items:</h4>
                      {items?.map((item) => (
                        <div key={item.id} className="order-card__item-row">
                          <span>
                            <strong>{item.productName}</strong> — {Number(item.quantityKg).toFixed(1)} kg
                          </span>
                          <span>₹{Number(item.subtotal).toFixed(2)}</span>
                        </div>
                      ))}
                    </div>

                    {/* Recipient Shipping Address (if Delivery) */}
                    {fulfillmentType === 'DELIVERY' && deliveryAddress && (
                      <div className="order-card__shipping mt-4 p-3 bg-dark rounded text-sm">
                        <span className="text-secondary text-xs block mb-1">DISPATCH ADDRESS:</span>
                        <strong>{deliveryAddress.name}</strong> • {deliveryAddress.line1}, {deliveryAddress.city}, {deliveryAddress.state} ({deliveryAddress.postalCode})
                      </div>
                    )}

                    {/* Action Buttons for Valid Transitions */}
                    <div className="order-card__actions mt-6 flex justify-end gap-3 border-t pt-4">
                      {orderStatus === 'CONFIRMED' && (
                        <button
                          type="button"
                          className="btn btn--primary btn--sm"
                          disabled={isUpdating}
                          onClick={() => handleStatusUpdate(orderNumber, 'PACKED')}
                        >
                          {isUpdating ? 'Updating...' : '📦 Mark as Packed'}
                        </button>
                      )}

                      {orderStatus === 'PACKED' && (
                        <button
                          type="button"
                          className="btn btn--primary btn--sm"
                          disabled={isUpdating}
                          onClick={() => handleStatusUpdate(orderNumber, 'SHIPPED')}
                        >
                          {isUpdating ? 'Updating...' : '🚚 Mark as Shipped'}
                        </button>
                      )}

                      {orderStatus === 'SHIPPED' && (
                        <button
                          type="button"
                          className="btn btn--success btn--sm"
                          disabled={isUpdating}
                          onClick={() => handleStatusUpdate(orderNumber, 'DELIVERED')}
                        >
                          {isUpdating ? 'Updating...' : '🏠 Mark as Delivered'}
                        </button>
                      )}

                      {orderStatus === 'DELIVERED' && (
                        <span className="badge badge--success">✅ Order Delivered & Completed</span>
                      )}

                      {orderStatus === 'CANCELLED' && (
                        <span className="badge badge--danger">❌ Order Cancelled</span>
                      )}
                    </div>
                  </div>
                )
              })}
            </div>
          )}
      </div>
    </BeekeeperLayout>
  )
}

export default BeekeeperOrdersPage
