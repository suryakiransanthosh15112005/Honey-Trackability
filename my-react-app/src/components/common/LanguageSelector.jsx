import React from 'react'
import { useLanguage } from '../../i18n/LanguageContext'
import { LANGUAGES } from '../../i18n/languageConfig'

export const LanguageSelector = ({ variant = 'select', className = '' }) => {
  const { language, setLanguage } = useLanguage()

  if (variant === 'buttons') {
    return (
      <div className={`flex items-center gap-1 bg-slate-100 p-1 rounded-xl border border-slate-200 ${className}`}>
        {Object.values(LANGUAGES).map((lang) => (
          <button
            key={lang.code}
            type="button"
            className={`px-2.5 py-1 rounded-lg text-xs font-semibold transition-all ${
              language === lang.code
                ? 'bg-primary text-white shadow-sm font-bold'
                : 'text-slate-600 hover:text-slate-900 hover:bg-slate-200'
            }`}
            onClick={() => setLanguage(lang.code)}
          >
            <span className="mr-1">{lang.flag}</span>
            <span>{lang.nativeName}</span>
          </button>
        ))}
      </div>
    )
  }

  return (
    <div className={`relative inline-block ${className}`}>
      <select
        id="language-select"
        className="form-input text-xs py-1.5 px-3 bg-white border border-slate-200 text-slate-800 font-semibold rounded-lg cursor-pointer hover:border-primary transition-colors shadow-sm"
        value={language}
        onChange={(e) => setLanguage(e.target.value)}
        aria-label="Select Interface Language"
      >
        {Object.values(LANGUAGES).map((lang) => (
          <option key={lang.code} value={lang.code} className="bg-white text-slate-800 py-1">
            {lang.flag} {lang.nativeName} ({lang.label})
          </option>
        ))}
      </select>
    </div>
  )
}

export default LanguageSelector
