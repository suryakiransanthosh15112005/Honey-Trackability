import React, { useEffect } from 'react'
import { useSelector } from 'react-redux'
import { ROLES } from '../../../constants/roles'
import BeekeeperLayout from '../../../layouts/BeekeeperLayout'
import CustomerLayout from '../../../layouts/CustomerLayout'
import AdminLayout from '../../../layouts/AdminLayout'
import LabLayout from '../../../layouts/LabLayout'
import Card from '../../../components/ui/Card'
import LoadingSpinner from '../../../components/feedback/LoadingSpinner'
import Alert from '../../../components/feedback/Alert'
import NotificationList from '../components/NotificationList'
import useNotifications from '../hooks/useNotifications'

export const NotificationsPage = () => {
  const { role } = useSelector((state) => state.auth)
  const { items, loading, error, fetchNotifications, markAllRead, clearError } = useNotifications()

  useEffect(() => {
    fetchNotifications({ page: 0, size: 30 })
  }, [fetchNotifications])

  const RoleLayout =
    role === ROLES.CUSTOMER
      ? CustomerLayout
      : role === ROLES.ADMIN || role === ROLES.KVIC_OFFICER
      ? AdminLayout
      : role === ROLES.LAB
      ? LabLayout
      : BeekeeperLayout

  return (
    <RoleLayout>
      <div className="w-full space-y-6">
        {/* Page Header */}
        <div>
          <h1 className="text-3xl font-black text-slate-900 font-['Outfit'] flex items-center gap-3">
            <span>🔔</span> Notifications Center
          </h1>
          <p className="text-slate-500 text-sm mt-1">
            Stay informed on lab quality tests, IoT hive alerts, marketplace orders, and profile status updates.
          </p>
        </div>

        {error && <Alert type="error" message={error} onClose={clearError} />}

        {loading && items.length === 0 ? (
          <LoadingSpinner text="Loading notifications..." />
        ) : (
          <div className="grid grid-cols-1 lg:grid-cols-12 gap-6 items-start">
            {/* Main Notifications Feed */}
            <div className="lg:col-span-8">
              <Card className="p-6 bg-white border border-slate-200/90 shadow-sm">
                <NotificationList items={items} onMarkAllRead={markAllRead} loading={loading} />
              </Card>
            </div>

            {/* Sidebar Preferences & Info */}
            <div className="lg:col-span-4 space-y-6">
              <Card className="p-5 border border-amber-200/80 bg-gradient-to-br from-amber-50/50 to-white space-y-3">
                <div className="flex items-center gap-2">
                  <span className="text-xl">⚙️</span>
                  <h3 className="font-bold text-slate-900 font-['Outfit'] text-sm">Notification Channels</h3>
                </div>
                <ul className="text-xs text-slate-600 space-y-2 leading-relaxed">
                  <li className="flex items-center justify-between py-1 border-b border-amber-100">
                    <span>IoT Health Alerts</span>
                    <span className="font-bold text-blue-600">Immediate</span>
                  </li>
                  <li className="flex items-center justify-between py-1 border-b border-amber-100">
                    <span>Lab Test Approvals</span>
                    <span className="font-bold text-blue-600">Real-time</span>
                  </li>
                  <li className="flex items-center justify-between py-1">
                    <span>Marketplace Orders</span>
                    <span className="font-bold text-blue-600">Push & SMS</span>
                  </li>
                </ul>
              </Card>

              <Card className="p-5 border border-blue-200/80 bg-gradient-to-br from-blue-50/40 to-white space-y-2">
                <div className="flex items-center gap-2">
                  <span className="text-xl">🛡️</span>
                  <h3 className="font-bold text-slate-900 font-['Outfit'] text-sm">Cryptographic Verification</h3>
                </div>
                <p className="text-xs text-slate-600 leading-relaxed">
                  System alerts are triggered directly by on-chain events and verified IoT threshold monitors.
                </p>
              </Card>
            </div>
          </div>
        )}
      </div>
    </RoleLayout>
  )
}

export default NotificationsPage
