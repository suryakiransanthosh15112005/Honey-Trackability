import React, { createContext, useContext, useEffect, useCallback } from 'react'
import { useSelector } from 'react-redux'
import { useTranslation } from 'react-i18next'
import {
  LANGUAGES,
  DEFAULT_LANGUAGE,
  mapBackendEnumToCode,
  mapCodeToBackendEnum,
} from './config'
import { formatDate, formatCurrency, formatNumber, formatUnit } from './formatters'
import beekeeperApi from '../features/beekeeper/api/beekeeperApi'

const STORAGE_KEY = 'honeychain_language'

const LanguageContext = createContext()

export const LanguageProvider = ({ children }) => {
  const { isAuthenticated, role } = useSelector((state) => state.auth)
  const { t: i18nT, i18n } = useTranslation()

  const currentLanguage = i18n.language || DEFAULT_LANGUAGE

  // Sync language with Beekeeper Profile on login
  useEffect(() => {
    if (isAuthenticated && (role === 'BEEKEEPER' || role === 'ROLE_BEEKEEPER')) {
      beekeeperApi
        .getProfile()
        .then((res) => {
          const profile = res.data?.data
          if (profile?.preferredLanguage) {
            const langCode = mapBackendEnumToCode(profile.preferredLanguage)
            if (langCode !== i18n.language) {
              i18n.changeLanguage(langCode)
              localStorage.setItem(STORAGE_KEY, langCode)
            }
          }
        })
        .catch((err) => {
          console.warn('[LanguageContext] Profile language sync deferred:', err?.message)
        })
    }
  }, [isAuthenticated, role, i18n])

  const setLanguage = useCallback(
    async (newCode) => {
      if (!LANGUAGES[newCode]) return

      await i18n.changeLanguage(newCode)
      localStorage.setItem(STORAGE_KEY, newCode)

      // Persist to backend profile if logged in as Beekeeper
      if (isAuthenticated && (role === 'BEEKEEPER' || role === 'ROLE_BEEKEEPER')) {
        try {
          const profileRes = await beekeeperApi.getProfile()
          const current = profileRes.data?.data
          if (current) {
            await beekeeperApi.updateProfile({
              name: current.name,
              village: current.village,
              photoUrl: current.photoUrl,
              latitude: current.latitude,
              longitude: current.longitude,
              preferredLanguage: mapCodeToBackendEnum(newCode),
            })
          }
        } catch (err) {
          console.warn('[LanguageContext] Could not update profile preferredLanguage on server:', err?.message)
        }
      }
    },
    [isAuthenticated, role, i18n]
  )

  /**
   * Universal translation helper.
   * Wraps i18next t() with fallback support.
   */
  const t = useCallback(
    (keyPath, fallbackOrParams = '', paramsObj = {}) => {
      let fallbackText = ''
      let options = {}

      if (typeof fallbackOrParams === 'string') {
        fallbackText = fallbackOrParams
        options = paramsObj
      } else if (typeof fallbackOrParams === 'object') {
        options = fallbackOrParams
      }

      const translated = i18nT(keyPath, options)
      if (translated === keyPath && fallbackText) {
        return fallbackText
      }
      return translated
    },
    [i18nT]
  )

  return (
    <LanguageContext.Provider
      value={{
        language: currentLanguage,
        setLanguage,
        t,
        currentLanguageConfig: LANGUAGES[currentLanguage] || LANGUAGES.en,
        formatDate: (d, opts) => formatDate(d, currentLanguage, opts),
        formatCurrency: (amt) => formatCurrency(amt, currentLanguage),
        formatNumber: (num, dec) => formatNumber(num, currentLanguage, dec),
        formatUnit: (val, unitKey) => formatUnit(val, unitKey, currentLanguage),
      }}
    >
      {children}
    </LanguageContext.Provider>
  )
}

export function useLanguage() {
  const ctx = useContext(LanguageContext)
  if (!ctx) {
    throw new Error('useLanguage must be used within a LanguageProvider')
  }
  return ctx
}

export default LanguageContext
