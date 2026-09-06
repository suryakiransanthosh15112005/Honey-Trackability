import React, { useState } from 'react'
import { useSelector } from 'react-redux'
import Navbar from '../common/Navbar'
import RoleSidebar from '../common/RoleSidebar'

export const AppShell = ({ children, stripLabel, stripVariant = 'default' }) => {
  const { isAuthenticated, role } = useSelector((state) => state.auth)
  const [mobileOpen, setMobileOpen] = useState(false)

  const showSidebar = isAuthenticated && Boolean(role)

  return (
    <div className="min-h-screen bg-slate-50/60 flex flex-col font-['Inter']">
      {/* Top Navbar */}
      <Navbar onMobileToggle={() => setMobileOpen(!mobileOpen)} />

      {/* Main Body Shell: Sidebar + Main Content */}
      <div className="flex-1 flex w-full min-h-[calc(100vh-4rem)]">
        {showSidebar && (
          <RoleSidebar mobileOpen={mobileOpen} setMobileOpen={setMobileOpen} />
        )}
        <div className="flex-1 min-w-0 flex flex-col bg-slate-50/40">
          {/* Subtle Portal Context Header / Strip */}
          {stripLabel && (
            <div className="py-3 px-5 sm:px-8 lg:px-10 flex items-center justify-between border-b border-slate-200/60 bg-white/80 backdrop-blur-sm">
              <div className="flex items-center gap-3">
                <span className="w-2.5 h-2.5 rounded-full bg-amber-500 animate-pulse shadow-[0_0_6px_rgba(245,158,11,0.4)]" />
                <span className="text-[11px] font-bold uppercase tracking-widest text-slate-600">
                  {stripLabel}
                </span>
              </div>
              {showSidebar && (
                <button
                  type="button"
                  id="appshell-mobile-sidebar-toggle"
                  onClick={() => setMobileOpen(true)}
                  className="md:hidden flex items-center gap-2 px-3.5 py-2 rounded-xl bg-white border border-slate-200 text-xs font-semibold text-slate-700 hover:bg-slate-50 hover:border-slate-300 shadow-xs transition-all duration-200"
                  aria-label="Open portal navigation"
                >
                  <span className="text-sm">☰</span>
                  <span>Menu</span>
                </button>
              )}
            </div>
          )}

          <main className="app-main-content">
            {children}
          </main>
        </div>
      </div>
    </div>
  )
}

export default AppShell
