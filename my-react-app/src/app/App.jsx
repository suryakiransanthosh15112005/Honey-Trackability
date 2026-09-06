import React from 'react'
import { BrowserRouter } from 'react-router-dom'
import { Provider } from 'react-redux'
import { store } from './store'
import { LanguageProvider } from '../i18n/LanguageContext'
import AppRouter from './router'

export function App() {
  return (
    <Provider store={store}>
      <LanguageProvider>
        <BrowserRouter>
          <AppRouter />
        </BrowserRouter>
      </LanguageProvider>
    </Provider>
  )
}

export default App
