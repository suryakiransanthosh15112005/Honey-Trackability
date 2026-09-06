import React, { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import CustomerLayout from '../../../layouts/CustomerLayout'
import Card from '../../../components/ui/Card'
import PageHeader from '../../../components/layout/PageHeader'
import MetricCard from '../../../components/ui/MetricCard'
import Button from '../../../components/ui/Button'
import { useAuth } from '../../auth/hooks/useAuth'
import customerApi from '../api/customerApi'
import disputeApi from '../api/disputeApi'
import orderApi from '../../order/api/orderApi'
import { useLanguage } from '../../../i18n/LanguageContext'

export const CustomerDashboard = () => {
  const { phoneNumber } = useAuth()
  const { t } = useLanguage()

  const [loading, setLoading] = useState(true)
  const [profile, setProfile] = useState(null)
  const [profileComplete, setProfileComplete] = useState(true)
  const [orders, setOrders] = useState([])
  const [disputesCount, setDisputesCount] = useState(0)

  useEffect(() => {
    loadDashboardData()
  }, [])

  const loadDashboardData = async () => {
    setLoading(true)
    try {
      const [profileRes, statusRes, ordersRes, disputesRes] = await Promise.allSettled([
        customerApi.getProfile(),
        customerApi.getProfileStatus(),
        orderApi.getMyOrders({ page: 0, size: 5 }),
        disputeApi.getMyDisputes(),
      ])

      if (profileRes.status === 'fulfilled' && profileRes.value?.data?.data) {
        setProfile(profileRes.value.data.data)
      }
      if (statusRes.status === 'fulfilled' && statusRes.value?.data?.data) {
        setProfileComplete(statusRes.value.data.data.profileComplete ?? true)
      }
      if (ordersRes.status === 'fulfilled' && ordersRes.value?.data) {
        const orderData = ordersRes.value.data.data?.content || ordersRes.value.data?.content || ordersRes.value.data || []
        setOrders(Array.isArray(orderData) ? orderData : [])
      }
      if (disputesRes.status === 'fulfilled' && disputesRes.value?.data) {
        const dispData = disputesRes.value.data.data?.content || disputesRes.value.data?.data || disputesRes.value.data || []
        setDisputesCount(Array.isArray(dispData) ? dispData.length : 0)
      }
    } catch {
      // Graceful fallback
    } finally {
      setLoading(false)
    }
  }

  const activeOrders = orders.filter(
    (o) => o.status === 'PENDING' || o.status === 'CONFIRMED' || o.status === 'PROCESSING' || o.status === 'SHIPPED'
  ).length

  return (
    <CustomerLayout>
      <div className="space-y-6">
        <PageHeader
          title={
            <span>
              Customer <span className="text-primary-medium font-bold">Portal</span>
            </span>
          }
          subtitle={
            <span>
              Welcome back{profile?.fullName ? `, ${profile.fullName}` : ''} | Account:{' '}
              <strong className="font-mono">{phoneNumber || 'Customer'}</strong>
            </span>
          }
          action={
            <div className="flex gap-2">
              <Link to="/marketplace">
                <Button variant="primary">🛍️ Browse Honey</Button>
              </Link>
            </div>
          }
        />

        {!profileComplete && (
          <div className="p-4 bg-amber-50 border border-amber-200 rounded-lg flex flex-col sm:flex-row items-start sm:items-center justify-between gap-3">
            <div>
              <p className="font-semibold text-amber-900">Complete Your Delivery Profile</p>
              <p className="text-sm text-amber-800">
                Add your shipping address and contact name to enable one-click checkout.
              </p>
            </div>
            <Link to="/customer/profile">
              <Button variant="secondary" size="sm">
                Complete Profile →
              </Button>
            </Link>
          </div>
        )}

        {/* Metrics Grid */}
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 align-stretch">
          <MetricCard
            icon="🛒"
            label="Total Orders"
            value={loading ? '...' : orders.length.toString()}
            subtext="Placed to date"
          />
          <MetricCard
            icon="📦"
            label="Active Shipments"
            value={loading ? '...' : activeOrders.toString()}
            subtext="In transit / processing"
          />
          <MetricCard
            icon="⚖️"
            label="Disputes"
            value={loading ? '...' : disputesCount.toString()}
            subtext="Submitted cases"
          />
          <MetricCard
            icon="✨"
            label="Profile Status"
            value={profileComplete ? 'Complete' : 'Pending'}
            subtext={profileComplete ? 'Verified buyer' : 'Action needed'}
          />
        </div>

        {/* Quick Navigation Cards */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <Card className="p-5 flex flex-col justify-between hover:border-primary-light transition-all shadow-sm">
            <div className="space-y-2">
              <div className="text-3xl">🍯</div>
              <h3 className="text-lg font-bold text-slate-900">Traceable Marketplace</h3>
              <p className="text-sm text-slate-600">
                Explore 100% lab-verified raw honey directly from certified beekeepers with blockchain traceability.
              </p>
            </div>
            <div className="mt-4 pt-3 border-t border-slate-100">
              <Link to="/marketplace" className="text-sm font-semibold text-primary hover:underline inline-flex items-center gap-1">
                Explore Marketplace →
              </Link>
            </div>
          </Card>

          <Card className="p-5 flex flex-col justify-between hover:border-primary-light transition-all shadow-sm">
            <div className="space-y-2">
              <div className="text-3xl">🔍</div>
              <h3 className="text-lg font-bold text-slate-900">Verify Honey Batch</h3>
              <p className="text-sm text-slate-600">
                Scan your jar's QR code or enter a batch ID to inspect lab purity tests, pollen composition, and hive origin.
              </p>
            </div>
            <div className="mt-4 pt-3 border-t border-slate-100">
              <Link to="/verify" className="text-sm font-semibold text-primary hover:underline inline-flex items-center gap-1">
                Verify Batch →
              </Link>
            </div>
          </Card>

          <Card className="p-5 flex flex-col justify-between hover:border-primary-light transition-all shadow-sm">
            <div className="space-y-2">
              <div className="text-3xl">🛡️</div>
              <h3 className="text-lg font-bold text-slate-900">Dispute & Support</h3>
              <p className="text-sm text-slate-600">
                Need assistance with an order or test discrepancy? File a dispute with full transparent resolution tracking.
              </p>
            </div>
            <div className="mt-4 pt-3 border-t border-slate-100">
              <Link to="/customer/disputes" className="text-sm font-semibold text-primary hover:underline inline-flex items-center gap-1">
                Manage Disputes →
              </Link>
            </div>
          </Card>
        </div>

        {/* Recent Orders Section */}
        <Card className="p-6 space-y-4">
          <div className="flex items-center justify-between">
            <div>
              <h2 className="text-lg font-bold text-slate-900">Recent Orders</h2>
              <p className="text-xs text-slate-500">Your latest purchases and live fulfillment status</p>
            </div>
            <Link to="/orders">
              <Button variant="ghost" size="sm">
                View All Orders →
              </Button>
            </Link>
          </div>

          {loading ? (
            <div className="py-8 text-center text-slate-500">
              <div className="btn-spinner__icon inline-block mb-2" />
              <p className="text-sm">Loading orders...</p>
            </div>
          ) : orders.length === 0 ? (
            <div className="py-10 text-center border border-dashed border-slate-200 rounded-lg space-y-2">
              <div className="text-4xl">🛍️</div>
              <p className="font-semibold text-slate-700">No orders placed yet</p>
              <p className="text-xs text-slate-500">Browse pure certified honey batches in our marketplace.</p>
              <div className="pt-2">
                <Link to="/marketplace">
                  <Button variant="secondary" size="sm">
                    Start Shopping
                  </Button>
                </Link>
              </div>
            </div>
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full text-left text-sm">
                <thead>
                  <tr className="border-b border-slate-200 text-xs uppercase tracking-wider text-slate-500">
                    <th className="pb-3 font-semibold">Order #</th>
                    <th className="pb-3 font-semibold">Date</th>
                    <th className="pb-3 font-semibold">Items</th>
                    <th className="pb-3 font-semibold">Total Amount</th>
                    <th className="pb-3 font-semibold">Status</th>
                    <th className="pb-3 font-semibold text-right">Action</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-100">
                  {orders.slice(0, 5).map((order) => (
                    <tr key={order.orderNumber || order.id} className="hover:bg-slate-50 transition-colors">
                      <td className="py-3 font-mono font-medium text-slate-800">
                        {order.orderNumber || `#${order.id}`}
                      </td>
                      <td className="py-3 text-slate-600">
                        {order.createdAt ? new Date(order.createdAt).toLocaleDateString() : 'Recent'}
                      </td>
                      <td className="py-3 text-slate-600">
                        {order.items?.length || 1} item(s)
                      </td>
                      <td className="py-3 font-semibold text-slate-900 font-mono">
                        ₹{order.totalAmount ?? order.totalPrice ?? '—'}
                      </td>
                      <td className="py-3">
                        <span className={`badge ${
                          order.status === 'DELIVERED'
                            ? 'badge--success'
                            : order.status === 'CANCELLED'
                            ? 'badge--error'
                            : 'badge--info'
                        }`}>
                          {order.status || 'PROCESSING'}
                        </span>
                      </td>
                      <td className="py-3 text-right">
                        <Link to={`/orders/${order.orderNumber || order.id}`}>
                          <Button variant="ghost" size="xs">
                            Details
                          </Button>
                        </Link>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </Card>
      </div>
    </CustomerLayout>
  )
}

export default CustomerDashboard
