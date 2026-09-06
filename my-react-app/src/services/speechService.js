import { LANGUAGES } from '../i18n/languageConfig'

let activeUtterance = null
let currentOnEnd = null

export const speechService = {
  /**
   * Check if browser supports SpeechSynthesis API.
   * @returns {boolean}
   */
  isSupported() {
    return typeof window !== 'undefined' && 'speechSynthesis' in window
  },

  /**
   * Read text aloud using browser SpeechSynthesis.
   *
   * @param {string} text - Text to read aloud.
   * @param {string} langCode - Language code ('en', 'ta', 'hi').
   * @param {object} options - Callbacks like onEnd, onStart, onError.
   */
  speak(text, langCode = 'en', options = {}) {
    if (!this.isSupported()) {
      console.warn('[SpeechService] SpeechSynthesis is not supported on this browser.')
      options.onError?.('SpeechSynthesis not supported')
      return false
    }

    if (!text || !text.trim()) {
      return false
    }

    // Stop any ongoing speech first to prevent overlap
    this.stop()

    const config = LANGUAGES[langCode] || LANGUAGES.en
    const speechLocale = config.speechLocale || 'en-IN'

    const utterance = new SpeechSynthesisUtterance(text)
    utterance.lang = speechLocale
    utterance.rate = 0.9 // Comfortable reading speed
    utterance.pitch = 1.0

    // Try to find a matching voice for the target locale
    const voices = window.speechSynthesis.getVoices()
    if (voices && voices.length > 0) {
      const matchingVoice =
        voices.find((v) => v.lang === speechLocale) ||
        voices.find((v) => v.lang.startsWith(langCode)) ||
        voices.find((v) => v.lang.includes(langCode))

      if (matchingVoice) {
        utterance.voice = matchingVoice
      }
    }

    utterance.onstart = () => {
      options.onStart?.()
    }

    utterance.onend = () => {
      activeUtterance = null
      currentOnEnd = null
      options.onEnd?.()
    }

    utterance.onerror = (e) => {
      console.warn('[SpeechService] Utterance error:', e)
      activeUtterance = null
      currentOnEnd = null
      options.onEnd?.()
    }

    activeUtterance = utterance
    currentOnEnd = options.onEnd

    window.speechSynthesis.speak(utterance)
    return true
  },

  /**
   * Stop current speech playback immediately.
   */
  stop() {
    if (this.isSupported() && window.speechSynthesis.speaking) {
      window.speechSynthesis.cancel()
    }
    if (currentOnEnd) {
      const cb = currentOnEnd
      currentOnEnd = null
      activeUtterance = null
      cb()
    } else {
      activeUtterance = null
    }
  },

  /**
   * Check if speech is currently playing.
   * @returns {boolean}
   */
  isSpeaking() {
    return this.isSupported() && window.speechSynthesis.speaking
  },
}

export default speechService
