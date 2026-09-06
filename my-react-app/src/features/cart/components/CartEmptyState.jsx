import React from 'react'
import { Link } from 'react-router-dom'

const CartEmptyState = () => {
  return (
    <div className="card empty-state p-10 text-center">
      <div className="empty-state__icon">🛒</div>
      <h2 className="empty-state__title">Your Cart is Empty</h2>
      <p className="empty-state__description">
        You haven't added any authentic artisan honey to your cart yet.
        Explore our marketplace to find 100% lab-tested, blockchain-verified honey.
      </p>
      <div className="mt-6">
        <Link to="/marketplace" className="btn btn--primary btn--lg">
          🍯 Explore Marketplace
        </Link>
      </div>
    </div>
  )
}

export default CartEmptyState
