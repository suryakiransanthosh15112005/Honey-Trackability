import React from 'react'
import { Link } from 'react-router-dom'
import CustomerLayout from '../../../layouts/CustomerLayout'
import useOrders from '../hooks/useOrders'
import OrderCard from '../components/OrderCard'
import Alert from '../../../components/feedback/Alert'

const OrdersPage = () => {
  const { orders, loading, error, cancellingId, cancelOrder } = useOrders()

  return (
    <CustomerLayout>
      <div className="orders-page section">
        <div className="container max-w-4xl mx-auto">
          <div className="page-header mb-6">
            <div>
              <h1 className="page-header__title">📦 My Honey Orders</h1>
              <p className="page-header__subtitle">
                Track status and view history of your artisan honey purchases.
              </p>
            </div>
            <Link to="/marketplace" className="btn btn--secondary btn--sm">
              🍯 Browse Marketplace
            </Link>
          </div>

          {error && <Alert type="danger" message={error} className="mb-6" />}

          {loading ? (
            <div className="card p-8 text-center">Loading your order history...</div>
          ) : orders.length === 0 ? (
            <div className="card empty-state p-10 text-center">
              <div className="empty-state__icon">📦</div>
              <h2 className="empty-state__title">No Orders Placed Yet</h2>
              <p className="empty-state__description">
                You haven't ordered any verified honey yet. Explore our verified marketplace to place your first order.
              </p>
              <div className="mt-6">
                <Link to="/marketplace" className="btn btn--primary">
                  Explore Verified Honey
                </Link>
              </div>
            </div>
          ) : (
            <div className="orders-list">
              {orders.map((order) => (
                <OrderCard
                  key={order.orderNumber}
                  order={order}
                  onCancel={cancelOrder}
                  isCancelling={cancellingId === order.orderNumber}
                />
              ))}
            </div>
          )}
        </div>
      </div>
    </CustomerLayout>
  )
}

export default OrdersPage
