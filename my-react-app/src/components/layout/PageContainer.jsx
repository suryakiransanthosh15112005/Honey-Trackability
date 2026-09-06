import React from 'react'

/**
 * PageContainer — Reusable application-wide content container.
 * Enforces 1440px max-width, margin-inline auto, and responsive 32px padding on desktop.
 */
export const PageContainer = ({ children, maxWidth = '1280px', className = '' }) => {
  const maxWClass =
    maxWidth === '720px'
      ? 'max-w-3xl'
      : maxWidth === '960px'
      ? 'max-w-5xl'
      : 'max-w-7xl'

  return (
    <div className={`w-full ${maxWClass} mx-auto px-5 sm:px-8 lg:px-10 py-8 sm:py-10 space-y-8 ${className}`}>
      {children}
    </div>
  )
}

export default PageContainer
