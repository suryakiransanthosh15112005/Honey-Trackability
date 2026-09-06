import React from 'react'
import AppShell from '../components/layout/AppShell'

export const LabLayout = ({ children }) => {
  return (
    <AppShell stripLabel="Laboratory & Testing Portal" stripVariant="lab">
      {children}
    </AppShell>
  )
}

export default LabLayout
