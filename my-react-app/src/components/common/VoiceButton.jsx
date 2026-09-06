import React, { useState, useEffect } from 'react'
import { useLanguage } from '../../i18n/LanguageContext'

export const VoiceButton = ({
  translationKey,
  fallbackText = '',
  params = {},
  customText = '',
  className = '',
  size = 'sm',
}) => {
  const { t, currentLanguageConfig, language } = useLanguage()
  const [isSpeaking, setIsSpeaking] = useState(false)

  useEffect(() => {
    // Warm up Chrome speech synthesis voices
    if (typeof window !== 'undefined' && window.speechSynthesis) {
      window.speechSynthesis.getVoices()
    }
    return () => {
      if (typeof window !== 'undefined' && window.speechSynthesis) {
        window.speechSynthesis.cancel()
      }
    }
  }, [])

  const handleSpeak = () => {
    if (typeof window === 'undefined' || !window.speechSynthesis) {
      alert('Speech synthesis is not supported in this browser.')
      return
    }

    if (isSpeaking) {
      window.speechSynthesis.cancel()
      setIsSpeaking(false)
      return
    }

    let textToSpeak = customText || (translationKey ? t(translationKey, fallbackText || params, params) : fallbackText)

    // Fallback if key itself was returned
    if (!textToSpeak || textToSpeak === translationKey) {
      textToSpeak = fallbackText || 'Welcome to HoneyChain. Direct-from-source honey verification with tamper-proof blockchain integrity.'
    }

    window.speechSynthesis.cancel()
    window.speechSynthesis.resume()

    const utterance = new SpeechSynthesisUtterance(textToSpeak)

    const localeMap = {
      en: 'en-IN',
      hi: 'hi-IN',
      ta: 'ta-IN',
    }
    utterance.lang = localeMap[language] || currentLanguageConfig?.speechLocale || 'en-IN'
    utterance.rate = 0.95
    utterance.pitch = 1.0

    // Chrome voice matching
    const voices = window.speechSynthesis.getVoices()
    if (voices && voices.length > 0) {
      const langPrefix = (utterance.lang || 'en').split('-')[0]
      const matchingVoice = voices.find((v) => v.lang.toLowerCase().includes(langPrefix.toLowerCase()))
      if (matchingVoice) {
        utterance.voice = matchingVoice
      }
    }

    utterance.onstart = () => setIsSpeaking(true)
    utterance.onend = () => setIsSpeaking(false)
    utterance.onerror = (err) => {
      console.warn('[VoiceButton] Speech synthesis notice:', err)
      setIsSpeaking(false)
    }

    setIsSpeaking(true)
    window.speechSynthesis.speak(utterance)
  }

  return (
    <button
      type="button"
      onClick={handleSpeak}
      className={`btn ${isSpeaking ? 'btn--primary' : 'btn--ghost'} btn--${size} inline-flex items-center gap-1.5 ${className}`}
      title={isSpeaking ? t('voice.stop', 'Stop Voice') : t('voice.listen', 'Listen to Voice Instructions 🔊')}
      aria-label={t('voice.listen', 'Listen to Voice Instructions')}
    >
      <span>{isSpeaking ? '🔊' : '🔈'}</span>
      <span className="text-xs font-semibold">
        {isSpeaking ? t('voice.speaking', 'Speaking...') : t('voice.listen', 'Listen')}
      </span>
    </button>
  )
}

export default VoiceButton
