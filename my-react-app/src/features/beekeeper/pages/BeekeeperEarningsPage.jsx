import React, { useState, useEffect, useMemo } from 'react'
import { Link } from 'react-router-dom'
import BeekeeperLayout from '../../../layouts/BeekeeperLayout'
import orderApi from '../../order/api/orderApi'
import Card from '../../../components/ui/Card'
import MetricCard from '../../../components/ui/MetricCard'
import Alert from '../../../components/feedback/Alert'
import LoadingSpinner from '../../../components/feedback/LoadingSpinner'

export const BeekeeperEarningsPage = () => {
  const [orders, setOrders] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
    const fetchOrders = async () => {
      try {
        setLoading(true)
        setError(null)
        const response = await orderApi.getBeekeeperOrders({ page: 0, size: 100 })
        const data = response?.data?.data || response?.data || response
        const list = Array.isArray(data?.content)
          ? data.content
          : Array.isArray(data)
            ? data
            : []
        setOrders(list)
      } catch (err) {
        console.error('Failed to load orders for earnings:', err)
        setError(
          err?.response?.data?.message ||
            'Failed to load earnings records. Please check your backend connection.'
        )
      } finally {
        setLoading(false)
      }
    }
    fetchOrders()
  }, [])

  // Aggregate financial metrics
  const { metrics, chartData } = useMemo(() => {
    let totalRevenue = 0
    let settledRevenue = 0
    let pendingRevenue = 0
    let totalHoneyKg = 0
    const completed = []
    const pending = []
    const dailyMap = {}

    // Initialize 7 days placeholder for continuous trend visualization
    for (let i = 6; i >= 0; i--) {
      const d = new Date()
      d.setDate(d.getDate() - i)
      const key = d.toLocaleDateString('en-US', { month: 'short', day: 'numeric' })
      dailyMap[key] = 0
    }

    orders.forEach((order) => {
      const status = (order.orderStatus || order.status || '').toUpperCase()
      const isPaidOrDelivered = ['DELIVERED', 'PAID'].includes(status)
      const isInProgress = ['CONFIRMED', 'PACKED', 'SHIPPED'].includes(status)
      const orderTotal = Number(order.totalAmount || order.amount || 0)

      if (isPaidOrDelivered || isInProgress) {
        totalRevenue += orderTotal

        if (isPaidOrDelivered) {
          settledRevenue += orderTotal
          completed.push(order)
        } else {
          pendingRevenue += orderTotal
          pending.push(order)
        }

        // Daily grouping
        const rawDate = order.createdAt || order.orderDate || new Date().toISOString()
        const dateKey = new Date(rawDate).toLocaleDateString('en-US', { month: 'short', day: 'numeric' })
        dailyMap[dateKey] = (dailyMap[dateKey] || 0) + orderTotal
      }

      // Calculate Honey Weight Sold
      if (Array.isArray(order.items)) {
        order.items.forEach((it) => {
          totalHoneyKg += Number(it.quantityKg || it.quantity || it.weightKg || 1)
        })
      }
    })

    const chartData = Object.entries(dailyMap).map(([date, revenue]) => ({
      date,
      revenue,
    }))

    const validOrderCount = completed.length + pending.length

    return {
      metrics: {
        totalRevenue,
        settledRevenue,
        pendingRevenue,
        totalOrders: validOrderCount,
        deliveredOrders: completed.length,
        pendingOrders: pending.length,
        totalHoneyKg,
        avgOrderValue: validOrderCount > 0 ? totalRevenue / validOrderCount : 0,
      },
      chartData,
      completedOrders: completed,
      pendingOrders: pending,
    }
  }, [orders])

  const maxChartVal = useMemo(() => {
    return Math.max(...chartData.map((x) => x.revenue), 1000)
  }, [chartData])

  return (
    <BeekeeperLayout>
      <div className="space-y-6">
        {/* Page Header */}
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-2 border-b border-slate-200">
          <div>
            <div className="flex items-center gap-2 mb-1">
              <span className="inline-flex items-center gap-1.5 px-2.5 py-0.5 rounded-full text-xs font-bold bg-amber-50 text-amber-900 border border-amber-200">
                💰 Payout & Revenue Ledger
              </span>
              <span className="inline-flex items-center gap-1.5 px-2.5 py-0.5 rounded-full text-xs font-semibold bg-blue-50 text-blue-800 border border-blue-200">
                ⚡ Direct Bank Transfer Active
              </span>
            </div>
            <h1 className="text-2xl sm:text-3xl font-black text-slate-900 font-['Outfit']">
              Beekeeper Revenue & Earnings
            </h1>
            <p className="text-slate-600 text-xs sm:text-sm mt-1">
              Real-time marketplace payouts and financial ledger from your certified pure honey harvests.
            </p>
          </div>

          <div className="flex items-center gap-3">
            <Link
              to="/beekeeper/products/new"
              className="inline-flex items-center gap-2 px-3.5 py-2 rounded-lg bg-blue-600 hover:bg-blue-700 text-white text-xs font-bold shadow-xs transition-colors"
            >
              <span>+</span>
              <span>New Listing</span>
            </Link>
            <Link
              to="/beekeeper/orders"
              className="inline-flex items-center gap-2 px-3.5 py-2 rounded-lg bg-white border border-slate-300 text-xs font-semibold text-slate-700 hover:bg-slate-50 shadow-xs transition-colors"
            >
              <span>📦</span>
              <span>Fulfill Orders</span>
            </Link>
          </div>
        </div>

        {error && <Alert type="danger" message={error} onClose={() => setError(null)} />}

        {/* Loading State */}
        {loading ? (
          <div className="py-16 flex justify-center">
            <LoadingSpinner text="Computing honey sales, payouts, and revenue analytics..." />
          </div>
        ) : (
          <>
            {/* KPI Stat Cards */}
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 align-stretch">
              <MetricCard
                label="Total Revenue"
                value={`₹${metrics.totalRevenue.toLocaleString('en-IN', { maximumFractionDigits: 2 })}`}
                subtext={`₹${metrics.settledRevenue.toLocaleString('en-IN')} settled • ₹${metrics.pendingRevenue.toLocaleString('en-IN')} pending`}
                icon="💵"
              />
              <MetricCard
                label="Marketplace Orders"
                value={metrics.totalOrders}
                subtext={`${metrics.deliveredOrders} delivered • ${metrics.pendingOrders} in transit`}
                icon="📦"
              />
              <MetricCard
                label="Honey Dispatched"
                value={`${metrics.totalHoneyKg.toFixed(1)} kg`}
                subtext="Certified authentic stock"
                icon="🍯"
              />
              <MetricCard
                label="Avg Order Value"
                value={`₹${Math.round(metrics.avgOrderValue)}`}
                subtext="Per consumer purchase"
                icon="📈"
              />
            </div>

            {/* Payout Direct Transfer Banner */}
            <div className="p-4 rounded-xl bg-blue-50/70 border border-blue-200/80 flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 rounded-lg bg-white border border-blue-200 flex items-center justify-center text-xl shadow-xs shrink-0">
                  🏦
                </div>
                <div>
                  <h4 className="font-bold text-slate-900 text-sm">
                    KVIC Integrated Direct Bank Settlement
                  </h4>
                  <p className="text-slate-600 text-xs mt-0.5">
                    Order funds are automatically escrowed via smart contract and credited directly to your registered bank account upon verified delivery.
                  </p>
                </div>
              </div>
              <div className="shrink-0 flex items-center gap-2">
                <span className="font-mono text-xs px-2.5 py-1 rounded bg-white text-slate-700 border border-slate-200 font-semibold">
                  T+2 Settlement
                </span>
              </div>
            </div>

            {/* Revenue Trend Chart & Quick Metrics */}
            <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
              <Card className="lg:col-span-2 p-6 flex flex-col justify-between">
                <div>
                  <div className="flex items-center justify-between gap-2 mb-1">
                    <h2 className="text-base font-bold text-slate-900 font-['Outfit']">
                      7-Day Revenue Velocity
                    </h2>
                    <span className="text-[11px] font-semibold text-slate-600 bg-slate-100 px-2.5 py-0.5 rounded-full">
                      Daily Sales (INR)
                    </span>
                  </div>
                  <p className="text-xs text-slate-600 mb-6">
                    Direct marketplace earnings calculated from incoming and delivered consumer orders.
                  </p>
                </div>

                <div className="pt-4 pb-2">
                  <div className="h-52 flex items-end gap-2 sm:gap-4 border-b border-slate-200 px-2">
                    {chartData.map((d, idx) => {
                      const pct = Math.max(Math.round((d.revenue / maxChartVal) * 100), 4)
                      return (
                        <div
                          key={idx}
                          className="flex-1 flex flex-col items-center gap-2 h-full justify-end group relative"
                        >
                          {/* Hover Tooltip */}
                          <div className="opacity-0 group-hover:opacity-100 transition-opacity absolute -top-8 bg-slate-900 text-white text-[11px] font-mono py-1 px-2 rounded pointer-events-none whitespace-nowrap z-20 shadow-md">
                            ₹{d.revenue.toLocaleString('en-IN')}
                          </div>
                          {/* Value above bar */}
                          <span className="text-[10px] sm:text-[11px] font-mono text-slate-600 group-hover:text-blue-700 font-semibold transition-colors">
                            {d.revenue > 0
                              ? d.revenue >= 1000
                                ? `₹${(d.revenue / 1000).toFixed(1)}k`
                                : `₹${d.revenue}`
                              : '—'}
                          </span>
                          {/* Animated Bar */}
                          <div
                            className={`w-full max-w-11 rounded-t-md transition-all duration-300 ${
                              d.revenue > 0
                                ? 'bg-blue-600 group-hover:bg-blue-700 shadow-xs'
                                : 'bg-slate-200'
                            }`}
                            style={{ height: `${pct}%` }}
                          />
                        </div>
                      )
                    })}
                  </div>
                  {/* X-axis labels */}
                  <div className="flex gap-2 sm:gap-4 px-2 mt-2">
                    {chartData.map((d, idx) => (
                      <div
                        key={idx}
                        className="flex-1 text-center text-[10px] sm:text-[11px] font-medium text-slate-600 truncate"
                      >
                        {d.date}
                      </div>
                    ))}
                  </div>
                </div>
              </Card>

              {/* Settlement Summary Breakdown */}
              <Card className="p-6 flex flex-col justify-between space-y-4">
                <div>
                  <h3 className="text-base font-bold text-slate-900 font-['Outfit'] mb-1">
                    Financial Summary
                  </h3>
                  <p className="text-xs text-slate-600">
                    Overview of your order payouts and fulfillment pipeline.
                  </p>
                </div>

                <div className="space-y-3">
                  <div className="p-3 rounded-lg bg-slate-50 border border-slate-200 flex items-center justify-between">
                    <div>
                      <span className="text-xs text-slate-600 block">Settled to Bank</span>
                      <strong className="text-sm text-slate-900 font-mono">
                        ₹{metrics.settledRevenue.toLocaleString('en-IN', { maximumFractionDigits: 2 })}
                      </strong>
                    </div>
                    <span className="px-2 py-0.5 rounded text-[11px] font-bold bg-white text-blue-700 border border-blue-200">
                      PAID
                    </span>
                  </div>

                  <div className="p-3 rounded-lg bg-slate-50 border border-slate-200 flex items-center justify-between">
                    <div>
                      <span className="text-xs text-slate-600 block">Pending Delivery</span>
                      <strong className="text-sm text-slate-900 font-mono">
                        ₹{metrics.pendingRevenue.toLocaleString('en-IN', { maximumFractionDigits: 2 })}
                      </strong>
                    </div>
                    <span className="px-2 py-0.5 rounded text-[11px] font-bold bg-amber-50 text-amber-900 border border-amber-200">
                      IN ESCROW
                    </span>
                  </div>

                  <div className="p-3 rounded-lg bg-amber-50/60 border border-amber-200/80 flex items-center justify-between">
                    <div>
                      <span className="text-xs text-amber-900 block font-medium">Verified Harvest Yield</span>
                      <strong className="text-sm text-slate-900 font-mono">
                        {metrics.totalHoneyKg.toFixed(1)} Kilograms
                      </strong>
                    </div>
                    <span className="text-lg">🍯</span>
                  </div>
                </div>

                <div className="pt-2 border-t border-slate-200">
                  <Link
                    to="/beekeeper/orders"
                    className="w-full inline-flex items-center justify-center gap-2 py-2 rounded-lg bg-slate-100 hover:bg-slate-200 text-slate-800 text-xs font-bold transition-colors"
                  >
                    <span>Inspect Fulfillment Details →</span>
                  </Link>
                </div>
              </Card>
            </div>

            {/* Recent Orders / Transactions Table */}
            <Card className="p-6">
              <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2 mb-4">
                <div>
                  <h2 className="text-base font-bold text-slate-900 font-['Outfit']">
                    Recent Customer Transactions
                  </h2>
                  <p className="text-xs text-slate-600">
                    Direct marketplace orders and fulfillment progress.
                  </p>
                </div>
                <span className="text-xs text-slate-600 font-mono">
                  {orders.length} Total Orders Received
                </span>
              </div>

              {orders.length === 0 ? (
                <div className="py-14 text-center space-y-4">
                  <div className="w-16 h-16 rounded-full bg-amber-50 border border-amber-200 flex items-center justify-center mx-auto text-3xl">
                    🍯
                  </div>
                  <div>
                    <h3 className="text-base font-bold text-slate-900">No Orders or Earnings Yet</h3>
                    <p className="text-xs text-slate-600 max-w-sm mx-auto mt-1">
                      Once your honey batches pass laboratory testing and you list them on the marketplace, incoming sales will reflect in real time.
                    </p>
                  </div>
                  <div className="pt-2 flex items-center justify-center gap-3">
                    <Link
                      to="/beekeeper/products/new"
                      className="inline-flex items-center gap-1.5 px-4 py-2 rounded-lg bg-blue-600 hover:bg-blue-700 text-white text-xs font-bold shadow-xs transition-colors"
                    >
                      + Create Honey Listing
                    </Link>
                    <Link
                      to="/beekeeper/batches"
                      className="inline-flex items-center gap-1.5 px-4 py-2 rounded-lg bg-white border border-slate-300 text-slate-700 text-xs font-semibold hover:bg-slate-50 transition-colors"
                    >
                      View Batches
                    </Link>
                  </div>
                </div>
              ) : (
                <div className="overflow-x-auto border border-slate-200 rounded-lg">
                  <table className="w-full text-left text-xs sm:text-sm">
                    <thead>
                      <tr className="bg-slate-50 border-b border-slate-200 text-slate-600 uppercase text-[11px] font-bold tracking-wider">
                        <th className="py-3 px-4">Order #</th>
                        <th className="py-3 px-4">Date</th>
                        <th className="py-3 px-4">Delivery Destination</th>
                        <th className="py-3 px-4">Items / Quantity</th>
                        <th className="py-3 px-4">Status</th>
                        <th className="py-3 px-4 text-right">Revenue</th>
                      </tr>
                    </thead>
                    <tbody className="divide-y divide-slate-100 bg-white">
                      {orders.slice(0, 15).map((o) => {
                        const status = (o.orderStatus || o.status || 'CONFIRMED').toUpperCase()
                        const isDelivered = status === 'DELIVERED'
                        const isPending = ['CONFIRMED', 'PACKED', 'SHIPPED'].includes(status)
                        const totalKg = Array.isArray(o.items)
                          ? o.items.reduce((acc, it) => acc + Number(it.quantityKg || it.quantity || 1), 0)
                          : 1

                        return (
                          <tr key={o.orderNumber} className="hover:bg-slate-50/80 transition-colors">
                            <td className="py-3.5 px-4 font-mono font-bold text-blue-600">
                              <Link to="/beekeeper/orders" className="hover:underline">
                                {o.orderNumber}
                              </Link>
                            </td>
                            <td className="py-3.5 px-4 text-slate-600 whitespace-nowrap">
                              {o.createdAt ? new Date(o.createdAt).toLocaleDateString('en-IN') : 'Recent'}
                            </td>
                            <td className="py-3.5 px-4 text-slate-800 font-medium">
                              {o.deliveryAddress?.city
                                ? `${o.deliveryAddress.city}, ${o.deliveryAddress.state || ''}`
                                : o.deliveryAddress?.address || 'Direct Order'}
                            </td>
                            <td className="py-3.5 px-4 text-slate-600">
                              <span className="font-semibold text-slate-900">{totalKg.toFixed(1)} kg</span>{' '}
                              <span className="text-[11px] text-slate-600">
                                ({o.items?.length || 1} item{o.items?.length === 1 ? '' : 's'})
                              </span>
                            </td>
                            <td className="py-3.5 px-4">
                              <span
                                className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-[11px] font-bold ${
                                  isDelivered
                                    ? 'bg-blue-50 text-blue-800 border border-blue-200'
                                    : isPending
                                      ? 'bg-amber-50 text-amber-900 border border-amber-200'
                                      : 'bg-slate-100 text-slate-700 border border-slate-200'
                                }`}
                              >
                                {status}
                              </span>
                            </td>
                            <td className="py-3.5 px-4 text-right font-mono font-bold text-slate-900 whitespace-nowrap">
                              ₹{Number(o.totalAmount || 0).toFixed(2)}
                            </td>
                          </tr>
                        )
                      })}
                    </tbody>
                  </table>
                </div>
              )}
            </Card>
          </>
        )}
      </div>
    </BeekeeperLayout>
  )
}

export default BeekeeperEarningsPage
