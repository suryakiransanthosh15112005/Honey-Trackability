import i18n from 'i18next'
import { initReactI18next } from 'react-i18next'

import enTranslation from './locales/en/translation.json'
import hiTranslation from './locales/hi/translation.json'
import taTranslation from './locales/ta/translation.json'

import { DEFAULT_LANGUAGE } from './config'

const STORAGE_KEY = 'honeychain_language'

const initialLng =
  (typeof window !== 'undefined' && localStorage.getItem(STORAGE_KEY)) ||
  DEFAULT_LANGUAGE

i18n.use(initReactI18next).init({
  resources: {
    en: { translation: enTranslation },
    hi: { translation: hiTranslation },
    ta: { translation: taTranslation },
  },
  lng: initialLng,
  fallbackLng: 'en',
  interpolation: {
    escapeValue: false, // React handles escaping XSS
  },
  react: {
    useSuspense: false,
  },
})

export default i18n
