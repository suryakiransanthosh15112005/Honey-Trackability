import React from 'react'
import { useSelector } from 'react-redux'
import Navbar from '../components/common/Navbar'
import Footer from '../components/common/Footer'
import AppShell from '../components/layout/AppShell'

const MainLayout = ({ children, transparentNav = false }) => {
  return (
    <div className="layout min-h-screen flex flex-col bg-slate-50">
      <Navbar transparent={transparentNav} />
      <main className="flex-1 w-full">
        {children}
      </main>
      <Footer />
    </div>
  )
}

export default MainLayout
