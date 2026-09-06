import AppShell from '../components/layout/AppShell'

export const AdminLayout = ({ children }) => {
  return (
    <AppShell>
      {children}
    </AppShell>
  )
}

export default AdminLayout
