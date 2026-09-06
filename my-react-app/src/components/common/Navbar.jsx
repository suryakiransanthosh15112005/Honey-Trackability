import React, { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useDispatch, useSelector } from 'react-redux'
import { logout } from '../../features/auth/authSlice'
import { useLanguage } from '../../i18n/LanguageContext'
import LanguageSelector from './LanguageSelector'
import NotificationBell from '../../features/notification/components/NotificationBell'

export const Navbar = ({ transparent = false, onMobileToggle }) => {
  const { isAuthenticated, role } = useSelector((state) => state.auth)
  const { itemCount } = useSelector((state) => state.cart || { itemCount: 0 })
  const { t } = useLanguage()
  const dispatch = useDispatch()
  const navigate = useNavigate()
  const [mobileOpen, setMobileOpen] = useState(false)

  const handleLogout = () => {
    dispatch(logout())
    setMobileOpen(false)
    navigate('/login')
  }

  return (
    <nav id="main-navbar" className={`navbar${transparent ? ' navbar--transparent' : ''}`}>
      <div className="w-full max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 flex items-center justify-between gap-4">
        {/* Brand Logo */}
        <div className="flex items-center gap-6">
          <Link to="/" id="nav-logo" className="navbar__logo shrink-0" onClick={() => setMobileOpen(false)}>
            <div className="navbar__logo-icon">🍯</div>
            <span className="navbar__logo-name">HoneyChain</span>
          </Link>

          {/* Desktop Navigation Links */}
          <div className="navbar__links hidden md:flex items-center gap-2">
            <Link to="/marketplace" className="navbar__link">
              {t('nav.marketplace', 'Marketplace')}
            </Link>

            {isAuthenticated && role === 'BEEKEEPER' && (
              <>
                <Link to="/beekeeper/dashboard" className="navbar__link">
                  {t('nav.dashboard', 'Dashboard')}
                </Link>
                <Link to="/hives" className="navbar__link">
                  {t('nav.myHives', 'My Hives')}
                </Link>
                <Link to="/batches" className="navbar__link">
                  {t('nav.myBatches', 'My Batches')}
                </Link>
                <Link to="/hives/health" className="navbar__link">
                  {t('nav.hiveHealth', 'Hive Health')}
                </Link>
              </>
            )}

            {isAuthenticated && role === 'CUSTOMER' && (
              <>
                <Link to="/customer/dashboard" className="navbar__link">
                  {t('nav.dashboard', 'Dashboard')}
                </Link>
                <Link to="/customer/profile" className="navbar__link">
                  {t('nav.profile', 'Profile')}
                </Link>
                <Link to="/orders" className="navbar__link">
                  {t('nav.orders', 'My Orders')}
                </Link>
                <Link to="/my-reviews" className="navbar__link">
                  {t('nav.myReviews', 'My Reviews')}
                </Link>
                <Link to="/customer/disputes" className="navbar__link">
                  {t('nav.disputes', 'Disputes')}
                </Link>
              </>
            )}

            {isAuthenticated && (role === 'ADMIN' || role === 'KVIC_OFFICER') && (
              <>
                <Link to="/admin/dashboard" className="navbar__link">
                  {t('nav.adminDashboard', 'Dashboard')}
                </Link>
                <Link to="/admin/beekeepers" className="navbar__link">
                  {t('nav.beekeepers', 'Beekeepers')}
                </Link>
                <Link to="/admin/batches" className="navbar__link">
                  {t('nav.batches', 'Batches')}
                </Link>
                <Link to="/admin/analytics" className="navbar__link">
                  {t('nav.analytics', 'Analytics')}
                </Link>
              </>
            )}
          </div>
        </div>

        {/* Actions */}
        <div className="navbar__actions flex items-center gap-2 sm:gap-3 shrink-0">
          {/* Language Selector Dropdown */}
          <div className="flex items-center">
            <LanguageSelector />
          </div>

          {/* Notification Bell */}
          {isAuthenticated && <NotificationBell />}

          {/* Cart Icon for Customers / Public */}
          {(!isAuthenticated || role === 'CUSTOMER') && (
            <Link to="/cart" id="nav-cart-btn" className="btn btn--ghost btn--sm relative" title={t('accessibility.viewCart', 'View Cart')}>
              <span>🛒 {t('nav.cart', 'Cart')}</span>
              {itemCount > 0 && (
                <span className="badge badge--gold badge--xs ml-1 font-bold">
                  {itemCount}
                </span>
              )}
            </Link>
          )}

          {isAuthenticated ? (
            <div className="hidden md:flex items-center gap-2">
              <span className="navbar__role-badge">{t('roles.' + role, role)}</span>
              <button
                id="nav-logout-btn"
                onClick={handleLogout}
                className="btn btn--secondary btn--sm"
              >
                {t('nav.logout', 'Logout')}
              </button>
            </div>
          ) : (
            <Link to="/login" id="nav-login-btn" className="btn btn--primary btn--sm hidden md:inline-flex">
              {t('nav.login', 'Login')}
            </Link>
          )}

          {/* Mobile Hamburger Toggle Button */}
          <button
            type="button"
            id="nav-mobile-toggle"
            onClick={() => {
              if (onMobileToggle) {
                onMobileToggle()
              } else {
                setMobileOpen(!mobileOpen)
              }
            }}
            className="md:hidden btn btn--ghost btn--sm p-2 text-slate-700"
            aria-label="Toggle Navigation Menu"
          >
            {mobileOpen ? '✕' : '☰'}
          </button>
        </div>
      </div>

      {/* Mobile Drawer / Menu Panel */}
      {mobileOpen && (
        <div className="md:hidden w-full bg-white border-b border-slate-200 px-4 py-4 space-y-3">
          <div className="flex items-center justify-between pb-2 border-b border-slate-100">
            <LanguageSelector />
            {isAuthenticated && <span className="navbar__role-badge">{t('roles.' + role, role)}</span>}
          </div>

          <div className="flex flex-col space-y-1">
            <Link to="/marketplace" className="navbar__link" onClick={() => setMobileOpen(false)}>
              {t('nav.marketplace', 'Marketplace')}
            </Link>

            {isAuthenticated && role === 'BEEKEEPER' && (
              <>
                <Link to="/beekeeper/dashboard" className="navbar__link" onClick={() => setMobileOpen(false)}>
                  {t('nav.dashboard', 'Dashboard')}
                </Link>
                <Link to="/hives" className="navbar__link" onClick={() => setMobileOpen(false)}>
                  {t('nav.myHives', 'My Hives')}
                </Link>
                <Link to="/batches" className="navbar__link" onClick={() => setMobileOpen(false)}>
                  {t('nav.myBatches', 'My Batches')}
                </Link>
                <Link to="/hives/health" className="navbar__link" onClick={() => setMobileOpen(false)}>
                  {t('nav.hiveHealth', 'Hive Health')}
                </Link>
              </>
            )}

            {isAuthenticated && role === 'CUSTOMER' && (
              <>
                <Link to="/customer/dashboard" className="navbar__link" onClick={() => setMobileOpen(false)}>
                  {t('nav.dashboard', 'Dashboard')}
                </Link>
                <Link to="/customer/profile" className="navbar__link" onClick={() => setMobileOpen(false)}>
                  {t('nav.profile', 'Profile')}
                </Link>
                <Link to="/orders" className="navbar__link" onClick={() => setMobileOpen(false)}>
                  {t('nav.orders', 'My Orders')}
                </Link>
                <Link to="/my-reviews" className="navbar__link" onClick={() => setMobileOpen(false)}>
                  {t('nav.myReviews', 'My Reviews')}
                </Link>
                <Link to="/customer/disputes" className="navbar__link" onClick={() => setMobileOpen(false)}>
                  {t('nav.disputes', 'Disputes')}
                </Link>
              </>
            )}

            {isAuthenticated && (role === 'ADMIN' || role === 'KVIC_OFFICER') && (
              <>
                <Link to="/admin/dashboard" className="navbar__link" onClick={() => setMobileOpen(false)}>
                  {t('nav.adminDashboard', 'Dashboard')}
                </Link>
                <Link to="/admin/beekeepers" className="navbar__link" onClick={() => setMobileOpen(false)}>
                  {t('nav.beekeepers', 'Beekeepers')}
                </Link>
                <Link to="/admin/batches" className="navbar__link" onClick={() => setMobileOpen(false)}>
                  {t('nav.batches', 'Batches')}
                </Link>
                <Link to="/admin/analytics" className="navbar__link" onClick={() => setMobileOpen(false)}>
                  {t('nav.analytics', 'Analytics')}
                </Link>
              </>
            )}
          </div>

          <div className="pt-2 border-t border-slate-100 flex items-center justify-end gap-2">
            {isAuthenticated ? (
              <button
                onClick={handleLogout}
                className="btn btn--secondary btn--sm w-full"
              >
                {t('nav.logout', 'Logout')}
              </button>
            ) : (
              <Link to="/login" className="btn btn--primary btn--sm w-full text-center" onClick={() => setMobileOpen(false)}>
                {t('nav.login', 'Login')}
              </Link>
            )}
          </div>
        </div>
      )}
    </nav>
  )
}

export default Navbar
