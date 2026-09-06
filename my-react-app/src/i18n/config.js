export const LANGUAGES = {
  en: {
    code: 'en',
    backendEnum: 'ENGLISH',
    label: 'English',
    nativeName: 'English',
    speechLocale: 'en-IN',
    flag: '🇬🇧',
  },
  hi: {
    code: 'hi',
    backendEnum: 'HINDI',
    label: 'Hindi',
    nativeName: 'हिन्दी',
    speechLocale: 'hi-IN',
    flag: '🇮🇳',
  },
  ta: {
    code: 'ta',
    backendEnum: 'TAMIL',
    label: 'Tamil',
    nativeName: 'தமிழ்',
    speechLocale: 'ta-IN',
    flag: '🇮🇳',
  },
}

export const DEFAULT_LANGUAGE = 'en'

export function mapBackendEnumToCode(backendEnum) {
  if (!backendEnum) return DEFAULT_LANGUAGE
  const upper = String(backendEnum).toUpperCase()
  if (upper === 'TAMIL') return 'ta'
  if (upper === 'HINDI') return 'hi'
  return 'en'
}

export function mapCodeToBackendEnum(code) {
  if (code === 'ta') return 'TAMIL'
  if (code === 'hi') return 'HINDI'
  return 'ENGLISH'
}
