import React from 'react'
import AppShell from '../components/layout/AppShell'

export const CustomerLayout = ({ children }) => {
  return (
    <AppShell stripLabel="Customer Marketplace Portal" stripVariant="customer">
      {children}
    </AppShell>
  )
}

export default CustomerLayout
