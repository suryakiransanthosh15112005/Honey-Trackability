/**
 * Utility helpers for HoneyChain frontend
 */

/** Format a date string to a readable format */
export const formatDate = (dateString) => {
  if (!dateString) return '—'
  return new Date(dateString).toLocaleDateString('en-IN', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
  })
}

/** Truncate a blockchain hash for display */
export const truncateHash = (hash, length = 8) => {
  if (!hash) return '—'
  return `${hash.slice(0, length)}...${hash.slice(-length)}`
}

/** Get role display label */
export const getRoleLabel = (role) => {
  const labels = {
    BEEKEEPER: 'Beekeeper',
    CUSTOMER: 'Customer',
    LAB: 'Lab Technician',
    ADMIN: 'Administrator',
  }
  return labels[role] || role
}

/** Get dashboard route by role */
export const getDashboardRoute = (role) => {
  const routes = {
    BEEKEEPER: '/beekeeper/dashboard',
    CUSTOMER: '/customer/dashboard',
    LAB: '/lab/dashboard',
    ADMIN: '/admin/dashboard',
  }
  return routes[role] || '/login'
}
