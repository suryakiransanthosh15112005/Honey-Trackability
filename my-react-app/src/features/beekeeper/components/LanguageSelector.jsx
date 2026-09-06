import React from 'react'

const LANGUAGES = [
  { code: 'ENGLISH', label: 'English', native: 'English', icon: '🌐' },
  { code: 'TAMIL', label: 'Tamil', native: 'தமிழ்', icon: '🍯' },
  { code: 'HINDI', label: 'Hindi', native: 'हिन्दी', icon: '🇮🇳' },
]

export const LanguageSelector = ({ selectedLanguage, onChange }) => {
  return (
    <div className="space-y-3">
      <label className="block text-sm font-semibold text-slate-800">
        Preferred Communication Language *
      </label>
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
        {LANGUAGES.map(({ code, label, native, icon }) => {
          const isSelected = selectedLanguage === code
          return (
            <button
              key={code}
              type="button"
              onClick={() => onChange(code)}
              className={`p-4 rounded-2xl border text-left flex items-center justify-between transition-all ${isSelected
                  ? 'border-blue-500 bg-blue-50 shadow-sm font-semibold'
                  : 'border-slate-200 bg-white hover:border-slate-300'
                }`}
            >
              <div>
                <span className="text-xl mr-2">{icon}</span>
                <span className="font-semibold text-slate-900 text-base font-['Outfit']">{label}</span>
                <p className="text-slate-500 text-xs mt-0.5">{native}</p>
              </div>
              <div
                className={`w-5 h-5 rounded-full border flex items-center justify-center text-xs ${isSelected ? 'border-blue-600 bg-blue-600 text-white font-bold' : 'border-slate-300'
                  }`}
              >
                {isSelected && '✓'}
              </div>
            </button>
          )
        })}
      </div>
    </div>
  )
}

export default LanguageSelector
