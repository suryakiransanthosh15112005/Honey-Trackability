import React from 'react'
import { Link } from 'react-router-dom'
import CustomerLayout from '../../../layouts/CustomerLayout'
import useCart from '../hooks/useCart'
import CartItem from '../components/CartItem'
import CartSummary from '../components/CartSummary'
import CartEmptyState from '../components/CartEmptyState'
import Alert from '../../../components/feedback/Alert'

const CartPage = () => {
  const {
    items,
    subtotal,
    itemCount,
    loading,
    error,
    updateQuantity,
    removeItem,
    emptyCart,
  } = useCart()

  const isEmpty = !items || items.length === 0

  return (
    <CustomerLayout>
      <div className="cart-page section">
        <div className="container">
          <nav className="breadcrumb mb-6">
            <Link to="/">Home</Link> / <Link to="/marketplace">Marketplace</Link> /{' '}
            <span className="text-secondary">Shopping Cart</span>
          </nav>

          <div className="cart-page__header mb-6">
            <h1 className="page-header__title">🛒 Your Shopping Cart</h1>
            {!isEmpty && (
              <span className="text-secondary">
                {itemCount} {itemCount === 1 ? 'item' : 'items'} in your cart
              </span>
            )}
          </div>

          {error && <Alert type="danger" message={error} className="mb-6" />}

          {loading && isEmpty ? (
            <div className="card p-8 text-center">Loading cart...</div>
          ) : isEmpty ? (
            <CartEmptyState />
          ) : (
            <div className="cart-layout">
              {/* Items List */}
              <div className="cart-layout__items">
                <div className="card cart-items-card">
                  <div className="cart-items-card__header">
                    <span>Product</span>
                    <span>Quantity</span>
                    <span>Subtotal</span>
                    <span></span>
                  </div>

                  <div className="cart-items-card__list">
                    {items.map((item) => (
                      <CartItem
                        key={item.id}
                        item={item}
                        onUpdateQuantity={updateQuantity}
                        onRemove={removeItem}
                        disabled={loading}
                      />
                    ))}
                  </div>
                </div>

                <div className="mt-4">
                  <Link to="/marketplace" className="btn btn--ghost btn--sm">
                    ← Continue Shopping
                  </Link>
                </div>
              </div>

              {/* Cart Summary */}
              <div className="cart-layout__summary">
                <CartSummary
                  subtotal={subtotal}
                  itemCount={itemCount}
                  onClear={emptyCart}
                  disabled={loading}
                />
              </div>
            </div>
          )}
        </div>
      </div>
    </CustomerLayout>
  )
}

export default CartPage
