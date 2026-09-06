import React from 'react'
import { Link } from 'react-router-dom'

const CartSummary = ({ subtotal, itemCount, onClear, disabled }) => {
  const deliveryEstimate = 0.0 // Free delivery for prototype
  const total = Number(subtotal) + deliveryEstimate

  return (
    <div className="card cart-summary">
      <h3 className="cart-summary__title">Order Summary</h3>

      <div className="cart-summary__rows mt-4">
        <div className="cart-summary__row">
          <span>Items ({itemCount}):</span>
          <strong>₹{Number(subtotal).toFixed(2)}</strong>
        </div>

        <div className="cart-summary__row">
          <span>Standard Delivery:</span>
          <span className="text-success">FREE</span>
        </div>

        <div className="cart-summary__divider"></div>

        <div className="cart-summary__row cart-summary__row--total">
          <span>Total Amount:</span>
          <span className="cart-summary__total-val">₹{total.toFixed(2)}</span>
        </div>
      </div>

      <div className="cart-summary__actions mt-6">
        <Link
          to="/checkout"
          className="btn btn--primary btn--full btn--lg"
        >
          Proceed to Checkout →
        </Link>

        <button
          type="button"
          className="btn btn--ghost btn--full btn--sm mt-2 text-secondary"
          onClick={onClear}
          disabled={disabled}
        >
          Clear Cart
        </button>
      </div>

      <div className="cart-summary__guarantee mt-6">
        <p className="text-xs text-secondary text-center">
          🔒 100% Secure Checkout • Direct Beekeeper Fair Pricing • Blockchain Purity Guarantee
        </p>
      </div>
    </div>
  )
}

export default CartSummary
