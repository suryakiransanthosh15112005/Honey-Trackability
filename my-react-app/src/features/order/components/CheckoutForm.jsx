import React, { useState } from 'react'

const CheckoutForm = ({ totalAmount, onSubmit, loading, error, initialAddress = null }) => {
  const [fulfillmentType, setFulfillmentType] = useState('DELIVERY')
  const [address, setAddress] = useState({
    name: initialAddress?.name || '',
    line1: initialAddress?.line1 || '',
    line2: initialAddress?.line2 || '',
    city: initialAddress?.city || '',
    state: initialAddress?.state || '',
    postalCode: initialAddress?.postalCode || '',
  })
  const [paymentMode, setPaymentMode] = useState('mock')

  const handleAddressChange = (e) => {
    const { name, value } = e.target
    setAddress((prev) => ({ ...prev, [name]: value }))
  }

  const handleSubmit = (e) => {
    e.preventDefault()
    onSubmit({
      fulfillmentType,
      deliveryAddress: fulfillmentType === 'DELIVERY' ? address : null,
      paymentMode,
    })
  }

  return (
    <form onSubmit={handleSubmit} className="checkout-form">
      {/* Fulfillment Type */}
      <div className="card mb-6">
        <h3 className="card__title">1. Fulfillment Method</h3>
        <p className="card__subtitle mb-4">Choose how you wish to receive your authentic honey.</p>

        <div className="fulfillment-options">
          <label className={`fulfillment-option ${fulfillmentType === 'DELIVERY' ? 'active' : ''}`}>
            <input
              type="radio"
              name="fulfillmentType"
              value="DELIVERY"
              checked={fulfillmentType === 'DELIVERY'}
              onChange={() => setFulfillmentType('DELIVERY')}
            />
            <div className="fulfillment-option__content">
              <strong>🚚 Standard Doorstep Delivery</strong>
              <p className="text-secondary text-xs">Direct from beekeeper cluster to your home address</p>
            </div>
          </label>

          <label className={`fulfillment-option ${fulfillmentType === 'LOCAL_PICKUP' ? 'active' : ''}`}>
            <input
              type="radio"
              name="fulfillmentType"
              value="LOCAL_PICKUP"
              checked={fulfillmentType === 'LOCAL_PICKUP'}
              onChange={() => setFulfillmentType('LOCAL_PICKUP')}
            />
            <div className="fulfillment-option__content">
              <strong>🏪 Local Apiary Pickup</strong>
              <p className="text-secondary text-xs">Pick up directly from the beekeeper's registered farm/cluster</p>
            </div>
          </label>
        </div>
      </div>

      {/* Delivery Address (only for DELIVERY) */}
      {fulfillmentType === 'DELIVERY' && (
        <div className="card mb-6">
          <h3 className="card__title">2. Delivery Address</h3>
          <p className="card__subtitle mb-4">Provide recipient details for shipment dispatch.</p>

          <div className="form-grid">
            <div className="form-group form-group--full">
              <label className="form-label" htmlFor="addr-name">
                Recipient Full Name <span className="text-danger">*</span>
              </label>
              <input
                id="addr-name"
                name="name"
                type="text"
                className="form-input"
                placeholder="e.g. Priyan Sharma"
                value={address.name}
                onChange={handleAddressChange}
                required
              />
            </div>

            <div className="form-group form-group--full">
              <label className="form-label" htmlFor="addr-line1">
                Street Address / Line 1 <span className="text-danger">*</span>
              </label>
              <input
                id="addr-line1"
                name="line1"
                type="text"
                className="form-input"
                placeholder="House / Flat No., Street Name"
                value={address.line1}
                onChange={handleAddressChange}
                required
              />
            </div>

            <div className="form-group form-group--full">
              <label className="form-label" htmlFor="addr-line2">
                Apartment / Landmark / Line 2
              </label>
              <input
                id="addr-line2"
                name="line2"
                type="text"
                className="form-input"
                placeholder="Near landmark, locality"
                value={address.line2}
                onChange={handleAddressChange}
              />
            </div>

            <div className="form-group">
              <label className="form-label" htmlFor="addr-city">
                City <span className="text-danger">*</span>
              </label>
              <input
                id="addr-city"
                name="city"
                type="text"
                className="form-input"
                placeholder="City"
                value={address.city}
                onChange={handleAddressChange}
                required
              />
            </div>

            <div className="form-group">
              <label className="form-label" htmlFor="addr-state">
                State <span className="text-danger">*</span>
              </label>
              <input
                id="addr-state"
                name="state"
                type="text"
                className="form-input"
                placeholder="State"
                value={address.state}
                onChange={handleAddressChange}
                required
              />
            </div>

            <div className="form-group form-group--full">
              <label className="form-label" htmlFor="addr-postalCode">
                Postal PIN Code <span className="text-danger">*</span>
              </label>
              <input
                id="addr-postalCode"
                name="postalCode"
                type="text"
                className="form-input"
                placeholder="6-digit PIN code"
                value={address.postalCode}
                onChange={handleAddressChange}
                required
              />
            </div>
          </div>
        </div>
      )}

      {/* Payment Section */}
      <div className="card mb-6">
        <h3 className="card__title">3. Payment Method</h3>
        <p className="card__subtitle mb-4">Secure payment processing abstraction.</p>

        <div className="alert alert--info mb-4">
          <span className="alert__icon">ℹ️</span>
          <div className="alert__body">
            <h4 className="alert__title">Sandbox Payment Gateway</h4>
            <p className="alert__message">
              This is a secure sandbox payment gateway environment. Transactions are simulated for development and evaluation.
            </p>
          </div>
        </div>

        <div className="payment-options">
          <label className={`payment-option ${paymentMode === 'mock' ? 'active' : ''}`}>
            <input
              type="radio"
              name="paymentMode"
              value="mock"
              checked={paymentMode === 'mock'}
              onChange={() => setPaymentMode('mock')}
            />
            <div className="payment-option__content">
              <strong>💳 Simulated UPI / Card Payment (Success)</strong>
              <p className="text-secondary text-xs">Simulates a successful 200 OK payment transaction</p>
            </div>
          </label>

          <label className={`payment-option ${paymentMode === 'fail' ? 'active' : ''}`}>
            <input
              type="radio"
              name="paymentMode"
              value="fail"
              checked={paymentMode === 'fail'}
              onChange={() => setPaymentMode('fail')}
            />
            <div className="payment-option__content">
              <strong>⚠️ Test Payment Decline (Failure Simulation)</strong>
              <p className="text-secondary text-xs">Simulates bank decline — tests transactional stock rollback</p>
            </div>
          </label>
        </div>
      </div>

      {/* Submit Button */}
      <button
        type="submit"
        className="btn btn--primary btn--full btn--lg"
        disabled={loading}
      >
        {loading ? 'Processing Transaction...' : `Confirm & Pay ₹${Number(totalAmount).toFixed(2)}`}
      </button>
    </form>
  )
}

export default CheckoutForm
