import React from 'react'
import { Link } from 'react-router-dom'
import { useSelector } from 'react-redux'
import { ROLE_ROUTES, ROLE_LABELS } from '../../constants/roles'
import Button from '../ui/Button'
import Card from '../ui/Card'
import AppShell from '../layout/AppShell'

export const ForbiddenPage = () => {
  const { role, isAuthenticated } = useSelector((state) => state.auth)

  const dashboardRoute = isAuthenticated && role ? ROLE_ROUTES[role] || '/' : '/login'
  const userRoleLabel = isAuthenticated && role ? ROLE_LABELS[role] || role : 'Guest'

  const content = (
    <div className="min-h-[70vh] flex items-center justify-center p-6">
      <Card className="max-w-md w-full text-center p-8 space-y-6 bg-white border border-slate-200 shadow-sm rounded-xl">
        <div className="w-16 h-16 rounded-full bg-slate-100 border border-slate-200 text-slate-800 flex items-center justify-center text-3xl mx-auto">
          🛡️
        </div>
        <div className="space-y-2">
          <h1 className="text-2xl font-extrabold text-slate-900 font-['Outfit']">
            403 — Access Denied
          </h1>
          <p className="text-sm text-slate-600 leading-relaxed">
            You do not have permission to access this page. Your account role is logged in as{' '}
            <span className="font-semibold text-primary">{userRoleLabel}</span>.
          </p>
        </div>
        <div className="pt-2 flex flex-col gap-3">
          <Link to={dashboardRoute}>
            <Button variant="primary" className="w-full justify-center">
              Go to Your Role Dashboard
            </Button>
          </Link>
          <Link to="/">
            <Button variant="secondary" className="w-full justify-center">
              Back to Home Page
            </Button>
          </Link>
        </div>
      </Card>
    </div>
  )

  if (isAuthenticated && role) {
    return (
      <AppShell stripLabel="Access Denied" stripVariant="default">
        {content}
      </AppShell>
    )
  }

  return content
}

export default ForbiddenPage
