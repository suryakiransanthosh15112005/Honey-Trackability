import React from 'react'
import AppShell from '../components/layout/AppShell'
import { useLanguage } from '../i18n/LanguageContext'

export const BeekeeperLayout = ({ children }) => {
  const { t } = useLanguage()

  return (
    <AppShell
      stripLabel={t('navigation.portalLabel', 'Beekeeper Management Portal')}
      stripVariant="beekeeper"
    >
      {children}
    </AppShell>
  )
}

export default BeekeeperLayout
