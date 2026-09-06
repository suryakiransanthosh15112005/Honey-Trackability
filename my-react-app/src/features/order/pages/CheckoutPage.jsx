import React, { useState, useEffect } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useDispatch } from 'react-redux'
import CustomerLayout from '../../../layouts/CustomerLayout'
import useCart from '../../cart/hooks/useCart'
import { resetCartState } from '../../cart/cartSlice'
import orderApi from '../api/orderApi'
import customerApi from '../../customer/api/customerApi'
import CheckoutForm from '../components/CheckoutForm'
import Alert from '../../../components/feedback/Alert'

const CheckoutPage = () => {
  const navigate = useNavigate()
  const dispatch = useDispatch()
  const { items, subtotal, itemCount } = useCart()

  const [placingOrder, setPlacingOrder] = useState(false)
  const [orderError, setOrderError] = useState(null)
  const [confirmedOrder, setConfirmedOrder] = useState(null)
  const [initialAddress, setInitialAddress] = useState(null)

  useEffect(() => {
    customerApi.getProfile()
      .then((res) => {
        const p = res.data?.data
        if (p && p.fullName) {
          setInitialAddress({
            name: p.fullName || '',
            line1: p.address || '',
            line2: '',
            city: p.city || '',
            state: p.state || '',
            postalCode: p.pincode || '',
          })
        }
      })
      .catch(() => {})
  }, [])

  const handleCheckout = async (checkoutData) => {
    setPlacingOrder(true)
    setOrderError(null)

    try {
      const res = await orderApi.checkout(checkoutData)
      const order = res.data?.data
      setConfirmedOrder(order)
      dispatch(resetCartState())
    } catch (err) {
      setOrderError(err?.response?.data?.message || 'Checkout could not be completed')
    } finally {
      setPlacingOrder(false)
    }
  }

  // Success Confirmation Screen
  if (confirmedOrder) {
    return (
      <CustomerLayout>
        <div className="container section max-w-2xl mx-auto">
          <div className="card text-center p-8">
            <div className="empty-state__icon text-success">🎉</div>
            <span className="badge badge--success badge--lg mb-3">✅ Payment & Order Confirmed</span>
            <h1 className="card__title mt-2">Thank you for supporting Indian Beekeepers!</h1>
            <p className="card__subtitle mt-2">
              Your order <code>{confirmedOrder.orderNumber}</code> has been confirmed and dispatched to the beekeeper for packing.
            </p>

            <div className="order-summary-box mt-6 p-4 bg-dark rounded">
              <div className="flex justify-between py-1">
                <span className="text-secondary">Order Reference:</span>
                <strong>{confirmedOrder.orderNumber}</strong>
              </div>
              <div className="flex justify-between py-1">
                <span className="text-secondary">Payment Status:</span>
                <span className="text-success font-semibold">✅ SUCCESS</span>
              </div>
              <div className="flex justify-between py-1">
                <span className="text-secondary">Total Amount Paid:</span>
                <strong className="text-gold">₹{Number(confirmedOrder.totalAmount).toFixed(2)}</strong>
              </div>
              <div className="flex justify-between py-1">
                <span className="text-secondary">Fulfillment:</span>
                <span>{confirmedOrder.fulfillmentType === 'LOCAL_PICKUP' ? 'Local Apiary Pickup' : 'Standard Doorstep Delivery'}</span>
              </div>
            </div>

            <div className="flex justify-center gap-4 mt-8">
              <Link to={`/orders/${confirmedOrder.orderNumber}`} className="btn btn--primary">
                📦 Track Order Progress
              </Link>
              <Link to="/marketplace" className="btn btn--secondary">
                🍯 Continue Shopping
              </Link>
            </div>
          </div>
        </div>
      </CustomerLayout>
    )
  }

  // If cart is empty and no confirmed order, redirect to cart
  if (!items || items.length === 0) {
    return (
      <CustomerLayout>
        <div className="container section max-w-xl mx-auto">
          <div className="card text-center p-8">
            <h2>Your cart is empty</h2>
            <p className="text-secondary mt-2">Please add honey products to your cart before proceeding to checkout.</p>
            <div className="mt-4">
              <Link to="/marketplace" className="btn btn--primary">
                Explore Marketplace
              </Link>
            </div>
          </div>
        </div>
      </CustomerLayout>
    )
  }

  return (
    <CustomerLayout>
      <div className="checkout-page section">
        <div className="container">
          <nav className="breadcrumb mb-6">
            <Link to="/">Home</Link> / <Link to="/marketplace">Marketplace</Link> /{' '}
            <Link to="/cart">Cart</Link> / <span className="text-secondary">Checkout</span>
          </nav>

          <h1 className="page-header__title mb-6">🔒 Secure Order Checkout</h1>

          {orderError && <Alert type="danger" message={orderError} className="mb-6" />}

          <div className="checkout-layout">
            {/* Left: Checkout Form */}
            <div className="checkout-layout__form">
              <CheckoutForm
                totalAmount={subtotal}
                onSubmit={handleCheckout}
                loading={placingOrder}
                error={orderError}
                initialAddress={initialAddress}
              />
            </div>

            {/* Right: Items Overview */}
            <div className="checkout-layout__summary">
              <div className="card checkout-items-card">
                <h3 className="card__title mb-4">Cart Summary ({itemCount} items)</h3>
                <div className="checkout-items-list">
                  {items.map((item) => (
                    <div key={item.id} className="checkout-item-row">
                      <div>
                        <strong>{item.productName}</strong>
                        <span className="text-secondary text-xs block">
                          {Number(item.quantityKg).toFixed(1)} kg × ₹{Number(item.unitPrice).toFixed(2)}
                        </span>
                      </div>
                      <strong>₹{Number(item.subtotal).toFixed(2)}</strong>
                    </div>
                  ))}
                </div>

                <div className="cart-summary__divider mt-4"></div>

                <div className="flex justify-between mt-4">
                  <span className="text-secondary">Delivery:</span>
                  <span className="text-success font-semibold">FREE</span>
                </div>

                <div className="flex justify-between mt-2">
                  <strong className="text-lg">Total Amount:</strong>
                  <strong className="text-lg text-gold">₹{Number(subtotal).toFixed(2)}</strong>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </CustomerLayout>
  )
}

export default CheckoutPage
