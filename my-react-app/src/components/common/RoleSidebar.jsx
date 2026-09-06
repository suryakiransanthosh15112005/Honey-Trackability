import { useState } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import { useSelector, useDispatch } from 'react-redux'
import { logout } from '../../features/auth/authSlice'
import { ROLE_NAVIGATION } from '../../config/roleNavigation'
import { ROLE_LABELS } from '../../constants/roles'
import { useLanguage } from '../../i18n/LanguageContext'

export const RoleSidebar = ({ mobileOpen, setMobileOpen }) => {
  const { role, user } = useSelector((state) => state.auth)
  const [collapsed, setCollapsed] = useState(false)
  const location = useLocation()
  const navigate = useNavigate()
  const dispatch = useDispatch()
  const { t } = useLanguage()

  const handleLogout = () => {
    dispatch(logout())
    if (setMobileOpen) setMobileOpen(false)
    navigate('/login')
  }

  const roleNavGroups = ROLE_NAVIGATION[role] || []
  const roleLabel = t(`roles.${role}`, ROLE_LABELS[role] || role || 'Guest')

  const isActiveRoute = (route) => {
    if (!route) return false
    if (location.pathname === route) return true
    const hasMoreSpecificRoute = roleNavGroups.some((grp) =>
      grp.items.some((it) => it.route === location.pathname)
    )
    if (hasMoreSpecificRoute) return false
    return location.pathname.startsWith(`${route}/`)
  }

  const getGroupTitle = (group) => {
    const key = group.groupKey || group.group.toLowerCase().replace(/[^a-z0-9]+/g, '_')
    return t(`navGroup.${key}`, group.group)
  }

  const getItemLabel = (item) => {
    const key = item.itemKey || item.label.toLowerCase().replace(/[^a-z0-9]+/g, '_')
    return t(`navItem.${key}`, item.label)
  }

  return (
    <>
      {/* Mobile Drawer (Only visible on mobile when open) */}
      {mobileOpen && (
        <>
          <div
            className="fixed inset-0 bg-slate-900/50 backdrop-blur-xs z-40 md:hidden"
            onClick={() => setMobileOpen(false)}
          />
          <aside
            aria-label="Mobile Role Navigation Drawer"
            className="fixed inset-y-0 left-0 w-72 bg-white z-50 flex flex-col shadow-2xl md:hidden"
          >
            {/* Mobile Sidebar Header */}
            <div className="p-4 border-b border-slate-100 flex items-center justify-between gap-2 min-h-16">
              <div className="flex items-center gap-3 min-w-0">
                <div className="w-9 h-9 rounded-xl bg-gradient-to-br from-amber-400 via-amber-500 to-amber-600 flex items-center justify-center text-white text-lg font-bold shrink-0 shadow-sm shadow-amber-500/20">
                  🍯
                </div>
                <div className="min-w-0">
                  <div className="flex items-center gap-1.5">
                    <span className="w-1.5 h-1.5 rounded-full bg-emerald-500 animate-pulse" />
                    <span className="text-[10px] font-black text-slate-400 uppercase tracking-widest truncate">
                      {t('common.controlPortal', 'Control Portal')}
                    </span>
                  </div>
                  <div className="text-sm font-extrabold text-slate-900 font-['Outfit'] truncate">
                    {roleLabel}
                  </div>
                </div>
              </div>
              <button
                type="button"
                onClick={() => setMobileOpen(false)}
                className="p-1.5 rounded-lg text-slate-400 hover:text-slate-700 hover:bg-slate-100"
                aria-label={t('accessibility.closeMenu', 'Close navigation')}
              >
                ✕
              </button>
            </div>

            {/* Mobile Nav Items */}
            <div className="flex-1 overflow-y-auto p-3 space-y-5">
              {roleNavGroups.map((group, idx) => (
                <div key={idx} className="space-y-1">
                  <div className="px-3 pt-1 text-[10px] font-black text-slate-400 uppercase tracking-widest">
                    {getGroupTitle(group)}
                  </div>
                  <div className="space-y-1">
                    {group.items.map((item) => {
                      const active = isActiveRoute(item.route)
                      const label = getItemLabel(item)
                      return (
                        <Link
                          key={item.route}
                          to={item.route}
                          onClick={() => setMobileOpen(false)}
                          aria-current={active ? 'page' : undefined}
                          className={`sidebar-nav-item flex items-center gap-3 py-2.5 px-3 rounded-xl text-sm ${
                            active ? 'active' : ''
                          }`}
                        >
                          <span className="text-base leading-none shrink-0">{item.icon}</span>
                          <span className="truncate flex-1 text-left">{label}</span>
                          {active && (
                            <span className="w-1.5 h-1.5 rounded-full bg-amber-400 shadow-[0_0_6px_#f59e0b] shrink-0" />
                          )}
                        </Link>
                      )
                    })}
                  </div>
                </div>
              ))}
            </div>

            {/* Mobile Footer */}
            <div className="p-3 border-t border-slate-100 bg-slate-50/60">
              {user && (
                <div className="p-2.5 mb-2 rounded-xl bg-white border border-slate-200/80 shadow-2xs flex items-center gap-2.5">
                  <div className="w-8 h-8 rounded-full bg-gradient-to-tr from-amber-500 to-amber-600 text-white font-bold text-xs flex items-center justify-center shrink-0">
                    {(user.fullName || user.username || role || 'A')[0].toUpperCase()}
                  </div>
                  <div className="min-w-0 flex-1 text-left">
                    <div className="text-xs font-bold text-slate-900 truncate">
                      {user.fullName || user.username || user.phoneNumber || 'Administrator'}
                    </div>
                    <div className="text-[10px] font-semibold text-slate-500 truncate">{roleLabel}</div>
                  </div>
                </div>
              )}
              <button
                type="button"
                onClick={handleLogout}
                className="w-full flex items-center justify-center gap-2 px-3 py-2 rounded-xl text-xs font-semibold text-slate-600 hover:text-red-700 hover:bg-red-50 border border-transparent hover:border-red-200 transition-all cursor-pointer"
              >
                <span>🚪</span>
                <span>{t('navigation.logout', 'Logout')}</span>
              </button>
            </div>
          </aside>
        </>
      )}

      {/* Desktop Sticky Sidebar (Strictly in document flow, sticky below navbar) */}
      <aside
        aria-label="Role Navigation Sidebar"
        className={`hidden md:flex md:flex-col bg-white/95 backdrop-blur-md border-r border-slate-200/80 shrink-0 sticky top-16 h-[calc(100vh-4rem)] z-20 transition-all duration-300 ${
          collapsed ? 'w-20' : 'w-64'
        }`}
      >
        {/* Sidebar Header / Role Context */}
        <div className="p-4 border-b border-slate-100 flex items-center justify-between gap-2 min-h-16">
          <div className="flex items-center gap-3 min-w-0">
            <div className="w-9 h-9 rounded-xl bg-gradient-to-br from-amber-400 via-amber-500 to-amber-600 flex items-center justify-center text-white text-lg font-bold shrink-0 shadow-sm shadow-amber-500/20">
              🍯
            </div>
            {!collapsed && (
              <div className="min-w-0">
                <div className="flex items-center gap-1.5">
                  <span className="w-1.5 h-1.5 rounded-full bg-emerald-500 animate-pulse" />
                  <span className="text-[10px] font-black text-slate-400 uppercase tracking-widest truncate">
                    {t('common.controlPortal', 'Control Portal')}
                  </span>
                </div>
                <div className="text-sm font-extrabold text-slate-900 font-['Outfit'] truncate">
                  {roleLabel}
                </div>
              </div>
            )}
          </div>

          {/* Desktop Collapse Toggle */}
          <button
            type="button"
            onClick={() => setCollapsed(!collapsed)}
            className="hidden md:flex p-1.5 rounded-lg text-slate-400 hover:text-slate-700 hover:bg-slate-100 transition-colors cursor-pointer"
            title={collapsed ? t('accessibility.openMenu', 'Expand Sidebar') : t('accessibility.closeMenu', 'Collapse Sidebar')}
            aria-label={collapsed ? t('accessibility.openMenu', 'Expand Sidebar') : t('accessibility.closeMenu', 'Collapse Sidebar')}
          >
            {collapsed ? (
              <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 5l7 7-7 7M5 5l7 7-7 7" />
              </svg>
            ) : (
              <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M11 19l-7-7 7-7m8 14l-7-7 7-7" />
              </svg>
            )}
          </button>
        </div>

        {/* Navigation Section Items */}
        <div className="flex-1 overflow-y-auto p-3 space-y-5">
          {roleNavGroups.map((group, idx) => (
            <div key={idx} className="space-y-1">
              {!collapsed && (
                <div className="px-3 pt-1 text-[10px] font-black text-slate-400 uppercase tracking-widest">
                  {getGroupTitle(group)}
                </div>
              )}
              <div className="space-y-1">
                {group.items.map((item) => {
                  const active = isActiveRoute(item.route)
                  const label = getItemLabel(item)
                  return (
                    <Link
                      key={item.route}
                      to={item.route}
                      onClick={() => setMobileOpen && setMobileOpen(false)}
                      title={collapsed ? label : undefined}
                      aria-current={active ? 'page' : undefined}
                      className={`sidebar-nav-item flex items-center gap-3 py-2.5 px-3 rounded-xl text-xs sm:text-sm ${
                        active ? 'active' : ''
                      } ${collapsed ? 'justify-center px-0' : ''}`}
                    >
                      <span className="text-base leading-none shrink-0">
                        {item.icon}
                      </span>
                      {!collapsed && (
                        <>
                          <span className="truncate flex-1 text-left">
                            {label}
                          </span>
                          {active && (
                            <span className="w-1.5 h-1.5 rounded-full bg-amber-400 shadow-[0_0_6px_#f59e0b] shrink-0" />
                          )}
                        </>
                      )}
                    </Link>
                  )
                })}
              </div>
            </div>
          ))}
        </div>

        {/* Sidebar Footer / User Profile & Logout */}
        <div className="p-3 border-t border-slate-100 bg-slate-50/60">
          {!collapsed && user && (
            <div className="p-2.5 mb-2 rounded-xl bg-white border border-slate-200/80 shadow-2xs flex items-center gap-2.5">
              <div className="w-8 h-8 rounded-full bg-gradient-to-tr from-amber-500 to-amber-600 text-white font-bold text-xs flex items-center justify-center shrink-0 shadow-2xs">
                {(user.fullName || user.username || role || 'A')[0].toUpperCase()}
              </div>
              <div className="min-w-0 flex-1 text-left">
                <div className="text-xs font-bold text-slate-900 truncate">
                  {user.fullName || user.username || user.phoneNumber || 'Administrator'}
                </div>
                <div className="text-[10px] font-semibold text-slate-500 truncate">
                  {roleLabel}
                </div>
              </div>
            </div>
          )}

          <button
            type="button"
            onClick={handleLogout}
            className={`w-full flex items-center justify-center gap-2 px-3 py-2 rounded-xl text-xs font-semibold text-slate-600 hover:text-red-700 hover:bg-red-50 border border-transparent hover:border-red-200 transition-all cursor-pointer ${
              collapsed ? 'px-0' : ''
            }`}
            title={t('navigation.logout', 'Logout')}
            aria-label={t('navigation.logout', 'Logout')}
          >
            <span>🚪</span>
            {!collapsed && <span>{t('navigation.logout', 'Logout')}</span>}
          </button>
        </div>
      </aside>
    </>
  )
}

export default RoleSidebar
