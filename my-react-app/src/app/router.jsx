import { Routes, Route, Navigate, Outlet } from 'react-router-dom'
import { useSelector } from 'react-redux'
import { ROLES } from '../constants/roles'

// Pages
import LandingPage from '../features/landing/pages/LandingPage'
import LoginPage from '../features/auth/pages/LoginPage'
import OtpLoginPage from '../features/auth/pages/OtpLoginPage'
import RegisterPage from '../features/auth/pages/RegisterPage'

// Marketplace Pages
import MarketplacePage from '../features/marketplace/pages/MarketplacePage'
import ProductDetailsPage from '../features/marketplace/pages/ProductDetailsPage'
import MyProductsPage from '../features/marketplace/pages/MyProductsPage'
import CreateProductPage from '../features/marketplace/pages/CreateProductPage'

// Cart & Orders Pages
import CartPage from '../features/cart/pages/CartPage'
import CheckoutPage from '../features/order/pages/CheckoutPage'
import OrdersPage from '../features/order/pages/OrdersPage'
import MyReviewsPage from '../features/review/pages/MyReviewsPage'
import OrderDetailsPage from '../features/order/pages/OrderDetailsPage'
import BeekeeperOrdersPage from '../features/order/pages/BeekeeperOrdersPage'

// Beekeeper & Hive Pages
import BeekeeperDashboard from '../features/beekeeper/pages/BeekeeperDashboard'
import BeekeeperOnboardingPage from '../features/beekeeper/pages/BeekeeperOnboardingPage'
import BeekeeperProfilePage from '../features/beekeeper/pages/BeekeeperProfilePage'
import HiveListPage from '../features/hive/pages/HiveListPage'
import HiveDetailsPage from '../features/hive/pages/HiveDetailsPage'
import BatchListPage from '../features/batch/pages/BatchListPage'
import CreateBatchPage from '../features/batch/pages/CreateBatchPage'
import BatchDetailsPage from '../features/batch/pages/BatchDetailsPage'
import HiveHealthOverviewPage from '../features/iot/pages/HiveHealthOverviewPage'
import HiveHealthDetailsPage from '../features/iot/pages/HiveHealthDetailsPage'
import BeekeeperEarningsPage from '../features/beekeeper/pages/BeekeeperEarningsPage'

// Customer & Lab & Admin Pages
import CustomerDashboard from '../features/customer/pages/CustomerDashboard'
import CustomerProfilePage from '../features/customer/pages/CustomerProfilePage'
import CustomerDisputesPage from '../features/customer/pages/CustomerDisputesPage'
import LabDashboardPage from '../features/lab/pages/LabDashboardPage'
import PendingTestsPage from '../features/lab/pages/PendingTestsPage'
import LabTestDetailsPage from '../features/lab/pages/LabTestDetailsPage'
import LabTestHistoryPage from '../features/lab/pages/LabTestHistoryPage'
import PublicVerificationPage from '../features/verification/pages/PublicVerificationPage'

// Admin & KVIC Officer Suite (Phase 15)
import AdminDashboardPage from '../features/admin/pages/AdminDashboardPage'
import AdminBeekeepersPage from '../features/admin/pages/AdminBeekeepersPage'
import AdminBeekeeperDetailsPage from '../features/admin/pages/AdminBeekeeperDetailsPage'
import AdminBatchesPage from '../features/admin/pages/AdminBatchesPage'
import AdminBatchDetailsPage from '../features/admin/pages/AdminBatchDetailsPage'
import AdminLabPage from '../features/admin/pages/AdminLabPage'
import AdminHivesPage from '../features/admin/pages/AdminHivesPage'
import AdminAnalyticsPage from '../features/admin/pages/AdminAnalyticsPage'
import AdminVerificationRiskPage from '../features/admin/pages/AdminVerificationRiskPage'
import AdminDisputesPage from '../features/admin/pages/AdminDisputesPage'
import AdminOrdersPage from '../features/admin/pages/AdminOrdersPage'

// Notifications Page (Phase 18)
import NotificationsPage from '../features/notification/pages/NotificationsPage'
import ForbiddenPage from '../components/feedback/ForbiddenPage'

const ProtectedRoute = ({ allowedRoles }) => {
  const { isAuthenticated, role } = useSelector((state) => state.auth)

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />
  }

  if (allowedRoles && !allowedRoles.includes(role)) {
    return <ForbiddenPage />
  }

  return <Outlet />
}

export const AppRouter = () => {
  return (
    <Routes>
      {/* Public routes */}
      <Route path="/" element={<LandingPage />} />
      <Route path="/login" element={<LoginPage initialRole="ADMIN" />} />
      <Route path="/login/customer" element={<OtpLoginPage />} />
      <Route path="/login/beekeeper" element={<OtpLoginPage />} />
      <Route path="/login/lab" element={<LoginPage initialRole="LAB" />} />
      <Route path="/login/admin" element={<LoginPage initialRole="ADMIN" />} />
      <Route path="/login/kvic" element={<LoginPage initialRole="ADMIN" />} />
      <Route path="/otp-login" element={<OtpLoginPage />} />
      <Route path="/login/otp" element={<OtpLoginPage />} />
      <Route path="/register" element={<RegisterPage />} />
      <Route path="/verify/:batchId" element={<PublicVerificationPage />} />
      <Route path="/marketplace" element={<MarketplacePage />} />
      <Route path="/marketplace/product/:id" element={<ProductDetailsPage />} />

      {/* Authenticated Notifications route */}
      <Route element={<ProtectedRoute />}>
        <Route path="/notifications" element={<NotificationsPage />} />
      </Route>

      {/* Beekeeper routes */}
      <Route element={<ProtectedRoute allowedRoles={[ROLES.BEEKEEPER]} />}>
        <Route path="/beekeeper/dashboard" element={<BeekeeperDashboard />} />
        <Route path="/beekeeper/onboarding" element={<BeekeeperOnboardingPage />} />
        <Route path="/beekeeper/profile" element={<BeekeeperProfilePage />} />
        <Route path="/beekeeper/settings" element={<BeekeeperProfilePage />} />
        <Route path="/hives" element={<HiveListPage />} />
        <Route path="/beekeeper/hives" element={<HiveListPage />} />
        <Route path="/hives/:id" element={<HiveDetailsPage />} />
        <Route path="/beekeeper/hives/:id" element={<HiveDetailsPage />} />
        <Route path="/batches" element={<BatchListPage />} />
        <Route path="/beekeeper/batches" element={<BatchListPage />} />
        <Route path="/batches/new" element={<CreateBatchPage />} />
        <Route path="/beekeeper/batches/new" element={<CreateBatchPage />} />
        <Route path="/batches/:batchId" element={<BatchDetailsPage />} />
        <Route path="/beekeeper/batches/:batchId" element={<BatchDetailsPage />} />
        <Route path="/beekeeper/batches/:batchId/lab-result" element={<BatchDetailsPage />} />
        <Route path="/hives/health" element={<HiveHealthOverviewPage />} />
        <Route path="/beekeeper/hive-health" element={<HiveHealthOverviewPage />} />
        <Route path="/beekeeper/hives/health" element={<HiveHealthOverviewPage />} />
        <Route path="/hives/:id/health" element={<HiveHealthDetailsPage />} />
        <Route path="/beekeeper/hives/:id/health" element={<HiveHealthDetailsPage />} />
        <Route path="/my-products" element={<MyProductsPage />} />
        <Route path="/beekeeper/products" element={<MyProductsPage />} />
        <Route path="/my-products/new" element={<CreateProductPage />} />
        <Route path="/beekeeper/products/new" element={<CreateProductPage />} />
        <Route path="/beekeeper/orders" element={<BeekeeperOrdersPage />} />
        <Route path="/beekeeper/earnings" element={<BeekeeperEarningsPage />} />
        <Route path="/beekeeper/notifications" element={<NotificationsPage />} />
      </Route>

      {/* Customer routes */}
      <Route element={<ProtectedRoute allowedRoles={[ROLES.CUSTOMER]} />}>
        <Route path="/customer/dashboard" element={<CustomerDashboard />} />
        <Route path="/customer/home" element={<MarketplacePage />} />
        <Route path="/customer/marketplace" element={<MarketplacePage />} />
        <Route path="/customer/products/:id" element={<ProductDetailsPage />} />
        <Route path="/customer/profile" element={<CustomerProfilePage />} />
        <Route path="/customer/disputes" element={<CustomerDisputesPage />} />
        <Route path="/cart" element={<CartPage />} />
        <Route path="/customer/cart" element={<CartPage />} />
        <Route path="/checkout" element={<CheckoutPage />} />
        <Route path="/customer/checkout" element={<CheckoutPage />} />
        <Route path="/orders" element={<OrdersPage />} />
        <Route path="/customer/orders" element={<OrdersPage />} />
        <Route path="/orders/:orderNumber" element={<OrderDetailsPage />} />
        <Route path="/customer/orders/:orderNumber" element={<OrderDetailsPage />} />
        <Route path="/my-reviews" element={<MyReviewsPage />} />
        <Route path="/customer/reviews" element={<MyReviewsPage />} />
        <Route path="/customer/verification-history" element={<OrdersPage />} />
        <Route path="/customer/notifications" element={<NotificationsPage />} />
      </Route>

      {/* Lab routes */}
      <Route element={<ProtectedRoute allowedRoles={[ROLES.LAB]} />}>
        <Route path="/lab/dashboard" element={<LabDashboardPage />} />
        <Route path="/lab/profile" element={<LabDashboardPage />} />
        <Route path="/lab/tests" element={<PendingTestsPage />} />
        <Route path="/lab/tests/pending" element={<PendingTestsPage />} />
        <Route path="/lab/tests/:batchId" element={<LabTestDetailsPage />} />
        <Route path="/lab/tests/:batchId/result" element={<LabTestDetailsPage />} />
        <Route path="/lab/certificates" element={<LabTestHistoryPage />} />
        <Route path="/lab/history" element={<LabTestHistoryPage />} />
        <Route path="/lab/notifications" element={<NotificationsPage />} />
        <Route path="/lab/settings" element={<LabDashboardPage />} />
      </Route>

      {/* Admin / KVIC routes (Phase 15) */}
      <Route element={<ProtectedRoute allowedRoles={[ROLES.ADMIN, ROLES.KVIC_OFFICER]} />}>
        <Route path="/admin/dashboard" element={<AdminDashboardPage />} />
        <Route path="/admin/beekeepers" element={<AdminBeekeepersPage />} />
        <Route path="/admin/beekeepers/:id" element={<AdminBeekeeperDetailsPage />} />
        <Route path="/admin/batches" element={<AdminBatchesPage />} />
        <Route path="/admin/batches/:batchId" element={<AdminBatchDetailsPage />} />
        <Route path="/admin/lab" element={<AdminLabPage />} />
        <Route path="/admin/lab-tests" element={<AdminLabPage />} />
        <Route path="/admin/hives" element={<AdminHivesPage />} />
        <Route path="/admin/orders" element={<AdminOrdersPage />} />
        <Route path="/admin/analytics" element={<AdminAnalyticsPage />} />
        <Route path="/admin/verification-risk" element={<AdminVerificationRiskPage />} />
        <Route path="/admin/disputes" element={<AdminDisputesPage />} />
        <Route path="/admin/notifications" element={<NotificationsPage />} />
        <Route path="/admin/settings" element={<AdminDashboardPage />} />
      </Route>

      {/* Fallback */}
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  )
}

export default AppRouter
