import { LANGUAGES, DEFAULT_LANGUAGE } from './config'

/**
 * Centralized Date Formatter
 * Formats a Date or ISO string according to the selected language locale.
 */
export function formatDate(dateValue, lng = DEFAULT_LANGUAGE, options = {}) {
  if (!dateValue) return ''
  try {
    const d = new Date(dateValue)
    if (isNaN(d.getTime())) return String(dateValue)

    const localeMap = {
      en: 'en-IN',
      hi: 'hi-IN',
      ta: 'ta-IN',
    }

    const targetLocale = localeMap[lng] || 'en-IN'

    const defaultOptions = {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      ...options,
    }

    return new Intl.DateTimeFormat(targetLocale, defaultOptions).format(d)
  } catch (err) {
    console.warn('[Formatters] formatDate error:', err)
    return String(dateValue)
  }
}

/**
 * Centralized Currency Formatter (INR)
 */
export function formatCurrency(amount, lng = DEFAULT_LANGUAGE) {
  if (amount === null || amount === undefined || isNaN(amount)) return '₹0.00'
  try {
    const localeMap = {
      en: 'en-IN',
      hi: 'hi-IN',
      ta: 'ta-IN',
    }
    const targetLocale = localeMap[lng] || 'en-IN'

    return new Intl.NumberFormat(targetLocale, {
      style: 'currency',
      currency: 'INR',
      maximumFractionDigits: 2,
    }).format(Number(amount))
  } catch (err) {
    return `₹${Number(amount).toFixed(2)}`
  }
}

/**
 * Centralized Number Formatter
 */
export function formatNumber(num, lng = DEFAULT_LANGUAGE, maxDecimals = 2) {
  if (num === null || num === undefined || isNaN(num)) return '0'
  try {
    const localeMap = {
      en: 'en-IN',
      hi: 'hi-IN',
      ta: 'ta-IN',
    }
    const targetLocale = localeMap[lng] || 'en-IN'

    return new Intl.NumberFormat(targetLocale, {
      maximumFractionDigits: maxDecimals,
    }).format(Number(num))
  } catch (err) {
    return String(num)
  }
}

/**
 * Centralized Unit Formatter
 */
export function formatUnit(value, unitKey, lng = DEFAULT_LANGUAGE) {
  const formattedNum = formatNumber(value, lng)
  const unitLabels = {
    kg: { en: 'kg', hi: 'किग्रा', ta: 'கிலோ' },
    hives: { en: 'hives', hi: 'छत्ते', ta: 'தேனீக் கூடுகள்' },
    days: { en: 'days', hi: 'दिन', ta: 'நாட்கள்' },
    daysAgo: { en: 'days ago', hi: 'दिन पहले', ta: 'நாட்களுக்கு முன்பு' },
    confidencePercent: { en: '% confidence', hi: '% विश्वास', ta: '% நம்பகத்தன்மை' },
    percentage: { en: '%', hi: '%', ta: '%' },
    celsius: { en: '°C', hi: '°C', ta: '°C' },
  }

  const label = unitLabels[unitKey]?.[lng] || unitLabels[unitKey]?.en || unitKey
  return `${formattedNum} ${label}`
}
