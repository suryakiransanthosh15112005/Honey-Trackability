import React, { useState, useEffect } from 'react'
import { useParams, Link } from 'react-router-dom'
import CustomerLayout from '../../../layouts/CustomerLayout'
import orderApi from '../api/orderApi'
import OrderTracking from '../components/OrderTracking'
import Alert from '../../../components/feedback/Alert'
import ReviewForm from '../../review/components/ReviewForm'
import ReviewCard from '../../review/components/ReviewCard'
import RatingStars from '../../review/components/RatingStars'
import reviewApi from '../../review/api/reviewApi'

// --- Per-item review panel shown on DELIVERED orders ---
const OrderItemReviewPanel = ({ item }) => {
  const [existingReview, setExistingReview] = useState(undefined) // undefined = loading
  const [showForm, setShowForm] = useState(false)
  const [loading, setLoading] = useState(true)
  const [deleteError, setDeleteError] = useState(null)

  const loadReview = async () => {
    setLoading(true)
    try {
      const res = await reviewApi.getReviewByOrderItem(item.id)
      setExistingReview(res.data?.data || null)
    } catch {
      setExistingReview(null)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadReview()
  }, [item.id])

  const handleSuccess = (review) => {
    setExistingReview(review)
    setShowForm(false)
  }

  const handleDelete = async () => {
    if (!window.confirm('Delete your review for this item?')) return
    setDeleteError(null)
    try {
      await reviewApi.deleteReview(existingReview.id)
      setExistingReview(null)
    } catch (err) {
      setDeleteError(err?.response?.data?.message || 'Failed to delete review')
    }
  }

  if (loading) return <div className="skeleton h-12 mt-3" />

  return (
    <div className="order-item-review mt-3">
      {deleteError && (
        <div className="alert alert--danger mb-2">
          <div className="alert__body"><p className="alert__message">{deleteError}</p></div>
        </div>
      )}

      {existingReview ? (
        <div className="order-item-review__existing">
          <div className="flex items-center gap-2 mb-2">
            <RatingStars value={existingReview.rating} size="sm" />
            <span className="text-secondary text-xs">Your review</span>
          </div>
          {existingReview.comment && (
            <p className="text-secondary text-sm mb-2">
              "{existingReview.comment}"
            </p>
          )}
          {showForm ? (
            <ReviewForm
              existingReview={existingReview}
              productName={item.productName}
              onSuccess={handleSuccess}
              onCancel={() => setShowForm(false)}
            />
          ) : (
            <div className="flex gap-2">
              <button
                type="button"
                className="btn btn--outline btn--xs"
                onClick={() => setShowForm(true)}
              >
                ✏️ Edit Review
              </button>
              <button
                type="button"
                className="btn btn--danger-outline btn--xs"
                onClick={handleDelete}
              >
                🗑 Delete
              </button>
            </div>
          )}
        </div>
      ) : showForm ? (
        <ReviewForm
          orderItemId={item.id}
          productName={item.productName}
          onSuccess={handleSuccess}
          onCancel={() => setShowForm(false)}
        />
      ) : (
        <button
          type="button"
          className="btn btn--outline btn--sm mt-1"
          onClick={() => setShowForm(true)}
        >
          ⭐ Rate This Honey
        </button>
      )}
    </div>
  )
}

const OrderDetailsPage = () => {
  const { orderNumber } = useParams()
  const [order, setOrder] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [cancelling, setCancelling] = useState(false)

  const loadOrder = async () => {
    setLoading(true)
    setError(null)
    try {
      const res = await orderApi.getMyOrderByNumber(orderNumber)
      setOrder(res.data?.data)
    } catch (err) {
      setError(err?.response?.data?.message || 'Failed to load order details')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadOrder()
  }, [orderNumber])

  const handleCancel = async () => {
    if (!window.confirm('Are you sure you want to cancel this order? Stock will be restored.')) return

    setCancelling(true)
    try {
      await orderApi.cancelOrder(orderNumber)
      await loadOrder()
    } catch (err) {
      alert(err?.response?.data?.message || 'Failed to cancel order')
    } finally {
      setCancelling(false)
    }
  }

  if (loading) {
    return (
      <CustomerLayout>
        <div className="container section text-center">Loading order details...</div>
      </CustomerLayout>
    )
  }

  if (error || !order) {
    return (
      <CustomerLayout>
        <div className="container section">
          <Alert type="danger" message={error || 'Order not found'} />
          <Link to="/orders" className="btn btn--secondary mt-4">
            ← Back to My Orders
          </Link>
        </div>
      </CustomerLayout>
    )
  }

  const {
    totalAmount,
    paymentStatus,
    paymentId,
    orderStatus,
    fulfillmentType,
    deliveryAddress,
    items,
    createdAt,
  } = order

  const isConfirmed = orderStatus === 'CONFIRMED'
  const isDelivered = orderStatus === 'DELIVERED'

  return (
    <CustomerLayout>
      <div className="order-details-page section">
        <div className="container max-w-4xl mx-auto">
          <nav className="breadcrumb mb-6">
            <Link to="/">Home</Link> / <Link to="/orders">My Orders</Link> /{' '}
            <span className="text-secondary">{orderNumber}</span>
          </nav>

          <div className="page-header mb-6">
            <div>
              <h1 className="page-header__title">Order Details</h1>
              <p className="page-header__subtitle">
                Reference: <code>{orderNumber}</code> • Placed on {new Date(createdAt).toLocaleString('en-IN')}
              </p>
            </div>
            {isConfirmed && (
              <button
                type="button"
                className="btn btn--ghost text-danger"
                disabled={cancelling}
                onClick={handleCancel}
              >
                {cancelling ? 'Cancelling...' : 'Cancel Order'}
              </button>
            )}
          </div>

          {/* Tracking Stepper Card */}
          <div className="card mb-6">
            <h3 className="card__title mb-4">Fulfillment Status</h3>
            <OrderTracking currentStatus={orderStatus} />
          </div>

          {/* Order Items Table */}
          <div className="card mb-6">
            <h3 className="card__title mb-4">Ordered Honey Items</h3>
            <div className="overflow-x-auto">
              <table className="data-table">
                <thead>
                  <tr>
                    <th>Item Description</th>
                    <th>Unit Price</th>
                    <th>Quantity</th>
                    <th>Subtotal</th>
                  </tr>
                </thead>
                <tbody>
                  {items?.map((item) => (
                    <tr key={item.id}>
                      <td>
                        <strong>{item.productName}</strong>
                        {/* Show review panel for DELIVERED orders */}
                        {isDelivered && <OrderItemReviewPanel item={item} />}
                      </td>
                      <td>₹{Number(item.unitPrice).toFixed(2)} / kg</td>
                      <td>{Number(item.quantityKg).toFixed(1)} kg</td>
                      <td>
                        <strong>₹{Number(item.subtotal).toFixed(2)}</strong>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>

            <div className="order-totals-summary mt-4 pt-4 border-t">
              <div className="flex justify-between py-1">
                <span className="text-secondary">Subtotal:</span>
                <span>₹{Number(totalAmount).toFixed(2)}</span>
              </div>
              <div className="flex justify-between py-1">
                <span className="text-secondary">Delivery:</span>
                <span className="text-success font-semibold">FREE</span>
              </div>
              <div className="flex justify-between py-2 text-lg font-bold border-t mt-2">
                <span>Total Amount:</span>
                <span className="text-gold">₹{Number(totalAmount).toFixed(2)}</span>
              </div>
            </div>
          </div>

          {/* Delivery & Payment Info */}
          <div className="grid grid-cols-2 gap-6">
            {/* Delivery Details */}
            <div className="card">
              <h3 className="card__title mb-3">
                {fulfillmentType === 'LOCAL_PICKUP' ? '🏪 Pickup Details' : '🚚 Shipping Address'}
              </h3>
              {fulfillmentType === 'LOCAL_PICKUP' ? (
                <p className="text-secondary text-sm">
                  Local Apiary Pickup arranged with beekeeper cluster.
                </p>
              ) : deliveryAddress ? (
                <div className="text-secondary text-sm">
                  <p><strong>{deliveryAddress.name}</strong></p>
                  <p>{deliveryAddress.line1}</p>
                  {deliveryAddress.line2 && <p>{deliveryAddress.line2}</p>}
                  <p>{deliveryAddress.city}, {deliveryAddress.state} - {deliveryAddress.postalCode}</p>
                </div>
              ) : (
                <p className="text-muted text-sm">No address provided</p>
              )}
            </div>

            {/* Payment Details */}
            <div className="card">
              <h3 className="card__title mb-3">💳 Payment Information</h3>
              <div className="text-sm">
                <p className="flex justify-between py-1">
                  <span className="text-secondary">Status:</span>
                  <span className="badge badge--success">{paymentStatus}</span>
                </p>
                <p className="flex justify-between py-1">
                  <span className="text-secondary">Payment Ref:</span>
                  <code>{paymentId || 'N/A'}</code>
                </p>
                <p className="flex justify-between py-1">
                  <span className="text-secondary">Method:</span>
                  <span>Sandbox UPI Gateway</span>
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </CustomerLayout>
  )
}

export default OrderDetailsPage
